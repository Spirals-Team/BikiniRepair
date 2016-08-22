package fr.inria.lille.spirals.bikinirepair.oracle.request;

import fr.inria.lille.spirals.bikinirepair.shadower.ShadowRequest;
import fr.inria.lille.spirals.bikinirepair.shadower.ShadowResponse;

public interface RequestOracle {

	boolean isValid(ShadowRequest request, ShadowResponse response);
}

