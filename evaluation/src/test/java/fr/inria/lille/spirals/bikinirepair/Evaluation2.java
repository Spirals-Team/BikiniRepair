package fr.inria.lille.spirals.bikinirepair;

import fr.inria.lille.spirals.bikinirepair.shadower.Shadower;

public class Evaluation2 {

	private String applicationImageName = "shadow-dataset:mayocat_231";
	private String regressionImageName = "shadow-dataset:mayocat_231_npefix";
	private String patchImageName = "shadow-dataset:mayocat_231_npefix";

	public static void main(String[] args) throws Exception {
		new Evaluation2().startServices();
	}

	protected void startServices() throws Exception {
		Shadower shadower = new Shadower(
				"172.17.0.109",
				regressionImageName,
				"172.17.0.111",
				8080);
		shadower.start();
	}


}