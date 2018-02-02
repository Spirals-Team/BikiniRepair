package fr.inria.lille.spirals.bikinirepair.oracle.regression;

import fr.inria.lille.spirals.bikinirepair.shadower.ShadowRequest;
import fr.inria.lille.spirals.bikinirepair.shadower.ShadowResponse;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		if (request.getRequestURI().endsWith("cart")) {
			Pattern p = Pattern.compile("total</h3>.*itemTotal.*([0-9]+)€", Pattern.CASE_INSENSITIVE + Pattern.COMMENTS + Pattern.DOTALL);
			Matcher m = p.matcher(responseOrigin.getHtmlBody());
			boolean b = m.find();
			if (b) {
				String priceOriginal = m.group(1);
				m = p.matcher(responseRegression.getHtmlBody());
				b = m.find();
				if (b) {
					System.out.println(priceOriginal + " " + m.group(1));
					return priceOriginal.equals(m.group(1));
				}
			}

		}
		return Arrays.equals(responseOrigin.getBody(), responseRegression.getBody());
	}
}

