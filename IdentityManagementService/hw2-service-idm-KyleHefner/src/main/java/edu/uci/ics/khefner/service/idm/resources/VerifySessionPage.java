package edu.uci.ics.khefner.service.idm.resources;

import edu.uci.ics.khefner.service.idm.core.ResponseHandler;
import edu.uci.ics.khefner.service.idm.core.VerifySessionCore.VerifySessionDBQueries;
import edu.uci.ics.khefner.service.idm.logger.ServiceLogger;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.khefner.service.idm.models.VerifySession.VerifySessionRequestModel;
import edu.uci.ics.khefner.service.idm.models.VerifySession.VerifySessionResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static edu.uci.ics.khefner.service.idm.core.ResultCodeGlobals.JSON_MAPPING_EXCEPTION;
import static edu.uci.ics.khefner.service.idm.core.ResultCodeGlobals.JSON_PARSE_EXCEPTION;

@Path("session")
public class VerifySessionPage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response VerifySession(String jsonTxt){
        ServiceLogger.LOGGER.info("received Verify Session request");
        ServiceLogger.LOGGER.info(jsonTxt);

        ObjectMapper mapper = new ObjectMapper();
        VerifySessionRequestModel requestModel;
        VerifySessionResponseModel responseModel;

        try{


            requestModel = mapper.readValue(jsonTxt, VerifySessionRequestModel.class);

            if(!requestModel.isValid()){
                ServiceLogger.LOGGER.info("request model is invalid");
                int resultCode = requestModel.getResultCode();
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new VerifySessionResponseModel(resultCode,message);

                if(resultCode > 0){return Response.status(Response.Status.OK).entity(responseModel).build();}
                else{ return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();}
            }else{
                ServiceLogger.LOGGER.info("request model is valid");
                //request is valid, verify email and token are valid
                responseModel = VerifySessionDBQueries.VerifySession(requestModel.getEmail(),requestModel.getSessionID());

                int resultCode = responseModel.getResultCode();

                if(resultCode > 0){return Response.status(Response.Status.OK).entity(responseModel).build();}
                else if(resultCode < 0){return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();}
                else{return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();}



            }

        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                int resultCode = JSON_MAPPING_EXCEPTION;
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new VerifySessionResponseModel(resultCode,message);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                int resultCode = JSON_PARSE_EXCEPTION;
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new VerifySessionResponseModel(resultCode,message);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("IOException.");
            }

        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }



    }

