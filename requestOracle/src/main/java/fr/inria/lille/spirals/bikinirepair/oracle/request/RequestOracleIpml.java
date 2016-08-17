package fr.inria.lille.spirals.bikinirepair.oracle.request;

import fr.inria.lille.spirals.bikinirepair.ShadowRequest;
import fr.inria.lille.spirals.bikinirepair.ShadowResponse;

public class RequestOracleIpml implements RequestOracle {

	public boolean isValid(ShadowRequest request, ShadowResponse response) {
		if (response == null) {
			return false;
		}
		return response.getStatus() != 500;
	}
}

