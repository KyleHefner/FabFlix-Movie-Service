package edu.uci.ics.khefner.service.api_gateway.threadpool;

import edu.uci.ics.khefner.service.api_gateway.models.RequestModel;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Map;

public class ClientRequest {
    private String email;
    private String sessionID;
    private String transactionID;
    private RequestModel request;
    private String URI;
    private String endpoint;
    private String method;
    private MultivaluedMap<String, String> queryParams;
    private Boolean hasQueryParams;


    public ClientRequest() {
        this.email = null;
        this.sessionID = null;
        this.transactionID = null;
        this.request = null;
        this.URI = null;
        this.endpoint = null;
        this.method = null;
        this.queryParams = null;
        this.hasQueryParams = false;

    }

    @Override
    public String toString() {
        return "ClientRequest{" +
                "email='" + email + '\'' +
                ", sessionID='" + sessionID + '\'' +
                ", transactionID='" + transactionID + '\'' +
                ", request=" + request +
                ", URI='" + URI + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", method='" + method + '\'' +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public RequestModel getRequest() {
        return request;
    }

    public void setRequest(RequestModel request) {
        this.request = request;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMethod() { return method; }

    public void setMethod(String method) { this.method = method; }

    public MultivaluedMap<String, String> getQueryParams() { return queryParams; }

    public void setQueryParams(MultivaluedMap<String, String> queryParams) { this.queryParams = queryParams; }

    public Boolean getHasQueryParams() { return hasQueryParams; }

    public void setHasQueryParams(Boolean hasQueryParams) { this.hasQueryParams = hasQueryParams; }
}
