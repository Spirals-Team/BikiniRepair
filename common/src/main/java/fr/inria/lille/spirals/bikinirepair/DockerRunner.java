package fr.inria.lille.spirals.bikinirepair;

import java.util.ArrayList;
import java.util.List;

public class DockerRunner {
	List<DockerImage> containers = new ArrayList<>();

	public DockerRunner() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				stopAll();
			}
		});
	}

	public void run(DockerImage... images) {
		for (int i = 0; i < images.length; i++) {
			DockerImage image = images[i];
			containers.add(image);
			image.run(false);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop(DockerImage dockerImage) {
		dockerImage.stop();
		containers.remove(containers);
	}

	public void stopAll() {
		for (int i = 0; i < containers.size(); i++) {
			DockerImage dockerImage = containers.get(i);
			dockerImage.stop();
		}
		containers.clear();
	}


}