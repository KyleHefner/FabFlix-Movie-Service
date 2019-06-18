package edu.uci.ics.khefner.service.api_gateway.resources;

import edu.uci.ics.khefner.service.api_gateway.DatabaseQueries.DeleteResponse;
import edu.uci.ics.khefner.service.api_gateway.DatabaseQueries.GetResponse;
import edu.uci.ics.khefner.service.api_gateway.logger.ServiceLogger;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("report")
public class ReportEndpoint {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetResponse(String jsonTxt, @Context HttpHeaders headers){


        ServiceLogger.LOGGER.info("received request to get report.");
        //ServiceLogger.LOGGER.info("jsonTxt: " + jsonTxt);
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");

        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        ServiceLogger.LOGGER.info("transactionID: " + transactionID);


        //need to search responses table by transactionid...
        Response response = GetResponse.GetResponse(transactionID);

        //need to delete the retrieved response if the respnse isnt a 204 NO content
        if(response.getStatus() != 204){
            DeleteResponse.DeleteResponse(transactionID);
        }
        return response;

    }
}
