package fr.inria.lille.spirals.bikinirepair;

import fr.inria.lille.spirals.bikinirepair.ShadowResponse;
import org.eclipse.jetty.client.api.ContentResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

public class ShadowResponseFactory {

	public static ShadowResponse create(HttpServletResponse response) {
		ShadowResponse shadowResponse = new ShadowResponse();
		shadowResponse.setStatus(response.getStatus());
		shadowResponse.setContentType(response.getContentType());

		// header
		Collection<String> headerNames = response.getHeaderNames();
		for (Iterator<String> iterator = headerNames.iterator(); iterator
				.hasNext(); ) {
			String headerName =  iterator.next();
			Collection<String> headerValues = response.getHeaders(headerName);
			for (Iterator<String> stringIterator = headerValues
					.iterator(); stringIterator.hasNext(); ) {
				String value =  stringIterator.next();
				shadowResponse.addHeader(headerName, value);
			}
		}


		return shadowResponse;
	}

	public static ShadowResponse create(ContentResponse response) {
		ShadowResponse shadowResponse = new ShadowResponse();
		shadowResponse.setStatus(response.getStatus());
		shadowResponse.setContentType(response.getMediaType());
		shadowResponse.setBody(response.getContent());

		// header
		Enumeration<String> headerNames = response.getHeaders().getFieldNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			Enumeration<String> headerValues = response.getHeaders()
					.getValues(headerName);
			while (headerValues.hasMoreElements()) {
				String value = headerValues.nextElement();
				shadowResponse.addHeader(headerName, value);
			}
		}

		return shadowResponse;
	}
}