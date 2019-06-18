package edu.uci.ics.khefner.service.api_gateway.resources;

import edu.uci.ics.khefner.service.api_gateway.GatewayService;
import edu.uci.ics.khefner.service.api_gateway.exceptions.ModelValidationException;
import edu.uci.ics.khefner.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.khefner.service.api_gateway.models.NoContentResponseModel;
import edu.uci.ics.khefner.service.api_gateway.models.billing.Cart.CartRequestModel;
import edu.uci.ics.khefner.service.api_gateway.models.billing.Cart.CartRequestModel2;
import edu.uci.ics.khefner.service.api_gateway.models.billing.Cart.CartRequestModel3;
import edu.uci.ics.khefner.service.api_gateway.models.billing.CreditCard.CreditCardRequestModel;
import edu.uci.ics.khefner.service.api_gateway.models.billing.CreditCard.CreditCardRequestModel2;
import edu.uci.ics.khefner.service.api_gateway.models.billing.Customer.CustomerRequestModel;
import edu.uci.ics.khefner.service.api_gateway.models.billing.Customer.CustomerRequestModel2;
import edu.uci.ics.khefner.service.api_gateway.models.billing.OrderRequestModel;
import edu.uci.ics.khefner.service.api_gateway.models.idm.Session.VerifySessionResponseModel;
import edu.uci.ics.khefner.service.api_gateway.threadpool.ClientRequest;
import edu.uci.ics.khefner.service.api_gateway.utilities.ModelValidator;
import edu.uci.ics.khefner.service.api_gateway.utilities.TransactionIDGenerator;
import edu.uci.ics.khefner.service.api_gateway.utilities.VerifySession;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("billing")
public class BillingEndpoints {
    @Path("cart/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertToCartRequest(String jsonText, @Context HttpHeaders headers) {

        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");

        if(email == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-16, "Email not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }
        if(sessionID == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        Response response = VerifySession.VerifySession(sessionID, email);
        String json = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("Json response: " + json);
        VerifySessionResponseModel responseModel = VerifySession.MaptoModel(json);
        ServiceLogger.LOGGER.info("responseModel.getresulstcode: " + responseModel.getResultCode());
        if(responseModel.getResultCode() != 130){
            ServiceLogger.LOGGER.info("Session is invalid...");
            return Response.status(response.getStatus()).entity(responseModel).build();
        }

        sessionID = responseModel.getSessionID();




        CartRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CartRequestModel) ModelValidator.verifyModel(jsonText, CartRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartInsert());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(email);
        //set the sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().add(cr);



        NoContentResponseModel responseModel1 = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("RequestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel1).build();
    }

    @Path("cart/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCartRequest(String jsonText,  @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");

        if(email == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-16, "Email not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }
        if(sessionID == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        Response response = VerifySession.VerifySession(sessionID, email);
        String json = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("Json response: " + json);
        VerifySessionResponseModel responseModel = VerifySession.MaptoModel(json);
        ServiceLogger.LOGGER.info("responseModel.getresulstcode: " + responseModel.getResultCode());
        if(responseModel.getResultCode() != 130){
            ServiceLogger.LOGGER.info("Session is invalid...");
            return Response.status(response.getStatus()).entity(responseModel).build();
        }
        sessionID = responseModel.getSessionID();


        CartRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CartRequestModel) ModelValidator.verifyModel(jsonText, CartRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartUpdate());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(email);
        //set the sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().add(cr);



        NoContentResponseModel responseModel1 = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("RequestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel1).build();
    }

    @Path("cart/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCartRequest(String jsonText,  @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");

        if(email == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-16, "Email not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }
        if(sessionID == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        Response response = VerifySession.VerifySession(sessionID, email);
        String json = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("Json response: " + json);
        VerifySessionResponseModel responseModel = VerifySession.MaptoModel(json);
        ServiceLogger.LOGGER.info("responseModel.getresulstcode: " + responseModel.getResultCode());
        if(responseModel.getResultCode() != 130){
            ServiceLogger.LOGGER.info("Session is invalid...");
            return Response.status(response.getStatus()).entity(responseModel).build();
        }
        sessionID = responseModel.getSessionID();


        CartRequestModel2 requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CartRequestModel2) ModelValidator.verifyModel(jsonText, CartRequestModel2.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartDelete());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(email);
        //set the sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().add(cr);



        NoContentResponseModel responseModel1 = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("RequestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel1).build();
    }

    @Path("cart/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCartRequest(String jsonText,  @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");

        if(email == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-16, "Email not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }
        if(sessionID == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        Response response = VerifySession.VerifySession(sessionID, email);
        String json = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("Json response: " + json);
        VerifySessionResponseModel responseModel = VerifySession.MaptoModel(json);
        ServiceLogger.LOGGER.info("responseModel.getresulstcode: " + responseModel.getResultCode());
        if(responseModel.getResultCode() != 130){
            ServiceLogger.LOGGER.info("Session is invalid...");
            return Response.status(response.getStatus()).entity(responseModel).build();
        }
        sessionID = responseModel.getSessionID();


        CartRequestModel3 requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CartRequestModel3) ModelValidator.verifyModel(jsonText, CartRequestModel3.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(email);
        //set the sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().add(cr);



        NoContentResponseModel responseModel1 = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("RequestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel1).build();
    }

    @Path("cart/clear")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearCartRequest(String jsonText,  @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");

        if(email == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-16, "Email not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }
        if(sessionID == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        Response response = VerifySession.VerifySession(sessionID, email);
        String json = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("Json response: " + json);
        VerifySessionResponseModel responseModel = VerifySession.MaptoModel(json);
        ServiceLogger.LOGGER.info("responseModel.getresulstcode: " + responseModel.getResultCode());
        if(responseModel.getResultCode() != 130){
            ServiceLogger.LOGGER.info("Session is invalid...");
            return Response.status(response.getStatus()).entity(responseModel).build();
        }
        sessionID = responseModel.getSessionID();


        CartRequestModel3 requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CartRequestModel3) ModelValidator.verifyModel(jsonText, CartRequestModel3.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartClear());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(email);
        //set the sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().add(cr);



        NoContentResponseModel responseModel1 = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("RequestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel1).build();
    }

    @Path("creditcard/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCreditCardRequest(String jsonText,  @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");

        if(email == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-16, "Email not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }
        if(sessionID == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        Response response = VerifySession.VerifySession(sessionID, email);
        String json = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("Json response: " + json);
        VerifySessionResponseModel responseModel = VerifySession.MaptoModel(json);
        ServiceLogger.LOGGER.info("responseModel.getresulstcode: " + responseModel.getResultCode());
        if(responseModel.getResultCode() != 130){
            ServiceLogger.LOGGER.info("Session is invalid...");
            return Response.status(response.getStatus()).entity(responseModel).build();
        }
        sessionID = responseModel.getSessionID();


        CreditCardRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CreditCardRequestModel) ModelValidator.verifyModel(jsonText, CreditCardRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcInsert());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(email);
        //set the sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().add(cr);



        NoContentResponseModel responseModel1 = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel1).build();
    }

    @Path("creditcard/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCreditCardRequest(String jsonText,  @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");

        if(email == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-16, "Email not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }
        if(sessionID == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        Response response = VerifySession.VerifySession(sessionID, email);
        String json = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("Json response: " + json);
        VerifySessionResponseModel responseModel = VerifySession.MaptoModel(json);
        ServiceLogger.LOGGER.info("responseModel.getresulstcode: " + responseModel.getResultCode());
        if(responseModel.getResultCode() != 130){
            ServiceLogger.LOGGER.info("Session is invalid...");
            return Response.status(response.getStatus()).entity(responseModel).build();
        }
        sessionID = responseModel.getSessionID();


        CreditCardRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CreditCardRequestModel) ModelValidator.verifyModel(jsonText, CreditCardRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcUpdate());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(email);
        //set the sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().add(cr);



        NoContentResponseModel responseModel1 = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel1).build();
    }

    @Path("creditcard/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCreditCardRequest(String jsonText,  @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");

        if(email == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-16, "Email not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }
        if(sessionID == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        Response response = VerifySession.VerifySession(sessionID, email);
        String json = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("Json response: " + json);
        VerifySessionResponseModel responseModel = VerifySession.MaptoModel(json);
        ServiceLogger.LOGGER.info("responseModel.getresulstcode: " + responseModel.getResultCode());
        if(responseModel.getResultCode() != 130){
            ServiceLogger.LOGGER.info("Session is invalid...");
            return Response.status(response.getStatus()).entity(responseModel).build();
        }
        sessionID = responseModel.getSessionID();


        CreditCardRequestModel2 requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CreditCardRequestModel2) ModelValidator.verifyModel(jsonText, CreditCardRequestModel2.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcDelete());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(email);
        //set the sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().add(cr);



        NoContentResponseModel responseModel1 = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel1).build();
    }

    @Path("creditcard/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCreditCardRequest(String jsonText,  @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");

        if(email == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-16, "Email not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }
        if(sessionID == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        Response response = VerifySession.VerifySession(sessionID, email);
        String json = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("Json response: " + json);
        VerifySessionResponseModel responseModel = VerifySession.MaptoModel(json);
        ServiceLogger.LOGGER.info("responseModel.getresulstcode: " + responseModel.getResultCode());
        if(responseModel.getResultCode() != 130){
            ServiceLogger.LOGGER.info("Session is invalid...");
            return Response.status(response.getStatus()).entity(responseModel).build();
        }
        sessionID = responseModel.getSessionID();


        CreditCardRequestModel2 requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CreditCardRequestModel2) ModelValidator.verifyModel(jsonText, CreditCardRequestModel2.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(email);
        //set the sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().add(cr);



        NoContentResponseModel responseModel1 = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel1).build();
    }

    @Path("customer/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCustomerRequest(String jsonText,  @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");

        if(email == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-16, "Email not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }
        if(sessionID == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        Response response = VerifySession.VerifySession(sessionID, email);
        String json = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("Json response: " + json);
        VerifySessionResponseModel responseModel = VerifySession.MaptoModel(json);
        ServiceLogger.LOGGER.info("responseModel.getresulstcode: " + responseModel.getResultCode());
        if(responseModel.getResultCode() != 130){
            ServiceLogger.LOGGER.info("Session is invalid...");
            return Response.status(response.getStatus()).entity(responseModel).build();
        }
        sessionID = responseModel.getSessionID();


        CustomerRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CustomerRequestModel) ModelValidator.verifyModel(jsonText, CustomerRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerInsert());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(email);
        //set the sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().add(cr);



        NoContentResponseModel responseModel1 = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("RequestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel1).build();
    }

    @Path("customer/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomerRequest(String jsonText,  @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");

        if(email == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-16, "Email not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }
        if(sessionID == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        Response response = VerifySession.VerifySession(sessionID, email);
        String json = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("Json response: " + json);
        VerifySessionResponseModel responseModel = VerifySession.MaptoModel(json);
        ServiceLogger.LOGGER.info("responseModel.getresulstcode: " + responseModel.getResultCode());
        if(responseModel.getResultCode() != 130){
            ServiceLogger.LOGGER.info("Session is invalid...");
            return Response.status(response.getStatus()).entity(responseModel).build();
        }
        sessionID = responseModel.getSessionID();


        CustomerRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CustomerRequestModel) ModelValidator.verifyModel(jsonText, CustomerRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerUpdate());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(email);
        //set the sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().add(cr);



        NoContentResponseModel responseModel1 = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel1).build();
    }

    @Path("customer/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerRequest(String jsonText,  @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");

        if(email == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-16, "Email not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }
        if(sessionID == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        Response response = VerifySession.VerifySession(sessionID, email);
        String json = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("Json response: " + json);
        VerifySessionResponseModel responseModel = VerifySession.MaptoModel(json);
        ServiceLogger.LOGGER.info("responseModel.getresulstcode: " + responseModel.getResultCode());
        if(responseModel.getResultCode() != 130){
            ServiceLogger.LOGGER.info("Session is invalid...");
            return Response.status(response.getStatus()).entity(responseModel).build();
        }
        sessionID = responseModel.getSessionID();


        CustomerRequestModel2 requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CustomerRequestModel2) ModelValidator.verifyModel(jsonText, CustomerRequestModel2.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(email);
        //set the sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().add(cr);



        NoContentResponseModel responseModel1 = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel1).build();
    }

    @Path("order/place")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response placeOrderRequest(String jsonText,  @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");

        if(email == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-16, "Email not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }
        if(sessionID == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        Response response = VerifySession.VerifySession(sessionID, email);
        String json = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("Json response: " + json);
        VerifySessionResponseModel responseModel = VerifySession.MaptoModel(json);
        ServiceLogger.LOGGER.info("responseModel.getresulstcode: " + responseModel.getResultCode());
        if(responseModel.getResultCode() != 130){
            ServiceLogger.LOGGER.info("Session is invalid...");
            return Response.status(response.getStatus()).entity(responseModel).build();
        }
        sessionID = responseModel.getSessionID();


        OrderRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (OrderRequestModel) ModelValidator.verifyModel(jsonText, OrderRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPOrderPlace());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(email);
        //set the sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().add(cr);



        NoContentResponseModel responseModel1 = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("RequestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel1).build();
    }

    @Path("order/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveOrderRequest(String jsonText,  @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");

        if(email == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-16, "Email not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }
        if(sessionID == null){

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        Response response = VerifySession.VerifySession(sessionID, email);
        String json = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("Json response: " + json);
        VerifySessionResponseModel responseModel = VerifySession.MaptoModel(json);
        ServiceLogger.LOGGER.info("responseModel.getresulstcode: " + responseModel.getResultCode());
        if(responseModel.getResultCode() != 130){
            ServiceLogger.LOGGER.info("Session is invalid...");
            return Response.status(response.getStatus()).entity(responseModel).build();
        }
        sessionID = responseModel.getSessionID();


        OrderRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (OrderRequestModel) ModelValidator.verifyModel(jsonText, OrderRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPOrderRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(email);
        //set the sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().add(cr);



        NoContentResponseModel responseModel1 = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header("RequestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel1).build();
    }
}
