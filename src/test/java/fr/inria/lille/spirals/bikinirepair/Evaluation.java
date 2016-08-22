package fr.inria.lille.spirals.bikinirepair;

import fr.inria.lille.spirals.bikinirepair.docker.DockerImage;
import fr.inria.lille.spirals.bikinirepair.docker.DockerRunner;
import fr.inria.lille.spirals.bikinirepair.services.Services;
import fr.inria.spirals.npefix.resi.selector.ExplorerSelector;
import org.junit.Test;

public class Evaluation {

	private String applicationImageName = "shadow-dataset:mayocat_231";
	private String regressionImageName = "shadow-dataset:mayocat_231_npefix";
	private String patchImageName = "shadow-dataset:mayocat_231_npefix";

	public static void main(String[] args) throws Exception {
		new Evaluation().startServices();
	}
	@Test
	public void test() throws Exception {
		startServices();
	}

	protected DockerRunner startServices() throws Exception {
		System.out.println("Start Services");
		Services.createAddressServer();
		Services.createRegressionServer();
		Services.createPatchServer(new ExplorerSelector());


		DockerRunner runner = new DockerRunner();

		DockerImage applicationImage = new DockerImage(applicationImageName);
		DockerImage patchImage = new DockerImage(patchImageName);

		System.out.println("Start Unmodified application");
		runner.run(applicationImage);
		System.out.println("Start Patch Generation Service");
		runner.runWithArgs(patchImage, "PatchServer");

		Services.getAddressServer().setApplicationIp(applicationImage.getIp());
		Services.getAddressServer().setApplicationPort(8080);
		Services.getAddressServer().setPatchIp(patchImage.getIp());

		/*Shadower shadower = new Shadower(
				applicationImage.getIp(),
				regressionImage.getIp(),
				patchImage.getIp(),
				8080);
		shadower.start();*/
		return runner;
	}


}