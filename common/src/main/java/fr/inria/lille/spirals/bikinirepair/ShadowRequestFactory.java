package fr.inria.lille.spirals.bikinirepair;

import fr.inria.lille.spirals.bikinirepair.ShadowRequest;
import org.eclipse.jetty.http.HttpHeader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.Enumeration;

public class ShadowRequestFactory {
	public static ShadowRequest create(HttpServletRequest request) {
		ShadowRequest shadowRequest = new ShadowRequest();

		// http method
		shadowRequest.setMethod(request.getMethod());
		// protocol
		shadowRequest.setProtocol(request.getProtocol());

		shadowRequest.setRequestURI(request.getRequestURI());
		shadowRequest.setQueryString(request.getQueryString());
		shadowRequest.setContextPath(request.getContextPath());
		shadowRequest.setContentType(request.getContentType());

		// parameters
		Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();
			String[] headerValues = request.getParameterValues(parameterName);
			for (int i = 0; i < headerValues.length; i++) {
				String value = headerValues[i];
				shadowRequest.addParameter(parameterName, value);
			}
		}

		// header
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			Enumeration<String> headerValues = request.getHeaders(headerName);
			while (headerValues.hasMoreElements()) {
				String value = headerValues.nextElement();
				shadowRequest.addHeader(headerName, value);
			}
		}

		if (hasContent(request)) {
			try {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();

				int nRead;
				byte[] data = new byte[16384];
				ServletInputStream inputStream = request.getInputStream();
				while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
					buffer.write(data, 0, nRead);
				}
				buffer.flush();
				shadowRequest.setContent(buffer.toByteArray());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return shadowRequest;
	}

	private static boolean hasContent(HttpServletRequest clientRequest) {
		return clientRequest.getContentLength() > 0 || clientRequest.getContentType() != null || clientRequest.getHeader(
				HttpHeader.TRANSFER_ENCODING.asString()) != null;
	}
}