package fr.inria.lille.spirals.bikinirepair.shadower;

import fr.inria.lille.spirals.bikinirepair.services.ServicesType;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.util.Callback;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ProxyServlet extends org.eclipse.jetty.proxy.ProxyServlet {

	private List<ProxyListener> listeners = new ArrayList<ProxyListener>();
	private Map<HttpServletRequest, ShadowRequest> outputs = new HashMap<>();

	@Override
	protected synchronized void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// create the shadow request
		ShadowRequest shadowRequest = ShadowRequestFactory.create(request);


		outputs.put(request, shadowRequest);

		// create the new request target
		String rewrittenTarget = shadowRequest.getTarget(ServicesType.APPLICATION);
		if(rewrittenTarget == null) {
			this.onProxyRewriteFailed(request, response);
		} else {
			Request proxyRequest = this.getHttpClient().newRequest(rewrittenTarget).method(request.getMethod()).version(HttpVersion.fromString(request.getProtocol()));
			if(shadowRequest.getContent() != null) {
				proxyRequest.content(new BytesContentProvider(shadowRequest.getContent()));
			}
			this.copyRequestHeaders(request, proxyRequest);
			this.addProxyHeaders(request, proxyRequest);
			AsyncContext asyncContext = request.startAsync();
			asyncContext.setTimeout(0L);
			proxyRequest.timeout(this.getTimeout(), TimeUnit.MILLISECONDS);

			this.sendProxyRequest(request, response, proxyRequest);
		}

		notifyNewRequest(shadowRequest);
	}

	@Override
	protected String rewriteTarget(HttpServletRequest request) {
		return ShadowRequestFactory.create(request).getTarget(ServicesType.APPLICATION);
	}

	@Override
	protected void onResponseContent(HttpServletRequest request,
			HttpServletResponse response, Response proxyResponse, byte[] buffer,
			int offset, int length, Callback callback) {
		outputs.get(request).addBody(length, buffer);

		super.onResponseContent(request, response, proxyResponse, buffer, offset, length, callback);
	}

	@Override
	protected void onClientRequestFailure(HttpServletRequest clientRequest,
			Request proxyRequest, HttpServletResponse proxyResponse,
			Throwable failure) {
		ShadowResponse shadowResponse = ShadowResponseFactory.create(proxyResponse);
		super.onClientRequestFailure(clientRequest, proxyRequest, proxyResponse, failure);

		notifyEndRequest(outputs.get(clientRequest), shadowResponse);
	}

	@Override
	protected void onProxyResponseSuccess(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Response serverResponse) {
		ShadowResponse shadowResponse = ShadowResponseFactory.create(proxyResponse);
		super.onProxyResponseSuccess(clientRequest, proxyResponse, serverResponse);
		shadowResponse.setBody(outputs.get(clientRequest).getBody());
		notifyEndRequest(outputs.get(clientRequest), shadowResponse);
	}



	private void notifyEndRequest(final ShadowRequest request, final ShadowResponse response) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < listeners.size(); i++) {
					listeners.get(i).endRequest(request, response);
				}
				outputs.remove(request);
			}
		}).run();
	}

	private void notifyNewRequest(ShadowRequest request) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).startRequest(request);
		}
	}


	public void addProxyListener(ProxyListener l) {
		this.listeners.add(l);
	}
}

