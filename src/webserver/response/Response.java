/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver.response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import webserver.InternalServerErrorException;
import webserver.configuration.ConfigVars;
import webserver.request.HttpReqMsg;

public class Response {
	private HttpReqMsg reqMsg;
	private Socket socket;
	private HttpRespMsg respMsg;
	private String path;
	private boolean authorized;

	public Response(HttpReqMsg reqMsg, Socket socket) {
		this.reqMsg = reqMsg;
		this.socket = socket;
		this.authorized = false;
		respMsg = new HttpRespMsg();
	}

	public HttpRespMsg buildResponse() throws InternalServerErrorException {
		// set default headers for every response
		respMsg.setVersion(reqMsg.getVersion());
		respMsg.setHeader("Server", "CSC 867");
		respMsg.setHeader("Date", getDate());
		respMsg.setHeader("Connection", "close");

		if (reqMsg.getBadRequest()) {
			respMsg.setStatusCode("400");
			respMsg.setDescription("Bad Request");
			respMsg.setBody(new File(ConfigVars.getServerRoot()
					+ "/error/400.html"));
		} else {
			// parse the uri in the request to extract the resource path
			parseURI(reqMsg.getURI());

			// check authentication
			new Authentication(this).checkAuthentication();

			if (!reqMsg.getCgiScript() && authorized) {
				try {
					// check the method in the request
					String methodName = reqMsg.getMethodName();

					if (methodName.equals("GET")) {
						getMethod();
					} else {
						if (methodName.equals("PUT")) {
							putMethod();
						} else {
							if (methodName.equals("HEAD")) {
								headMethod();
							}
						}
					}
				} catch (NullPointerException ex) {
					// System.out.println("No Method Name: " + ex.getMessage());
				}
			} else {
				if (reqMsg.getCgiScript() && authorized) {
					new CGIHandler(this).buildProcess();
				}
			}
		}
		return respMsg;
	}

	public void parseURI(String uri) {
		String documentRoot = ConfigVars.getDocumentRoot();
		String topDir;

		// find the top directory to check for alias and script alias
		try {
			String[] temp = uri.substring(1).split("/");
			topDir = "/" + temp[0];

		} catch (IndexOutOfBoundsException e) {
			topDir = uri;
		} catch (NullPointerException e) {
			topDir = uri;
		}

		// set the path for the resource requested by the client
		if (ConfigVars.getScriptAlias(topDir) != null) {
			path = uri.replace(topDir, ConfigVars.getScriptAlias(topDir));
			reqMsg.setCgiScript(true);

		} else {
			if (ConfigVars.getAlias(topDir) != null) {
				path = uri.replace(topDir, ConfigVars.getAlias(topDir));
			} else {

				if (uri.equals("/")) {
					for (int i = 0; i < ConfigVars.getDirectoryIndex().size(); i++) {
						path = documentRoot
								+ System.getProperty("file.separator")
								+ ConfigVars.getDirectoryIndex().get(i);
						if (new File(path).exists())
							break;
					}
				} else {
					path = documentRoot + uri;
				}
			}
		}
	}

	public void getMethod() {
		boolean updated;
		File resource = new File(path);
		if (!resource.exists()) {
			respMsg.setStatusCode("404");
			respMsg.setDescription("Not Found");
			respMsg.setBody(new File(ConfigVars.getServerRoot()
					+ "/error/404.html"));
		} else {
			if (ConfigVars.getCacheEnabled().equals("ON")) {
				updated = checkCache(resource);
			} else {
				updated = true;
			}
			if (updated) {
				String name = resource.getName();
				String type = name.substring(name.indexOf(".") + 1);

				long length = resource.length();

				respMsg.setHeader("Content-Type", ConfigVars.getMIME(type));
				respMsg.setHeader("Content-length", Long.toString(length));
				SimpleDateFormat sdf = new SimpleDateFormat(
						"EEE dd MMM yyyy HH:mm:ss z");
				respMsg.setHeader("Last-Modified",
						sdf.format(new Date(resource.lastModified())));
				respMsg.setStatusCode("200");
				respMsg.setDescription("OK");
				respMsg.setBody(resource);
			} else {
				if (!updated) {
					long lastModifiedDate = resource.lastModified();
					SimpleDateFormat sdf = new SimpleDateFormat(
							"EEE dd MMM yyyy HH:mm:ss z");
					respMsg.setHeader("Last-Modified",
							sdf.format(new Date(lastModifiedDate)));
					respMsg.setStatusCode("304");
					respMsg.setDescription("Not Modified");
				}
			}
		}

	}

	public boolean checkCache(File resource) {
		boolean updated = true;
		long lastModifiedDate;
		long modifiedSince = System.currentTimeMillis();

		String modSince = reqMsg.getHeader("If-Modified-Since");
		if (modSince != null) {
			lastModifiedDate = resource.lastModified();
			SimpleDateFormat sdf = new SimpleDateFormat(
					"dd MMM yyyy HH:mm:ss z");
			try {
				modifiedSince = (sdf.parse(modSince.substring(5))).getTime();
				// == or =>
				if (modifiedSince == lastModifiedDate) {
					updated = false;
				}
			} catch (ParseException e) {
				System.out.println("Parsing Date Error: " + e.getMessage());
			}

		}
		return updated;
	}

	 public void putMethod() throws InternalServerErrorException {
		String uploadRoot = ConfigVars.getUpload();
		path = uploadRoot + "/" + reqMsg.getURI();
		File resource = new File(path);
		try {
			if (resource.exists()) {
				long lastModifiedDate = resource.lastModified();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"EEE dd MMM yyyy HH:mm:ss z");
				respMsg.setHeader("Last-Modified",
						sdf.format(new Date(lastModifiedDate)));
				respMsg.setStatusCode("204");
				respMsg.setDescription("No Content");
			} else {
				OutputStream fOut = new FileOutputStream(resource);
				byte[] putFile = reqMsg.getBody();
				fOut.write(putFile);
				respMsg.setStatusCode("201");
				respMsg.setDescription("Created");
				fOut.flush();
				fOut.close();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw new InternalServerErrorException(respMsg);
		}

	}

	public void headMethod() {
		String documentRoot = ConfigVars.getDocumentRoot();
		File resource = new File(path);
		if (!resource.exists()) {
			respMsg.setStatusCode("404");
			respMsg.setDescription("Not Found");
			respMsg.setBody(new File(documentRoot + "/404.html"));

		} else {
			String name = resource.getName();
			String type = name.substring(name.indexOf(".") + 1);
			long length = resource.length();
			respMsg.setHeader("Content-Type", ConfigVars.getMIME(type));
			respMsg.setHeader("Content_length", Long.toString(length));
			respMsg.setStatusCode("200");
			respMsg.setDescription("OK");

		}
	}

	private String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public Socket getSocket() {
		return socket;
	}

	public HttpReqMsg getRequest() {
		return reqMsg;
	}

	public HttpRespMsg getResponse() {
		return respMsg;
	}

	public String getPath() {
		return path;
	}

	public boolean getAuthorized() {
		return authorized;
	}

	public void setAuthorized(boolean auth) {
		authorized = auth;
	}

}
