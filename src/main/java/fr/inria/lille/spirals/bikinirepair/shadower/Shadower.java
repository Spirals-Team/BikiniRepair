package fr.inria.lille.spirals.bikinirepair.shadower;

import fr.inria.lille.spirals.bikinirepair.oracle.request.RequestOracle;
import fr.inria.lille.spirals.bikinirepair.oracle.request.RequestOracleIpml;
import fr.inria.lille.spirals.bikinirepair.patch.PatchGeneration;
import fr.inria.lille.spirals.bikinirepair.regression.PatchValidation;
import fr.inria.lille.spirals.bikinirepair.services.ServicesType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

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

		ProxyListener proxyListener = new AbstractProxyListener() {
			@Override
			public void endRequest(ShadowRequest request, ShadowResponse response) {
				String cookiesKey = getCookiesKey(request.getHeaders().get("Cookie"));
				String responseCookiesKey = getCookiesKey(response.getHeaders().get("Set-Cookie"));

				String proxyCookies = null;
				if (cookies.containsKey(cookiesKey) && cookiesKey != null) {
					proxyCookies = cookies.get(cookiesKey);
				}

				RequestOracle requestOracle = new RequestOracleIpml();
				if (requestOracle.isValid(request, response)) {
					try {
						// the request is send to the patch validation service to setup the state
						ShadowResponse shadowResponse = request.execute(ServicesType.PATCH, proxyCookies);
						if (shadowResponse != null) {
							proxyCookies = getCookiesKey(shadowResponse.getHeaders().get("Set-Cookie"));
							cookies.put(cookiesKey, proxyCookies);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (!response.getHtmlBody().isEmpty()) {
						// send the request to the patch validation service
						new PatchValidation().run(request, response, regressionImageName);
					}
				} else {
					// if the request is invalid, generate the patches by sending
					new PatchGeneration(proxyCookies).run(request);
				}
			}

			private String getCookiesKey(List<String> cookies) {
				String responseCookiesKey = null;
				if (cookies != null) {
					for (int i = 0; i < cookies.size(); i++) {
						String cookiesValue = cookies.get(i);
						if (responseCookiesKey == null) {
							responseCookiesKey = "";
						}
						if (cookiesValue != null && cookiesValue.indexOf(";") > -1) {
							String value = cookiesValue.substring(0, cookiesValue.indexOf(";"));
							if (!value.endsWith("deleted")) {
								responseCookiesKey += value;
							}
						}
					}
				}
				return responseCookiesKey;
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