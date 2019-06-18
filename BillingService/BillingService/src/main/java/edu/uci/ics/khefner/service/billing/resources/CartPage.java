package edu.uci.ics.khefner.service.billing.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.khefner.service.billing.core.DatabaseQueries.CartDBQueries;
import edu.uci.ics.khefner.service.billing.core.ResponseHandler;
import edu.uci.ics.khefner.service.billing.logger.ServiceLogger;
import edu.uci.ics.khefner.service.billing.models.Carts.CartRequestModel;
import edu.uci.ics.khefner.service.billing.models.Carts.CartRequestModel2;
import edu.uci.ics.khefner.service.billing.models.Carts.CartRequestModel3;
import edu.uci.ics.khefner.service.billing.models.Carts.CartResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("cart")
public class CartPage {


    @Path("insert")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response InsertCart(@Context HttpHeaders headers, String jsonText){

        ServiceLogger.LOGGER.info("Received request to insert into cart");

        CartRequestModel requestModel;
        CartResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();
        ServiceLogger.LOGGER.info("jsonText = " + jsonText);


        try {

            requestModel = mapper.readValue(jsonText,CartRequestModel.class);

            if(!requestModel.isValid()){
                int resultCode = requestModel.getResultCode();
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new CartResponseModel(resultCode,message);
                if(resultCode > 0){return Response.status(Response.Status.OK).entity(responseModel).build();}
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }



            responseModel = CartDBQueries.InsertIntoCart(requestModel);
            if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();}
            return Response.status(Response.Status.OK).entity(responseModel).build();

        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new CartResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new CartResponseModel(-3, "JSON Parse Exception.");
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
    public Response UpdateCart(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to update cart");

        CartRequestModel requestModel;
        CartResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();
        ServiceLogger.LOGGER.info("jsonTxt = " + jsonText);



        try {

            requestModel = mapper.readValue(jsonText, CartRequestModel.class);

            if(!requestModel.isValid()){
                int resultCode = requestModel.getResultCode();
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new CartResponseModel(resultCode,message);
                if(resultCode > 0){return Response.status(Response.Status.OK).entity(responseModel).build();}
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            responseModel = CartDBQueries.UpdateCart(requestModel);
            if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();}
            return Response.status(Response.Status.OK).entity(responseModel).build();



        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new CartResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new CartResponseModel(-3, "JSON Parse Exception.");
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
    public Response DeleteFromCart(@Context HttpHeaders headers, String jsonTxt) {
        ServiceLogger.LOGGER.info("Received request to delete from cart");

        CartRequestModel2 requestModel;
        CartResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();
        ServiceLogger.LOGGER.info("jsonTxt = " + jsonTxt);



        try {


            requestModel = mapper.readValue(jsonTxt, CartRequestModel2.class);
            if(!requestModel.isValid()){
                int resultCode = requestModel.getResultCode();
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new CartResponseModel(resultCode,message);
                if(resultCode > 0){return Response.status(Response.Status.OK).entity(responseModel).build();}
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }


            responseModel = CartDBQueries.DeleteFromCart(requestModel);
            if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();}
            return Response.status(Response.Status.OK).entity(responseModel).build();



        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new CartResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new CartResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else {
                ServiceLogger.LOGGER.warning("IOException.");

            }
        }
        return Response.status(Response.Status.OK).build();
    }


    @Path("clear")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response ClearCart(@Context HttpHeaders headers, String jsonTxt) {
        ServiceLogger.LOGGER.info("Received request to clear cart");

        CartRequestModel3 requestModel;
        CartResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();
        ServiceLogger.LOGGER.info("jsonTxt = " + jsonTxt);

        try {


            requestModel = mapper.readValue(jsonTxt, CartRequestModel3.class);
            if(!requestModel.isValid()){
                int resultCode = requestModel.getResultCode();
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new CartResponseModel(resultCode,message);
                if(resultCode > 0){return Response.status(Response.Status.OK).entity(responseModel).build();}
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            responseModel = CartDBQueries.ClearCart(requestModel);
            if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();}
            return Response.status(Response.Status.OK).entity(responseModel).build();



        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new CartResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new CartResponseModel(-3, "JSON Parse Exception.");
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
    public Response RetrieveCart(@Context HttpHeaders headers, String jsonTxt) {
        ServiceLogger.LOGGER.info("Received request to retrieve cart");

        CartRequestModel3 requestModel;
        CartResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();
        ServiceLogger.LOGGER.info("jsonTxt = " + jsonTxt);

        try {


            requestModel = mapper.readValue(jsonTxt, CartRequestModel3.class);
            if(!requestModel.isValid()){
                int resultCode = requestModel.getResultCode();
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new CartResponseModel(resultCode,message);
                if(resultCode > 0){return Response.status(Response.Status.OK).entity(responseModel).build();}
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            responseModel = CartDBQueries.RetrieveCart(requestModel);
            if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();}
            return Response.status(Response.Status.OK).entity(responseModel).build();



        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new CartResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new CartResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else {
                ServiceLogger.LOGGER.warning("IOException.");

            }
        }
        return Response.status(Response.Status.OK).build();
    }

}
