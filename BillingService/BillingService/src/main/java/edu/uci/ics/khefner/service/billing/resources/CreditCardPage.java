package edu.uci.ics.khefner.service.billing.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.khefner.service.billing.core.DatabaseQueries.CreditCardDBQueries;
import edu.uci.ics.khefner.service.billing.core.ResponseHandler;
import edu.uci.ics.khefner.service.billing.logger.ServiceLogger;
import edu.uci.ics.khefner.service.billing.models.CreditCards.CreditCardRequestModel;
import edu.uci.ics.khefner.service.billing.models.CreditCards.CreditCardRequestModel2;
import edu.uci.ics.khefner.service.billing.models.CreditCards.CreditCardResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.TimeZone;

@Path("creditcard")
public class CreditCardPage {


    @Path("insert")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response InsertCreditCard(@Context HttpHeaders headers, String jsonTxt){

        ServiceLogger.LOGGER.info("Received request to insert creditcard");

        CreditCardRequestModel requestModel;
        CreditCardResponseModel responseModel;

        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(TimeZone.getDefault());
        ServiceLogger.LOGGER.info("jsonTxt = " + jsonTxt);




        try{

        requestModel = mapper.readValue(jsonTxt, CreditCardRequestModel.class);
        ServiceLogger.LOGGER.info("Date2: " + requestModel.getExpiration());
        if(!requestModel.isValid()){
            int resultCode = requestModel.getResultCode();
            String message = ResponseHandler.GetResponseMessage(resultCode);
            responseModel = new CreditCardResponseModel(resultCode,message);
            if(resultCode > 0){return Response.status(Response.Status.OK).entity(responseModel).build();}
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        responseModel = CreditCardDBQueries.InsertCreditCard(requestModel);
        if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();}
        return Response.status(Response.Status.OK).entity(responseModel).build();

        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new CreditCardResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new CreditCardResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else {
                ServiceLogger.LOGGER.warning("IOException.");

            }

    }
        return Response.status(Response.Status.OK).build();

    }



    @Path("update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response UpdateCreditCard(@Context HttpHeaders headers, String jsonTxt){

        ServiceLogger.LOGGER.info("Received request to update creditcard");

        CreditCardRequestModel requestModel;
        CreditCardResponseModel responseModel;

        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(TimeZone.getDefault());
        ServiceLogger.LOGGER.info("jsonTxt = " + jsonTxt);




        try{

            requestModel = mapper.readValue(jsonTxt, CreditCardRequestModel.class);
            if(!requestModel.isValid()){
                int resultCode = requestModel.getResultCode();
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new CreditCardResponseModel(resultCode,message);
                if(resultCode > 0){return Response.status(Response.Status.OK).entity(responseModel).build();}
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            responseModel = CreditCardDBQueries.UpdateCreditCard(requestModel);
            if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();}
            return Response.status(Response.Status.OK).entity(responseModel).build();

        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new CreditCardResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new CreditCardResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else {
                ServiceLogger.LOGGER.warning("IOException.");

            }

        }
        return Response.status(Response.Status.OK).build();

    }



    @Path("retrieve")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response RetrieveCreditCard(@Context HttpHeaders headers, String jsonTxt){

        ServiceLogger.LOGGER.info("Received request to retrieve creditcard");

        CreditCardRequestModel2 requestModel;
        CreditCardResponseModel responseModel;

        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(TimeZone.getDefault());
        ServiceLogger.LOGGER.info("jsonTxt = " + jsonTxt);




        try{

            requestModel = mapper.readValue(jsonTxt, CreditCardRequestModel2.class);
            if(!requestModel.isValid()){
                int resultCode = requestModel.getResultCode();
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new CreditCardResponseModel(resultCode,message);
                if(resultCode > 0){return Response.status(Response.Status.OK).entity(responseModel).build();}
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            responseModel = CreditCardDBQueries.RetrieveCreditCard(requestModel);
            if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();}
            return Response.status(Response.Status.OK).entity(responseModel).build();

        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new CreditCardResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new CreditCardResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else {
                ServiceLogger.LOGGER.warning("IOException.");

            }

        }
        return Response.status(Response.Status.OK).build();

    }

    @Path("delete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response DeleteCreditCard(@Context HttpHeaders headers, String jsonTxt){

        ServiceLogger.LOGGER.info("Received request to delete creditcard");

        CreditCardRequestModel2 requestModel;
        CreditCardResponseModel responseModel;

        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(TimeZone.getDefault());
        ServiceLogger.LOGGER.info("jsonTxt = " + jsonTxt);




        try{

            requestModel = mapper.readValue(jsonTxt, CreditCardRequestModel2.class);
            if(!requestModel.isValid()){
                int resultCode = requestModel.getResultCode();
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new CreditCardResponseModel(resultCode,message);
                if(resultCode > 0){return Response.status(Response.Status.OK).entity(responseModel).build();}
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            responseModel = CreditCardDBQueries.DeleteCreditCard(requestModel);
            if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();}
            return Response.status(Response.Status.OK).entity(responseModel).build();

        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new CreditCardResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new CreditCardResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else {
                ServiceLogger.LOGGER.warning("IOException.");

            }

        }
        return Response.status(Response.Status.OK).build();

    }

}
