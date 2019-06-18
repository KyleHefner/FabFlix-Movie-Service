package edu.uci.ics.khefner.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.khefner.service.billing.core.DatabaseQueries.CustomerDBQueries;
import edu.uci.ics.khefner.service.billing.core.ResponseHandler;
import edu.uci.ics.khefner.service.billing.logger.ServiceLogger;
import edu.uci.ics.khefner.service.billing.models.Customers.CustomerRequestModel;
import edu.uci.ics.khefner.service.billing.models.Customers.CustomerRequestModel1;
import edu.uci.ics.khefner.service.billing.models.Customers.CustomerResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("customer")
public class CustomerPage {

    @Path("insert")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response InsertCustomer(@Context HttpHeaders headers, String jsonTxt){

        ServiceLogger.LOGGER.info("Received request to insert customer");

        CustomerRequestModel requestModel;
        CustomerResponseModel responseModel;

        ObjectMapper mapper = new ObjectMapper();
        ServiceLogger.LOGGER.info("jsonTxt = " + jsonTxt);

        try{

            requestModel = mapper.readValue(jsonTxt, CustomerRequestModel.class);
            if(!requestModel.isValid()){
                int resultCode = requestModel.getResultCode();
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new CustomerResponseModel(resultCode,message);
                if(resultCode > 0){return Response.status(Response.Status.OK).entity(responseModel).build();}
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            responseModel = CustomerDBQueries.InsertCustomer(requestModel);
            if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();}
            return Response.status(Response.Status.OK).entity(responseModel).build();

        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new CustomerResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new CustomerResponseModel(-3, "JSON Parse Exception.");
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
    public Response UpdateCustomer(@Context HttpHeaders headers, String jsonTxt){

        ServiceLogger.LOGGER.info("Received request to update customer");

        CustomerRequestModel requestModel;
        CustomerResponseModel responseModel;

        ObjectMapper mapper = new ObjectMapper();
        ServiceLogger.LOGGER.info("jsonTxt = " + jsonTxt);

        try{

            requestModel = mapper.readValue(jsonTxt, CustomerRequestModel.class);
            if(!requestModel.isValid()){
                int resultCode = requestModel.getResultCode();
                String message = ResponseHandler.GetResponseMessage(resultCode);
                responseModel = new CustomerResponseModel(resultCode,message);
                if(resultCode > 0){return Response.status(Response.Status.OK).entity(responseModel).build();}
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            responseModel = CustomerDBQueries.UpdateCustomer(requestModel);
            if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();}
            return Response.status(Response.Status.OK).entity(responseModel).build();

        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new CustomerResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new CustomerResponseModel(-3, "JSON Parse Exception.");
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
    public Response RetrieveCustomer(@Context HttpHeaders headers, String jsonTxt){

        ServiceLogger.LOGGER.info("Received request to Retrieve customer");

        CustomerRequestModel1 requestModel;
        CustomerResponseModel responseModel;

        ObjectMapper mapper = new ObjectMapper();
        ServiceLogger.LOGGER.info("jsonTxt = " + jsonTxt);

        try{

            requestModel = mapper.readValue(jsonTxt, CustomerRequestModel1.class);


            responseModel = CustomerDBQueries.RetrieveCustomer(requestModel);
            if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();}
            return Response.status(Response.Status.OK).entity(responseModel).build();

        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new CustomerResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new CustomerResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

            } else {
                ServiceLogger.LOGGER.warning("IOException.");

            }

        }
        return Response.status(Response.Status.OK).build();

    }

}
