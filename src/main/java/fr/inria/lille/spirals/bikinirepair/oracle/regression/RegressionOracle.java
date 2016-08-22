package fr.inria.lille.spirals.bikinirepair.oracle.regression;

import fr.inria.lille.spirals.bikinirepair.shadower.ShadowRequest;
import fr.inria.lille.spirals.bikinirepair.shadower.ShadowResponse;

public interface RegressionOracle {

	boolean isValid(ShadowRequest request, ShadowResponse responseOrigin, ShadowResponse responseRegression);
}

