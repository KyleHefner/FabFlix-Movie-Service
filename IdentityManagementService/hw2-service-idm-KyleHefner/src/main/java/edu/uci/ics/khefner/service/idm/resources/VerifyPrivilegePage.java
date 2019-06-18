package edu.uci.ics.khefner.service.idm.resources;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.khefner.service.idm.core.ResponseHandler;
import edu.uci.ics.khefner.service.idm.core.VerifyPrivilegeCore.VerifyPrivilegeDBQueries;
import edu.uci.ics.khefner.service.idm.logger.ServiceLogger;
import edu.uci.ics.khefner.service.idm.models.VerifyPrivilege.VerifyPrivilegeRequestModel;
import edu.uci.ics.khefner.service.idm.models.VerifyPrivilege.VerifyPrivilegeResponseModel;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;

import static edu.uci.ics.khefner.service.idm.core.ResultCodeGlobals.JSON_MAPPING_EXCEPTION;
import static edu.uci.ics.khefner.service.idm.core.ResultCodeGlobals.JSON_PARSE_EXCEPTION;

@Path("privilege")
public class VerifyPrivilegePage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response VerifyPrivilege(String jsonTxt){
        ServiceLogger.LOGGER.info("received Login request");
        ServiceLogger.LOGGER.info(jsonTxt);

        ObjectMapper mapper = new ObjectMapper();
        VerifyPrivilegeRequestModel requestModel;
        VerifyPrivilegeResponseModel responseModel;

        try{
            requestModel = mapper.readValue(jsonTxt,VerifyPrivilegeRequestModel.class);
            if(!requestModel.Valid()){
                ServiceLogger.LOGGER.info("request model is invalid");
                int resultCode = requestModel.ResultCode();
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new VerifyPrivilegeResponseModel(resultCode,message);

                if(resultCode > 0){return Response.status(Status.OK).entity(responseModel).build();}
                else{ return Response.status(Status.BAD_REQUEST).entity(responseModel).build();}
            }else{
                ServiceLogger.LOGGER.info("request model is valid");
                //need to check plevel of requested user
                responseModel = VerifyPrivilegeDBQueries.VerifyPrivilege(requestModel.getEmail(), requestModel.getPlevel());
                int resultCode = responseModel.getResultCode();

                if(resultCode > 0){return Response.status(Status.OK).entity(responseModel).build();}
                else if(resultCode < 0){return Response.status(Status.BAD_REQUEST).entity(responseModel).build();}
                else{return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();}


            }
        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                int resultCode = JSON_MAPPING_EXCEPTION;
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new VerifyPrivilegeResponseModel(resultCode,message);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                int resultCode = JSON_PARSE_EXCEPTION;
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new VerifyPrivilegeResponseModel(resultCode,message);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("IOException.");
            }

        }
        return Response.status(Status.BAD_REQUEST).build();
    }
}