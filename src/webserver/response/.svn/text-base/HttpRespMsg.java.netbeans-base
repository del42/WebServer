package webserver.response;

import java.io.File;
import java.util.HashMap;
import webserver.HttpMsg;

public class HttpRespMsg extends HttpMsg{
    private String statusCode = "";
    private String description = "";
    private File body;
    
    public void setStatusCode(String code) {
        statusCode = code;
    }
    
    public String getStatusCode() {
        return statusCode;
    }
    
    public int getHeaderSize() {
        return headers.size();
    }
    
    public HashMap<String, String> getHeaders() {
        return headers;
    }
    
    public void setDescription(String phrase) {
        description = phrase;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setBody(File resource) {
        this.body = resource;
    }
    
    public File getBody() {
        return body;
    }
}
