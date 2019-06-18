package edu.uci.ics.khefner.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.khefner.service.idm.core.RegisterUserCore.RegisterUserDBQueries;
import edu.uci.ics.khefner.service.idm.core.ResponseHandler;
import edu.uci.ics.khefner.service.idm.logger.ServiceLogger;
import edu.uci.ics.khefner.service.idm.models.Register.RegisterRequestModel;
import edu.uci.ics.khefner.service.idm.models.Register.RegisterResponseModel;
import static edu.uci.ics.khefner.service.idm.core.ResultCodeGlobals.*;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Context;
import java.io.IOException;

@Path("register")
public class RegisterPage {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response RegisterUser(String jsonText, @Context HttpHeaders headers){
        ServiceLogger.LOGGER.info("received request to register user") ;




        ObjectMapper mapper = new ObjectMapper();
        RegisterRequestModel requestModel;
        RegisterResponseModel responseModel;

        try {
            //put json text into request Model and validate it.
            requestModel = mapper.readValue(jsonText, RegisterRequestModel.class);

            if (!requestModel.isValid()){
                //data is invalid build and return response model
                ServiceLogger.LOGGER.info("request model is invalid");
                int resultCode = requestModel.getResultCode();
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new RegisterResponseModel(resultCode,message);

                if(resultCode > 0){return Response.status(Status.OK).entity(responseModel).build();}
                else{ return Response.status(Status.BAD_REQUEST).entity(responseModel).build();}

            }
            else{
                ServiceLogger.LOGGER.info("request model is valid");
                //data is plausibly valid, try to query database for email
                int query_result = RegisterUserDBQueries.isEmailInUse(requestModel.getEmail());

                if(query_result == 0){
                    //means email is already being used so return email already in use response
                    int resultCode = EMAIL_ALREADY_IN_USE;
                    String message = ResponseHandler.GetResponseMessage(resultCode);
                    responseModel = new RegisterResponseModel(resultCode, message);
                    return Response.status(Status.OK).entity(responseModel).build();
                }
                else if(query_result == 1){
                    //means email is not being used so enter into db and return user registered successfully response
                    if(RegisterUserDBQueries.InsertNewUser(requestModel.getEmail(), requestModel.getPassword())){
                        //User was successfully registered response model
                        ServiceLogger.LOGGER.info("User successfully registered");
                        int resultCode = USER_REGISTERED_SUCCESSFULLY;
                        String message = ResponseHandler.GetResponseMessage(resultCode);
                        responseModel = new RegisterResponseModel(resultCode,message);
                        //need to zero out the password array for security reasons
                        requestModel.clearPassword();
                        return Response.status(Status.OK).entity(responseModel).build();
                    }
                    else{
                        //query to insert new user failed. return internal server error response
                        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                    }

                }
                else if(query_result == 2){
                    //means sql error occurred so return a Internal Server error response
                    return Response.status(Status.INTERNAL_SERVER_ERROR).build();

                }
            }

            //return Response.status(Status.OK).build();
        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                int resultCode = JSON_MAPPING_EXCEPTION;
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new RegisterResponseModel(resultCode,message);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                int resultCode = JSON_PARSE_EXCEPTION;
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new RegisterResponseModel(resultCode,message);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("IOException.");
            }

        }
        return Response.status(Status.BAD_REQUEST).build();
    }

}
