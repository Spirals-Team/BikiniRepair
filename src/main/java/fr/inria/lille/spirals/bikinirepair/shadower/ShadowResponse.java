package fr.inria.lille.spirals.bikinirepair.shadower;

import org.eclipse.jetty.client.GZIPContentDecoder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShadowResponse {

	private int status;
	private String contentType;
	private Map<String, List<String>> headers = new HashMap<>();
	private byte[] body;

	public void addHeader(String headerName, String value) {
		if (!headers.containsKey(headerName)) {
			headers.put(headerName, new ArrayList<String>());
		}
		headers.get(headerName).add(value);
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public byte[] getBody() {
		return body;
	}

	public String getHtmlBody(){
		if(headers.containsKey("Content-Encoding") &&
				"gzip".equals(headers.get("Content-Encoding").get(0))) {
			if (contentType != null && contentType.startsWith("text")) {
				return new String(new GZIPContentDecoder().decode(ByteBuffer.wrap(getBody(), 0, getBody().length)).array());
			}
			return "";
		}
		if (getBody() != null) {
			return new String(getBody());
		}
		return "";
	}

	@Override
	public String toString() {
		return getHtmlBody();
	}
}