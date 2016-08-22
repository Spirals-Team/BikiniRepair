package fr.inria.lille.spirals.bikinirepair.oracle.regression;

import fr.inria.lille.spirals.bikinirepair.shadower.ShadowRequest;
import fr.inria.lille.spirals.bikinirepair.shadower.ShadowResponse;

import java.util.Arrays;

public class RegressionOracleImpl implements RegressionOracle{

	@Override
	public boolean isValid(ShadowRequest request, ShadowResponse responseOrigin, ShadowResponse responseRegression) {
		if (responseOrigin == null) {
			if (responseRegression == null) {
				return true;
			}
			return false;
		}
		if (responseRegression == null) {
			return false;
		}
		return Arrays.equals(responseOrigin.getBody(), responseRegression.getBody());
	}
}

