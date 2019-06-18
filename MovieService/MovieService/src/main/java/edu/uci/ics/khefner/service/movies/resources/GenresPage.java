package edu.uci.ics.khefner.service.movies.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.khefner.service.movies.DatabaseQueries.AddGenre;
import edu.uci.ics.khefner.service.movies.DatabaseQueries.GetAllGenres;
import edu.uci.ics.khefner.service.movies.DatabaseQueries.SearchGenreByID;
import edu.uci.ics.khefner.service.movies.core.CheckPrivilege;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.AddGenre.AddGenreRequestModel;
import edu.uci.ics.khefner.service.movies.models.AddGenre.AddGenreResponseModel;
import edu.uci.ics.khefner.service.movies.models.GetAllGenresResponseModel;
import edu.uci.ics.khefner.service.movies.models.SearchGenreByIdResponseModel;
import edu.uci.ics.khefner.service.movies.models.VerifyPrivilege.VerifyPrivilegeResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;

@Path("genre")
public class GenresPage {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response GetAllGenres(@Context HttpHeaders headers){

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");


        GetAllGenresResponseModel responseModel = GetAllGenres.GetGenres();

        if(responseModel.getResultCode() == -1){return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();}
        return Response.status(Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
    }

    @Path("add")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response AddGenre(@Context HttpHeaders headers, String jsonTxt){


        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");


        ServiceLogger.LOGGER.info("received request to add genre");

        boolean hasPrivilege = CheckPrivilege.hasPrivilege(headers.getHeaderString("email"), 3);

        if(!hasPrivilege){
            ServiceLogger.LOGGER.info("Client does not have sufficient privilege.");
            VerifyPrivilegeResponseModel responseModel = new VerifyPrivilegeResponseModel(141, "User has insufficient privilege.");
            return Response.status(Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
        }


        ObjectMapper mapper = new ObjectMapper();
        AddGenreRequestModel requestModel;

        try{

            requestModel = mapper.readValue(jsonTxt, AddGenreRequestModel.class);
            AddGenreResponseModel responseModel = AddGenre.AddGenre(requestModel);

            ServiceLogger.LOGGER.info("Response model message: " + responseModel.getMessage());

            if(responseModel.getResultCode() == -1){return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();}
            return Response.status(Response.Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                AddGenreResponseModel responseModel = new AddGenreResponseModel(-2, "JSON mapping exception");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                AddGenreResponseModel responseModel = new AddGenreResponseModel(-3, "JSON parse exception");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

            } else {
                ServiceLogger.LOGGER.warning("IOException.");

            }
        }
        return Response.status(Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
    }
    @Path("{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)

    public Response GetGenreById(@PathParam("movieid") String movieid, @Context HttpHeaders headers){

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");


        ServiceLogger.LOGGER.info("received request to add genre");

        boolean hasPrivilege = CheckPrivilege.hasPrivilege(headers.getHeaderString("email"), 3);

        if(!hasPrivilege){
            ServiceLogger.LOGGER.info("Client does not have sufficient privilege.");
            VerifyPrivilegeResponseModel responseModel = new VerifyPrivilegeResponseModel(141, "User has insufficient privilege.");
            return Response.status(Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
        }

        SearchGenreByIdResponseModel responseModel = SearchGenreByID.GetGenreByID(movieid, email);

        if(responseModel.getResultCode() == -1)
        {return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email ).header("sessionID", sessionID).header("transactionID", transactionID).build();}

        if(responseModel.getResultCode() == 141)
        {
            //build a verify privilege response model and return it
            VerifyPrivilegeResponseModel responseModel1 = new VerifyPrivilegeResponseModel( 141, "User has insufficient privilege.");
            return Response.status(Response.Status.OK).entity(responseModel1).header("email", email ).header("sessionID", sessionID).header("transactionID", transactionID).build();
        }
        return Response.status(Response.Status.OK).entity(responseModel).header("email", email ).header("sessionID", sessionID).header("transactionID", transactionID).build();

    }


}
