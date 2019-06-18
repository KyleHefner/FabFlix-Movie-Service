package edu.uci.ics.khefner.service.api_gateway.resources;

import edu.uci.ics.khefner.service.api_gateway.GatewayService;
import edu.uci.ics.khefner.service.api_gateway.exceptions.ModelValidationException;
import edu.uci.ics.khefner.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.khefner.service.api_gateway.models.NoContentResponseModel;
import edu.uci.ics.khefner.service.api_gateway.models.idm.Login.LoginRequestModel;
import edu.uci.ics.khefner.service.api_gateway.models.idm.Login.LoginResponseModel;
import edu.uci.ics.khefner.service.api_gateway.models.idm.Register.RegisterUserRequestModel;
import edu.uci.ics.khefner.service.api_gateway.models.idm.Register.RegisterUserResponseModel;
import edu.uci.ics.khefner.service.api_gateway.models.idm.Session.VerifySessionRequestModel;
import edu.uci.ics.khefner.service.api_gateway.models.idm.Session.VerifySessionResponseModel;
import edu.uci.ics.khefner.service.api_gateway.models.idm.VerifyPrivilegeRequestModel;
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

@Path("idm")
public class IDMEndpoints {
    @Path("register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUserRequest(String jsonText) {
        ServiceLogger.LOGGER.info("Received request to register user.");
        RegisterUserRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (RegisterUserRequestModel) ModelValidator.verifyModel(jsonText, RegisterUserRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);//, RegisterUserResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getIdmConfigs().getEPUserRegister());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.

        GatewayService.getThreadPool().add(cr);

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).entity(responseModel).header("RequestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).build();
    }

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUserRequest(String jsonText) {
        ServiceLogger.LOGGER.info("Received request to Login user.");
        LoginRequestModel requestModel;

        ServiceLogger.LOGGER.info("json txt: " + jsonText);
        // Map jsonText to RequestModel
        try {
            requestModel = (LoginRequestModel) ModelValidator.verifyModel(jsonText, LoginRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);//, LoginResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getIdmConfigs().getEPUserLogin());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().add(cr);


        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).entity(responseModel).header("RequestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).build();
}

    @Path("session")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifySessionRequest(String jsonText) {
        ServiceLogger.LOGGER.info("Received request to verify session.");

        VerifySessionRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (VerifySessionRequestModel) ModelValidator.verifyModel(jsonText, VerifySessionRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);//, VerifySessionResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getIdmConfigs().getEPSessionVerify());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().add(cr);

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).entity(responseModel).header("RequestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("Access-Control-Allow-Origin", "*").build();
    }

    @Path("privilege")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyUserPrivilegeRequest(String jsonText, @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        ServiceLogger.LOGGER.info("Received request to verify privilege.");

        //need to verify sessionID with the IDM
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

        //----------------------------------------------------------------------End of Verifying session
        VerifyPrivilegeRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (VerifyPrivilegeRequestModel) ModelValidator.verifyModel(jsonText, VerifyPrivilegeRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);//, LoginResponseModel.class);
        }
        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getIdmConfigs().getEPUserPrivilegeVerify());
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
