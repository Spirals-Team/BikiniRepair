package fr.inria.lille.spirals.bikinirepair.shadower;

import fr.inria.lille.spirals.bikinirepair.Services;
import fr.inria.lille.spirals.bikinirepair.ShadowRequest;
import fr.inria.lille.spirals.bikinirepair.ShadowResponse;
import fr.inria.lille.spirals.bikinirepair.TargetType;
import fr.inria.lille.spirals.bikinirepair.oracle.request.RequestOracle;
import fr.inria.lille.spirals.bikinirepair.oracle.request.RequestOracleIpml;
import fr.inria.lille.spirals.bikinirepair.regression.PatchValidation;
import fr.inria.spirals.npefix.resi.context.Lapse;
import fr.inria.spirals.npefix.resi.oracle.AbstractOracle;
import fr.inria.spirals.npefix.resi.oracle.Oracle;
import fr.inria.spirals.npefix.resi.selector.Selector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shadower {
	private static final int PORT = 8080;

	private Map<String, String> cookies = new HashMap<String, String>();
	private String regressionImageName;

	public Shadower(String regressionImageName) {
		this.regressionImageName = regressionImageName;
	}

	public void start() {
		final Server server = new Server(PORT);

		// Create root context and add the ProxyServlet.Transparent to it
		ServletContextHandler contextHandler = new ServletContextHandler();
		server.setHandler(contextHandler);

		final ProxyServlet servlet = new ProxyServlet();

		final Selector selector = Services.getPatchServer();

		if (selector == null) {
			throw new RuntimeException("Selector not found.");
		}


		ProxyListener proxyListener = new AbstractProxyListener() {

			Lapse lapse;

			@Override
			public void startRequest(ShadowRequest request) {
				lapse = new Lapse(selector);
				lapse.setTestClassName(request.getRequestURI());
				lapse.setTestName(request.getParameters() + "");

				try {
					selector.startLaps(lapse);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void endRequest(ShadowRequest request, ShadowResponse response) {
				List<String> cookiesKey  = request.getHeaders().get("Cookie");
				if (cookiesKey != null) {
					for (int i = 0; i < cookiesKey.size(); i++) {
						String cookyValue = cookiesKey.get(i);
						if (cookiesKey != null
								&& cookyValue.indexOf(";") > -1) {
							cookyValue = cookyValue.substring(0, cookiesKey.indexOf(";"));
						}
					}
				}

				String proxyCookies = null;
				if (cookies.containsKey(cookiesKey) && cookiesKey != null) {
					proxyCookies = cookies.get(cookiesKey);
				}
				List<String> responseCookies = request.getHeaders().get("Set-Cookie");
				if (responseCookies != null) {
					//responseCookies = responseCookies.substring(0, responseCookies.indexOf(";"));
				}

				RequestOracle requestOracle = new RequestOracleIpml();


				if (requestOracle.isValid(request, response)) {
					// if the request is invalid, generate the patches by sending
					try {
						ShadowResponse shadowResponse = request.execute(TargetType.PATCH, proxyCookies);

						Oracle oracle = new AbstractOracle("Request", requestOracle.isValid(request, shadowResponse));
						lapse.setOracle(oracle);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					// send the request to the patch validation service
					new PatchValidation().run(regressionImageName);
				}







				ShadowResponse shadowResponse = null;
				try {
					shadowResponse = servlet.copyRequest(request, servlet.getTarget(request, patchURI), proxyCookies);
					if (shadowResponse == null) {
						return;
					}
					//proxyCookies = shadowResponse.getHeaders().get("Set-Cookie");
					if (proxyCookies != null) {
						String tmp = proxyCookies.substring(0, proxyCookies.indexOf(";"));
						if (!tmp.endsWith("deleted")) {
							//cookies.put(cookiesKey, tmp);
						}
					}

					Oracle oracle = new AbstractOracle("Request", shadowResponse.getStatus() != 500);
					lapse.setOracle(oracle);
					try {
						selector.restartTest(lapse);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					if (!oracle.isValid()) {
						if (shadowResponse.getContentType().startsWith("text/")) {
							//new String(new GZIPContentDecoder().decode(new HeapByteBuffer(shadowResponse.getContent(), 0, shadowResponse.getContent().length)).array())
							//System.out.println(shadowResponse.getContentAsString());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		servlet.addProxyListener(proxyListener);
		contextHandler.addServlet(new ServletHolder(servlet), "/*");

		// Start the server
		try {
			server.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}