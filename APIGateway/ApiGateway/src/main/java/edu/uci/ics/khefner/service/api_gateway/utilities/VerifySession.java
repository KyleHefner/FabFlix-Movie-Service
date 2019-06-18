package edu.uci.ics.khefner.service.api_gateway.utilities;


import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.khefner.service.api_gateway.GatewayService;
import edu.uci.ics.khefner.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.khefner.service.api_gateway.models.idm.Session.VerifySessionRequestModel;
import edu.uci.ics.khefner.service.api_gateway.models.idm.Session.VerifySessionResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class VerifySession {

    public static Response VerifySession(String sessionID,  String email) {
        //Verifies sessionID of the client...
        //returns response from idm

        // Create a new Client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Get the URI for the IDM
        ServiceLogger.LOGGER.info("Building URI...");
        String URI = GatewayService.getIdmConfigs().getIdmUri();

        ServiceLogger.LOGGER.info("Setting path to endpoint...");
        String ENDPOINT_PATH = GatewayService.getIdmConfigs().getEPSessionVerify();

        // Create a WebTarget to send a request at
        ServiceLogger.LOGGER.info("Building WebTarget...");
        ServiceLogger.LOGGER.info("endpoint path: " + ENDPOINT_PATH);
        WebTarget webTarget = client.target(URI).path(ENDPOINT_PATH);

        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        //set payload
        VerifySessionRequestModel requestModel = new VerifySessionRequestModel(email, sessionID);

        // Send the request and save it to a Response
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Sent!");
        ServiceLogger.LOGGER.info("response content: " + response.toString());



            //String jsonText = response.readEntity(String.class);
            //ServiceLogger.LOGGER.info("Json text: " + jsonText);
            //VerifySessionResponseModel responseModel;
            //ObjectMapper mapper = new ObjectMapper();


//            try {
//                responseModel = mapper.readValue(jsonText, VerifySessionResponseModel.class);
//                if(responseModel.getResultCode() == 130 ){
//                    ServiceLogger.LOGGER.info("Active Session");
//                    return true;}
//                else{
//                    ServiceLogger.LOGGER.info("Inactive Session");
//                    return false;}
//            }catch (IOException e) {
//                e.printStackTrace();
//            }
//            ServiceLogger.LOGGER.info("Error when verifying session.");
//            return false;
//
//        } else {
//            ServiceLogger.LOGGER.info("Received Status " + response.getStatus() + " -- you lose.");
//            return false;
//        }


        return response;
    }

    public static VerifySessionResponseModel MaptoModel(String jsonTxt){
        //maps response json to a verify Session response model

        ObjectMapper mapper = new ObjectMapper();
        VerifySessionResponseModel responseModel;
        try{
            responseModel = mapper.readValue(jsonTxt, VerifySessionResponseModel.class);
            return responseModel;

        }catch (IOException e) {
                e.printStackTrace();
            }
        responseModel = new VerifySessionResponseModel(-1, "Internal Server Error");
        return responseModel;

/*        ServiceLogger.LOGGER.info("json: " + jsonTxt);
        Integer resultCode = 0;
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(jsonTxt);
        if(m.find()){
             resultCode = Integer.parseInt(m.group());
        }
        return resultCode;*/


    }
}
