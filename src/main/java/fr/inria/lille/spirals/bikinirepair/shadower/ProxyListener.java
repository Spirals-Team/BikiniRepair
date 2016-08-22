package fr.inria.lille.spirals.bikinirepair.shadower;

/**
 * Created by thomas on 02/08/16.
 */
public interface ProxyListener {
	void endRequest(ShadowRequest request, ShadowResponse response);

	void startRequest(ShadowRequest output);
}
