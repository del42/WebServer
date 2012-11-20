package webserver;

import webserver.response.HttpRespMsg;

/**
 * This class defines an exception for the internal server errors. It is
 * instantiated when an error happens inside the web server because of the
 * server malfunction not the errors regarding the requests. It prepares a
 * "500 Internal Server Error" Response.
 * 
 */
@SuppressWarnings("serial")
public class InternalServerErrorException extends Exception {
	HttpRespMsg respMsg;

	public InternalServerErrorException(HttpRespMsg respMsg) {
		this.respMsg = respMsg;
		respMsg.setStatusCode("500");
		respMsg.setDescription("Internal Server Error");
	}

	public HttpRespMsg getRespMsg() {
		return respMsg;
	}
}
