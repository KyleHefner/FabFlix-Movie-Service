package edu.uci.ics.khefner.service.movies.resources;

import edu.uci.ics.khefner.service.movies.DatabaseQueries.DeleteMovie;
import edu.uci.ics.khefner.service.movies.MovieService;
import edu.uci.ics.khefner.service.movies.core.CheckPrivilege;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.DeleteMovieResponseModel;
import edu.uci.ics.khefner.service.movies.models.VerifyPrivilege.VerifyPrivilegeResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("delete")
public class DeleteMoviePage {

    @Path("{movieid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response DeleteMovie(@PathParam("movieid") String movieid, @Context HttpHeaders headers){

        ServiceLogger.LOGGER.info("Received request to delete Movie with id: " + movieid);


        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");

        //need to verify privilege of Client...
        boolean hasPrivilege = CheckPrivilege.hasPrivilege(email, 3);

        if(!hasPrivilege){
            VerifyPrivilegeResponseModel responseModel = new VerifyPrivilegeResponseModel(141, "User has insufficient privilege.");
            return Response.status(Response.Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
        }

        DeleteMovieResponseModel responseModel = DeleteMovie.DeleteMovie(movieid);

        if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email ).header("sessionID", sessionID).header("transactionID", transactionID).build();}

        return Response.status(Response.Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();


    }
}
