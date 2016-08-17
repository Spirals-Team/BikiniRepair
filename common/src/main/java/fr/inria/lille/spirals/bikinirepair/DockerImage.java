package fr.inria.lille.spirals.bikinirepair;

public class DockerImage {
	private final String imageName;
	private ImageRunner imageRunner;

	public DockerImage(String imageName) {
		this.imageName = imageName;
	}

	public void run() {
		run(true);
	}

	public void run(boolean wait, String...args) {
		imageRunner = new ImageRunner(imageName);
		imageRunner.run(wait, args);
	}

	public void stop() {
		if (imageRunner != null) {
			imageRunner.kill();
			imageRunner = null;
		}
	}

	public String getIp() {
		return imageRunner.inspect().networkSettings().ipAddress();
	}
}