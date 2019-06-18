package edu.uci.ics.khefner.service.api_gateway.utilities;

import edu.uci.ics.khefner.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.khefner.service.api_gateway.threadpool.ClientRequest;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Map;

public class SendRequest {
    public static Response SendRequest(ClientRequest clientRequest){

        // Create a new Client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Get the URI for the IDM
        ServiceLogger.LOGGER.info("Building URI...");
        String URI = clientRequest.getURI();

        ServiceLogger.LOGGER.info("Setting path to endpoint...");
        String ENDPOINT_PATH =clientRequest.getEndpoint();

        // Create a WebTarget to send a request at
        WebTarget webTarget;
        if(clientRequest.getMethod() == "GET" && clientRequest.getHasQueryParams()){

                ServiceLogger.LOGGER.info("Building WebTarget...");
                ServiceLogger.LOGGER.info("endpoint path: " + ENDPOINT_PATH);
                webTarget = client.target(URI).path(ENDPOINT_PATH);
                for (String key : clientRequest.getQueryParams().keySet()){
                    webTarget = webTarget.queryParam(key, clientRequest.getQueryParams().getFirst(key));
                }
                ServiceLogger.LOGGER.info("webtarget: " + webTarget.toString());

        } else {
            ServiceLogger.LOGGER.info("Building WebTarget...");
            ServiceLogger.LOGGER.info("endpoint path: " + ENDPOINT_PATH);
            webTarget = client.target(URI).path(ENDPOINT_PATH);
            ServiceLogger.LOGGER.info("webtarget: " + webTarget.toString());
        }

        // Create an InvocationBuilder to create the HTTP request
        Invocation.Builder invocationBuilder;

        if(ENDPOINT_PATH == "/session" || ENDPOINT_PATH == "/register" || ENDPOINT_PATH =="/login"){
            ServiceLogger.LOGGER.info("Starting invocation builder for " + ENDPOINT_PATH + "...");
            invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).header("transactionID", clientRequest.getTransactionID());
        }
        else{
            ServiceLogger.LOGGER.info("Starting invocation builder for " + ENDPOINT_PATH + "...");
            invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).header("sessionID", clientRequest.getSessionID()).header("email", clientRequest.getEmail()).header("transactionID", clientRequest.getTransactionID());

        }

        if(clientRequest.getMethod() == "GET"){
            ServiceLogger.LOGGER.info("Sending request...");
            Response response = invocationBuilder.get();
            ServiceLogger.LOGGER.info("Sent!");
            ServiceLogger.LOGGER.info("response content: " + response.toString());
            return response;


        }
        else if (clientRequest.getMethod() == "DELETE"){
            ServiceLogger.LOGGER.info("Sending request...");
            Response response = invocationBuilder.delete();
            ServiceLogger.LOGGER.info("Sent!");
            ServiceLogger.LOGGER.info("response content: " + response.toString());
            return response;
        }

        else {

            // Send the request and save it to a Response
            ServiceLogger.LOGGER.info("Sending request...");
            Response response = invocationBuilder.post(Entity.entity(clientRequest.getRequest(), MediaType.APPLICATION_JSON));
            ServiceLogger.LOGGER.info("Sent!");
            ServiceLogger.LOGGER.info("response content: " + response.toString());
            return response;
        }


    }
}
