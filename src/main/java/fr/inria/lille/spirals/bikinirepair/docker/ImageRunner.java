package fr.inria.lille.spirals.bikinirepair.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.PortBinding;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ImageRunner {
	private final DefaultDockerClient docker;
	private final String imageId;
	private String containerId;

	public ImageRunner(String imageId) {
		this.imageId = imageId;
		try {
			docker = DefaultDockerClient.fromEnv().readTimeoutMillis( Long.MAX_VALUE ).build();
		} catch (DockerCertificateException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Run the image and wait the end of the execution
	 */
	public void run() {
		run(true);
	}

	/**
	 * Run the image and wait or not the end of the execution of the image
	 * @param wait
	 */
	public void run(boolean wait, String...args) {
		stop();
		try {
			ContainerConfig containerConfig = ContainerConfig.builder()
					.image(imageId)
					.cmd(args)
					.build();
			ContainerCreation container = docker.createContainer(
					containerConfig);
			containerId = container.id();
			docker.startContainer(containerId);

			System.out.println("IP: " + docker.inspectContainer(containerId).networkSettings().ipAddress());
			Map<String, List<PortBinding>> ports = docker.inspectContainer(containerId).networkSettings().ports();
			String port = "";
			for (Iterator<String> iterator = ports.keySet().iterator(); iterator.hasNext(); ) {
				String next = iterator.next();
				port += next + ", ";
			}
			System.out.println("Port: " + port);


			if (wait) {
				docker.waitContainer(containerId);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Stop the running container
	 */
	public void stop() {
		try {
			if (containerId != null) {
				// Kill container
				docker.killContainer(containerId);
				// Remove container
				docker.removeContainer(containerId);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Kill docker and the container
	 */
 	public void kill() {
		try {
			stop();
			// Close the docker client
			docker.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public DefaultDockerClient getDocker() {
		return docker;
	}

	public ContainerInfo inspect() {
		try {
			return docker.inspectContainer(containerId);
		} catch (Exception e) {
			return null;
		}
	}
}