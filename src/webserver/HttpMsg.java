package webserver;

import java.util.HashMap;

/**
 * This class provides a generic class for both request and respond messages and
 * contains the HTTP version and the headers which is common in both messages.
 * Each header is stored in HashMap.
 */
public abstract class HttpMsg {
	protected String version = "";
	protected HashMap<String, String> headers;

	public HttpMsg() {
		headers = new HashMap<String, String>();
	}

	public void setVersion(String v) {
		version = v;
	}

	public String getVersion() {
		return version;
	}

	public void setHeader(String hName, String hValue) {
		headers.put(hName, hValue);
	}

	/**
	 * This method returns a value for a given header.
	 * 
	 * @param hName
	 *            the header name
	 * @return the value for a given header
	 */
	public String getHeader(String hName) {
		return headers.get(hName);
	}

	/**
	 * This method returns all headers.
	 * 
	 * @return a HashMap containing all headers
	 * 
	 */
	public HashMap<String, String> getAllHeaders() {
		return headers;
	}

}
