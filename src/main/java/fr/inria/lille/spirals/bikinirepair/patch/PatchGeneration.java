package fr.inria.lille.spirals.bikinirepair.patch;

import fr.inria.lille.spirals.bikinirepair.oracle.request.RequestOracleIpml;
import fr.inria.lille.spirals.bikinirepair.services.Services;
import fr.inria.lille.spirals.bikinirepair.services.ServicesType;
import fr.inria.lille.spirals.bikinirepair.shadower.ShadowRequest;
import fr.inria.lille.spirals.bikinirepair.shadower.ShadowResponse;
import fr.inria.spirals.npefix.resi.context.Lapse;
import fr.inria.spirals.npefix.resi.oracle.AbstractOracle;
import fr.inria.spirals.npefix.resi.oracle.Oracle;
import fr.inria.spirals.npefix.resi.selector.Selector;

import java.io.Serializable;

public class PatchGeneration implements Serializable {

	private String proxyCookies;

	public PatchGeneration(String proxyCookies) {

		this.proxyCookies = proxyCookies;
	}

	public void run(ShadowRequest request) {
		try {
			Selector selector = Services.getPatchServer();

			int countWithoutPatch = 0;
			while (countWithoutPatch < 3) {
				Lapse lapse = new Lapse(selector);
				lapse.setTestClassName(request.getRequestURI());
				lapse.setTestName(request.getParameters() + "");

				selector.startLaps(lapse);

				ShadowResponse validationResponse = request.execute(ServicesType.PATCH, proxyCookies);
				lapse = Services.getPatchServer().getCurrentLapse();
				boolean isValidRequest = new RequestOracleIpml().isValid(request, validationResponse);
				Oracle oracle = new AbstractOracle("Request", isValidRequest);
				lapse.setOracle(oracle);
				Services.getPatchServer().updateCurrentLapse(lapse);
				selector.restartTest(lapse);

				if (isValidRequest) {

				}
				if (lapse.getDecisions().isEmpty()) {
					countWithoutPatch ++;
				}
 			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

