package fr.inria.lille.spirals.bikinirepair.oracle.request;

import fr.inria.lille.spirals.bikinirepair.shadower.ShadowRequest;
import fr.inria.lille.spirals.bikinirepair.shadower.ShadowResponse;

public class RequestOracleIpml implements RequestOracle {

	public boolean isValid(ShadowRequest request, ShadowResponse response) {
		if (response == null) {
			return false;
		}
		return response.getStatus() != 500;
	}
}

