package fr.inria.lille.spirals.bikinirepair.regression;

import fr.inria.lille.spirals.bikinirepair.docker.DockerImage;
import fr.inria.lille.spirals.bikinirepair.oracle.regression.RegressionOracleImpl;
import fr.inria.lille.spirals.bikinirepair.oracle.request.RequestOracleIpml;
import fr.inria.lille.spirals.bikinirepair.services.Services;
import fr.inria.lille.spirals.bikinirepair.services.ServicesType;
import fr.inria.lille.spirals.bikinirepair.shadower.ShadowRequest;
import fr.inria.lille.spirals.bikinirepair.shadower.ShadowResponse;
import fr.inria.spirals.npefix.resi.context.Decision;
import fr.inria.spirals.npefix.resi.context.Lapse;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class PatchValidation implements Serializable {

	public PatchValidation() {

	}

	public void run(ShadowRequest request, ShadowResponse expectedResponse, String image) {
		List<Decision> allDecisions = new ArrayList<>();
		try {
			List<Lapse> lapses = fr.inria.lille.spirals.bikinirepair.services.Services
					.getPatchServer().getLapses();
			for (int i = 0; i < lapses.size(); i++) {
				Lapse lapse =  lapses.get(i);
				if (lapse.getOracle().isValid()) {
					List<Decision> decisions = lapse.getDecisions();
					allDecisions.addAll(decisions);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < allDecisions.size(); i++) {
			Decision decision = allDecisions.get(i);


			DockerImage docker = null;
			try {
				Services.getRegression().setDecision(decision);

				docker = new DockerImage(image);
				docker.run(false, "regression");
				Services.getAddressServer().setRegressionIp(docker.getIp());
				try {
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				ShadowResponse validationResponse = request.execute(ServicesType.VALIDATION, null);
				docker.stop();

				boolean isValidRequest = new RequestOracleIpml().isValid(request, validationResponse);
				if (isValidRequest) {
					boolean isValid = new RegressionOracleImpl().isValid(request, expectedResponse, validationResponse);
					if (isValid) {
						// TODO: save the result
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (docker != null) {
					docker.stop();
				}
			}
		}
	}
}

