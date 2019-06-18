package edu.uci.ics.khefner.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.khefner.service.billing.core.DatabaseQueries.OrderDBQueries;
import edu.uci.ics.khefner.service.billing.core.PaypalFunctions;
import edu.uci.ics.khefner.service.billing.logger.ServiceLogger;

import edu.uci.ics.khefner.service.billing.models.Orders.OrderRequestModel;
import edu.uci.ics.khefner.service.billing.models.Orders.OrderResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

@Path("order")
public class OrderPage {

    @Path("place")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response PlaceOrder(@Context HttpHeaders headers, String jsonTxt){

        ServiceLogger.LOGGER.info("received request to place order");

        OrderRequestModel requestModel;
        OrderResponseModel responseModel;

        ObjectMapper mapper = new ObjectMapper();
        ServiceLogger.LOGGER.info("jsonTxt = " + jsonTxt);

        try{

            requestModel = mapper.readValue(jsonTxt, OrderRequestModel.class);


            responseModel = OrderDBQueries.PlaceOrder(requestModel);
            if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();}
            return Response.status(Response.Status.OK).entity(responseModel).build();

        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new OrderResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new OrderResponseModel(-3, "JSON Parse Exception.");
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
    public Response RetrieveOrder(@Context HttpHeaders headers, String jsonTxt){

        ServiceLogger.LOGGER.info("received request to retrieve order");

        OrderRequestModel requestModel;
        OrderResponseModel responseModel;

        ObjectMapper mapper = new ObjectMapper();
        ServiceLogger.LOGGER.info("jsonTxt = " + jsonTxt);

        try{

            requestModel = mapper.readValue(jsonTxt, OrderRequestModel.class);


            responseModel = OrderDBQueries.RetrieveOrder(requestModel);
            if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();}
            return Response.status(Response.Status.OK).entity(responseModel).build();

        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new OrderResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new OrderResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else {
                ServiceLogger.LOGGER.warning("IOException.");

            }

        }
        return Response.status(Response.Status.OK).build();

    }


    @Path("complete")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response CompleteOrder( @QueryParam("paymentId") String paymentId,
                                   @QueryParam("PayerID") String payerId,
                                   @QueryParam("token") String token){
        ServiceLogger.LOGGER.info("received request to complete order");
        ServiceLogger.LOGGER.info("PaymentId: " + paymentId);
        ServiceLogger.LOGGER.info("PayerId: " + payerId);
        ServiceLogger.LOGGER.info("Token: " + token);

        //No need to validate anything...
        //need to execute the payment that was created in /order/place
        Map<String, String> response = PaypalFunctions.CompletePayment(paymentId, payerId);

        if(response.get("status") == "failure"){
            ServiceLogger.LOGGER.info("Failed to complete payment");
            OrderResponseModel responseModel = new OrderResponseModel(3422, "Payment can not be completed.");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }

        OrderResponseModel responseModel;
        //now need to update transactions table to include transactionID for a token.
        String transactionId = response.get("transactionId");

        responseModel = OrderDBQueries.CompleteOrder(transactionId, token);

        return Response.status(Response.Status.OK).build();
    }

}
