/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver.request;

import webserver.HttpMsg;

public class HttpReqMsg extends HttpMsg{
    
    private String methodName;
    private String uri;
    private byte[] body;
    private boolean badRequest;
    private boolean cgiRequest;
    
    public HttpReqMsg(){
        super();
        uri = "";
        badRequest = false;
        cgiRequest = false;
    }
    
    public void setMethodName(String mName) {
        methodName = mName;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public void setURI(String path) {
        uri = path;
    }
    
    public String getURI() {
        return uri;
    }
    
    public void setBadRequest(boolean badReq) {
		this.badRequest = badReq;
	}
    
    public boolean getBadRequest() {
    	return badRequest;
    }
    
    public void setCgiScript(boolean script) {
       this.cgiRequest = script;
    }
    
    public boolean getCgiScript() {
        return cgiRequest;
    }
    
    public void setBody(byte[] reqBody) {
    	/* if (headers.containsKey("Content-Length")){
    		 int length = Integer.parseInt(getHeader("Content-Length").trim());
    		 body = new byte[length];
         }*/
    	this.body = reqBody;
    }
    
    
    public byte[] getBody() {
    	return body;
    }
    
}
