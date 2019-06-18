package edu.uci.ics.khefner.service.movies.resources;

import edu.uci.ics.khefner.service.movies.DatabaseQueries.SearchMovieByID;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;

import edu.uci.ics.khefner.service.movies.models.SearchMovieByIdResponseModel;
import edu.uci.ics.khefner.service.movies.models.VerifyPrivilege.VerifyPrivilegeResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("get")
public class GetMovieByIDPage {

    @Path("{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response GetMovieByID(@PathParam("movieid") String movieid, @Context HttpHeaders headers) {

        ServiceLogger.LOGGER.info("Received request to get Movie with id: " + movieid);

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");

        SearchMovieByIdResponseModel responseModel = SearchMovieByID.GetMovieByID(movieid, email);


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
