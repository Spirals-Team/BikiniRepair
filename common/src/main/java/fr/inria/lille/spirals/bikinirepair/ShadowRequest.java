package fr.inria.lille.spirals.bikinirepair;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.util.HttpCookieStore;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import javax.servlet.ServletException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ShadowRequest {

	private String method;
	private String protocol;
	private Map<String, List<String>> headers = new HashMap<>();
	private Map<String, List<String>> parameters = new HashMap<>();
	private byte[] content;
	private byte[] body;
	private String requestURI;
	private String contextPath;
	private String contentType;
	private String queryString;

	public void setMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getProtocol() {
		return protocol;
	}

	public void addHeader(String headerName, String value) {
		if (!headers.containsKey(headerName)) {
			headers.put(headerName, new ArrayList<String>());
		}
		headers.get(headerName).add(value);
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public byte[] getContent() {
		return content;
	}

	public byte[] getBody() {
		return body;
	}

	public void addBody(int length, byte... body) {
		int currentLength = 0;
		if (this.body == null) {
			this.body = new byte[length];
		} else {
			currentLength = this.body.length;
			Arrays.copyOf(this.body, this.body.length + length);
		}
		System.arraycopy(body, 0, this.body, currentLength, length);
	}

	public void addParameter(String parameterName, String value) {
		if (!parameters.containsKey(parameterName)) {
			parameters.put(parameterName, new ArrayList<String>());
		}
		parameters.get(parameterName).add(value);
	}

	public Map<String, List<String>> getParameters() {
		return parameters;
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	public String getRequestURI() {
		return requestURI;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getQueryString() {
		return queryString;
	}


	public String getTarget(TargetType target) {
		String host = "http://";
		try {
			switch (target) {
			case APPLICATION:
				host += Services.getAddressServer().getApplicationIP();
				break;
			case VALIDATION:
				host += Services.getAddressServer().getRegressionIP();
				break;
			case PATCH:
				host += Services.getAddressServer().getPatchIp();
				break;
			default:
				return null;
			}
			host += ":" +  Services.getAddressServer().getApplicationPort();
		} catch (Exception e) {
			return null;
		}
		StringBuilder uri = new StringBuilder(host);
		String path = getRequestURI();
		if(host.endsWith("/")) {
			uri.setLength(uri.length() - 1);
		}

		String rest = path;
		if(rest != null && rest.length() > 0) {
			if(!rest.startsWith("/")) {
				uri.append("/");
			}

			uri.append(rest);
		}

		String query = "";
		try {
			getQueryString();
			if (query != null) {
				String rewrittenURI = "://";
				if (uri.indexOf("/",
						uri.indexOf(rewrittenURI) + rewrittenURI.length())
						< 0) {
					uri.append("/");
				}

				uri.append("?").append(query);
			}
		} catch (NullPointerException e) {
			// ignore
		}

		return URI.create(uri.toString()).normalize().toString();
	}

	public ShadowResponse execute(TargetType target, String cookies) throws Exception {
		Request r =  createHttpClient()
				.newRequest(getTarget(target))
				.method(getMethod())
				.version(HttpVersion.fromString(getProtocol()));

		// headers
		Set<String> headersName = getHeaders().keySet();
		for (Iterator<String> iterator = headersName.iterator(); iterator
				.hasNext(); ) {
			String headerName = iterator.next();
			List<String> values = getHeaders().get(headerName);
			for (int i = 0; i < values.size(); i++) {
				String value = values.get(i);
				r.header(headerName, value);
			}
		}
		r = r.header("Cookie", cookies);
		if(getContent() != null) {
			r.content(new BytesContentProvider(getContent()));
		}
		r.timeout(2, TimeUnit.SECONDS);
		try {
			ContentResponse send = r.send();

			return ShadowResponseFactory.create(send);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private HttpClient createHttpClient() throws ServletException {
		HttpClient client = new HttpClient();
		client.setFollowRedirects(false);
		client.setCookieStore(new HttpCookieStore.Empty());
		QueuedThreadPool executor = new QueuedThreadPool(4);
		String servletName = "shadow";
		executor.setName(servletName);

		client.setExecutor(executor);

		String value = "256";
		client.setMaxConnectionsPerDestination(Integer.parseInt(value));

		value = "30000";
		client.setIdleTimeout(Long.parseLong(value));

		try {
			client.start();
			client.getContentDecoderFactories().clear();
			client.getProtocolHandlers().clear();
			return client;
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}