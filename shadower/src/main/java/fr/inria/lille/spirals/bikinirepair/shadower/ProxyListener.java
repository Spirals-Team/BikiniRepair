package fr.inria.lille.spirals.bikinirepair.shadower;

import fr.inria.lille.spirals.bikinirepair.ShadowRequest;
import fr.inria.lille.spirals.bikinirepair.ShadowResponse;

/**
 * Created by thomas on 02/08/16.
 */
public interface ProxyListener {
	void endRequest(ShadowRequest request, ShadowResponse response);

	void startRequest(ShadowRequest output);
}
