package fr.inria.lille.spirals.bikinirepair.oracle.request;

import fr.inria.lille.spirals.bikinirepair.ShadowRequest;
import fr.inria.lille.spirals.bikinirepair.ShadowResponse;

public interface RequestOracle {

	boolean isValid(ShadowRequest request, ShadowResponse response);
}

