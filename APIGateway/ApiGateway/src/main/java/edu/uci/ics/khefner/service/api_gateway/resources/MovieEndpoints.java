package edu.uci.ics.khefner.service.api_gateway.resources;

import edu.uci.ics.khefner.service.api_gateway.GatewayService;
import edu.uci.ics.khefner.service.api_gateway.exceptions.ModelValidationException;
import edu.uci.ics.khefner.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.khefner.service.api_gateway.models.NoContentResponseModel;
import edu.uci.ics.khefner.service.api_gateway.models.idm.Session.VerifySessionResponseModel;
import edu.uci.ics.khefner.service.api_gateway.models.idm.VerifyPrivilegeRequestModel;
import edu.uci.ics.khefner.service.api_gateway.models.movies.*;
import edu.uci.ics.khefner.service.api_gateway.threadpool.ClientRequest;
import edu.uci.ics.khefner.service.api_gateway.utilities.ModelValidator;
import edu.uci.ics.khefner.service.api_gateway.utilities.TransactionIDGenerator;
import edu.uci.ics.khefner.service.api_gateway.utilities.VerifySession;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import java.util.Map;

@Path("movies")
public class MovieEndpoints {
    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchMovieRequest(@Context HttpHeaders headers, @Context UriInfo uriInfo) {

        ServiceLogger.LOGGER.info("received request to get a movie");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");

        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
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




        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();

        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());

/*        String query = uriInfo.getRequestUri().getQuery();
        String endpoint = GatewayService.getMovieConfigs().getEPMovieSearch();

        endpoint = endpoint + "?" + query;*/
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        cr.setQueryParams(queryParams);

        cr.setHasQueryParams(true);

        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieSearch());

        cr.setMethod("GET");

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

    @Path("get/{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovieRequest(@Context HttpHeaders headers, @PathParam("movieid") String movieid) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        ServiceLogger.LOGGER.info("Received request to get movie by id.");

        //need to verify sessionID with the IDM
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("email: " + email);
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


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        String endpoint = GatewayService.getMovieConfigs().getEPMovieGet();
        endpoint = endpoint + "/" + movieid;
        cr.setEndpoint(endpoint);


        cr.setMethod("GET");
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

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMovieRequest(String jsonText, @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        ServiceLogger.LOGGER.info("Received request to add movie.");

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
        AddMovieRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (AddMovieRequestModel) ModelValidator.verifyModel(jsonText, AddMovieRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }
        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieAdd());
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

    @Path("delete/{movieid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMovieRequest(@Context HttpHeaders headers, @PathParam("movieid") String movieid) {
        ServiceLogger.LOGGER.info("Received request to delete movie.");

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


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();

        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());

        String endpoint = GatewayService.getMovieConfigs().getEPMovieDelete();
        endpoint = endpoint + "/" + movieid;
        cr.setEndpoint(endpoint);

        cr.setMethod("DELETE");

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

    @Path("genre")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenresRequest(@Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("Received request to get genre list.");

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


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();

        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());

        cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreGet());

        cr.setMethod("GET");

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

    @Path("genre/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addGenreRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        ServiceLogger.LOGGER.info("Received request to add genre.");

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
        AddGenreRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (AddGenreRequestModel) ModelValidator.verifyModel(jsonText, AddGenreRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }
        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreAdd());

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

    @Path("genre/{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenresForMovieRequest(@Context HttpHeaders headers, @PathParam("movieid") String movieid) {
        ServiceLogger.LOGGER.info("Received request to get genre by id.");

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


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();

        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());

        String endpoint = GatewayService.getMovieConfigs().getEPGenreMovie();
        endpoint = endpoint + "/" + movieid;
        cr.setEndpoint(endpoint);

        cr.setMethod("GET");

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

    @Path("star/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response starSearchRequest(@Context HttpHeaders headers, @Context UriInfo uriInfo) {

        ServiceLogger.LOGGER.info("received request to get a movie");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        if (email == null) {

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-16, "Email not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }
        if (sessionID == null) {

            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header.");
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        Response response = VerifySession.VerifySession(sessionID, email);
        String json = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("Json response: " + json);
        VerifySessionResponseModel responseModel = VerifySession.MaptoModel(json);
        ServiceLogger.LOGGER.info("responseModel.getresulstcode: " + responseModel.getResultCode());
        if (responseModel.getResultCode() != 130) {
            ServiceLogger.LOGGER.info("Session is invalid...");
            return Response.status(response.getStatus()).entity(responseModel).build();
        }
        sessionID = responseModel.getSessionID();


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();

        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());

/*        String query = uriInfo.getRequestUri().getQuery();
        String endpoint = GatewayService.getMovieConfigs().getEPMovieSearch();

        endpoint = endpoint + "?" + query;*/
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        cr.setQueryParams(queryParams);

        cr.setHasQueryParams(true);

        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarSearch());

        cr.setMethod("GET");

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

    @Path("star/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStarRequest(@Context HttpHeaders headers, @PathParam("id") String id) {
        ServiceLogger.LOGGER.info("Received request to get star by id.");

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


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();

        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());

        String endpoint = GatewayService.getMovieConfigs().getEPStarGet();
        endpoint = endpoint + "/" + id;
        cr.setEndpoint(endpoint);

        cr.setMethod("GET");

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

    @Path("star/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStarRequest(String jsonText, @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        ServiceLogger.LOGGER.info("Received request to add Star.");

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
        AddStarRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (AddStarRequestModel) ModelValidator.verifyModel(jsonText, AddStarRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }
        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarAdd());
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

    @Path("star/starsin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStarToMovieRequest(String jsonText, @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        ServiceLogger.LOGGER.info("Received request to add Star to Movie.");

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
        StarsInRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (StarsInRequestModel) ModelValidator.verifyModel(jsonText, StarsInRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }
        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarIn());
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

    @Path("rating")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRatingRequest(String jsonText, @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("\n\n\n\n\n\n\n\n");
        ServiceLogger.LOGGER.info("Received request to Update rating.");
        ServiceLogger.LOGGER.info("Json text: " + jsonText);
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
        RatingRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (RatingRequestModel) ModelValidator.verifyModel(jsonText, RatingRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e);
        }
        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPRating());
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
