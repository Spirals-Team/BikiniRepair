package fr.inria.lille.spirals.bikinirepair.regression;

import fr.inria.lille.spirals.bikinirepair.docker.DockerImage;
import fr.inria.lille.spirals.bikinirepair.docker.DockerRunner;
import fr.inria.lille.spirals.bikinirepair.oracle.regression.RegressionOracleImpl;
import fr.inria.lille.spirals.bikinirepair.services.Services;
import fr.inria.lille.spirals.bikinirepair.services.ServicesType;
import fr.inria.lille.spirals.bikinirepair.shadower.ShadowRequest;
import fr.inria.lille.spirals.bikinirepair.shadower.ShadowResponse;
import fr.inria.spirals.npefix.resi.context.Lapse;
import fr.inria.spirals.npefix.resi.oracle.AbstractOracle;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PatchValidation implements Serializable {

	public static Map<Lapse, List<RegressionRequest>>  outputs = new HashMap<>();
	private final DockerRunner runner;

	public PatchValidation() {
		this.runner = new DockerRunner();
	}

	public synchronized void run(ShadowRequest request, ShadowResponse expectedResponse, String image, String cookies) {
		HashSet<Lapse> allLapses = new HashSet<>();
		try {
			List<Lapse> lapses = fr.inria.lille.spirals.bikinirepair.services.Services.getPatchServer().getLapses();
			allLapses.addAll(lapses);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (request.getMethod().toUpperCase().equals("POST")) {
			return;
		}
		if (!expectedResponse.getContentType().endsWith("html")) {
			return;
		}

		DockerImage docker = null;
		try {
			int countValid = 0;
			for (Iterator<Lapse> iterator = allLapses.iterator(); iterator
					.hasNext(); ) {
				Lapse lapse = iterator.next();
				if (!lapse.getOracle().isValid()) {
					continue;
				}
				try {
					Lapse validationLapse = new Lapse(Services.getRegression());

					Services.getRegression().startLaps(validationLapse);
					Services.getRegression().setLapse(lapse);

					ShadowResponse validationResponse = request.execute(ServicesType.VALIDATION, cookies);

					boolean isValid = new RegressionOracleImpl().isValid(request, expectedResponse, validationResponse);
					validationLapse = Services.getRegression().getCurrentLapse();

					validationLapse.setOracle(new AbstractOracle("Regression", isValid));
					validationLapse = Services.getRegression().updateCurrentLapse(validationLapse);

					Services.getRegression().restartTest(validationLapse);

					validationLapse = Services.getRegression().getCurrentLapse();

					RegressionRequest output = new RegressionRequest();
					output.setRequest(request);
					output.setResponse(validationResponse);
					output.setValidity(isValid);
					output.setLapse(validationLapse);

					if (!outputs.containsKey(lapse)) {
						outputs.put(lapse, new ArrayList<RegressionRequest>());
					}
					outputs.get(lapse).add(output);

					if (isValid) {
						System.out.println(validationLapse);
						countValid ++;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("# lapse: " + allLapses.size());
			System.out.println("# valid lapse: " + countValid);
			System.out.println("# invalid lapse: " + (allLapses.size() - countValid));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (docker != null) {
				runner.stop(docker);
			}
		}
	}
}

