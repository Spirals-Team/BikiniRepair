package fr.inria.lille.spirals.bikinirepair.regression;

import java.io.Serializable;

public class RegressionRequest implements Serializable {

	private static int ids = 1;

	/**
	 * The unique id of the regression request
	 */
	private final int id;
	private String patch;

	public RegressionRequest(final String patch) {
		this.patch = patch;
		id = ids++;
	}

}

