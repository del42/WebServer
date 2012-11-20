/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Request {
	private HttpReqMsg reqMsg;
	private BufferedInputStreamReader req;
	private String reqLine;

	public Request(BufferedInputStreamReader req) {
		this.req = req;
		reqMsg = new HttpReqMsg();
	}

	/**
	 * This method reads the request that is sent from the client line by line,
	 * parses each line using a method and prints each line. If the request
	 * contains body, it calls a method to read the body.
	 * 
	 * @return an HttpReqMsg which contains every information sent by the client
	 */
	public HttpReqMsg parseRequest() {

		try {
			reqLine = req.readLine();
			while (reqLine != null && reqLine.trim().length() != 0) {
				parseEachReqLine(reqLine);
				System.out.println(reqLine);
				reqLine = req.readLine();
			}
			readBody();
		} catch (IOException ex) {
			System.out.println("Error Reading the Request!");
			System.exit(1);
		}
		return reqMsg;
	}

	/**
	 * This method parses each line of an HTTP Request sent from the client and
	 * creates an HttpReqMsg using the parsed information.
	 * 
	 * @param line
	 *            a line in the request stream
	 * 
	 */
	public void parseEachReqLine(String line) {
		String temp[];
		// initial line containing methods
		if (line.startsWith("GET") || line.startsWith("POST")
				|| line.startsWith("HEAD") || line.startsWith("PUT")) {
			String delimiter = " ";
			temp = line.split(delimiter);
			if (temp.length == 3) {
				reqMsg.setMethodName(temp[0]);
				reqMsg.setURI(temp[1]);
				reqMsg.setVersion(temp[2]);
			} else {
				if (temp.length == 2) {
					reqMsg.setMethodName(temp[0]);
					reqMsg.setVersion(temp[1]);
					reqMsg.setURI("/");
				} else {
					reqMsg.setBadRequest(true);
				}
			}
		} else {
			// Header lines
			if (line.contains(":")) {
				String delimiter = ":";
				temp = line.split(delimiter);
				if (temp.length == 2) {
					reqMsg.setHeader(temp[0], temp[1]);
				} else {
					if (temp.length > 2) {
						for (int i = 2; i < temp.length; i++) {
							temp[1] = temp[1] + ":" + temp[i];
						}
						reqMsg.setHeader(temp[0], temp[1]);
					}
				}

			} else {
				reqMsg.setBadRequest(true);
			}
		}
	}

	/**
	 * This method reads the body of the request if it contains any
	 */
	private void readBody() {
		byte[] body = null;
		int bytesRead;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String contentLength = reqMsg.getHeader("Content-Length");
		try {
			if (contentLength != null) {
				int length = Integer.parseInt(contentLength.trim());
				body = new byte[length];
				bytesRead = req.read(body, 0, length);
				out.write(body, 0, bytesRead);
				body = out.toByteArray();

			} else {
				body = new byte[1024];
				while (req.available() > 0) {
					bytesRead = req.read(body, 0, 1024);
					out.write(body, 0, bytesRead);
				}
				body = out.toByteArray();
			}
		} catch (IOException e) {
		}
		reqMsg.setBody(body);

	}
}