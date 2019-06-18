package edu.uci.ics.khefner.service.movies.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.khefner.service.movies.DatabaseQueries.AddMovie;
import edu.uci.ics.khefner.service.movies.core.CheckPrivilege;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.AddMovie.AddMovieRequestModel;
import edu.uci.ics.khefner.service.movies.models.AddMovie.AddMovieResponseModel;
import edu.uci.ics.khefner.service.movies.models.VerifyPrivilege.VerifyPrivilegeResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("add")
public class AddMoviePage {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)

    public Response AddMovie(@Context HttpHeaders headers, String jsonTxt){
        ServiceLogger.LOGGER.info("received request to add movie");

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");

        //need to verify privilege of Client...
        boolean hasPrivilege = CheckPrivilege.hasPrivilege(email, 3);

        if(!hasPrivilege){
            VerifyPrivilegeResponseModel responseModel = new VerifyPrivilegeResponseModel(141, "User has insufficient privilege.");
            return Response.status(Response.Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
        }


        ObjectMapper mapper = new ObjectMapper();
        AddMovieRequestModel requestModel;

        try{
            requestModel = mapper.readValue(jsonTxt, AddMovieRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            AddMovieResponseModel responseModel = AddMovie.AddMovie(requestModel, headers.getHeaderString("email"));
            if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email ).header("sessionID", sessionID).header("transactionID", transactionID).build();}

            return Response.status(Response.Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                AddMovieResponseModel responseModel = new AddMovieResponseModel(-2, "JSON mapping exception");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                AddMovieResponseModel responseModel = new AddMovieResponseModel(-3, "JSON parse exception");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

            } else {
                ServiceLogger.LOGGER.warning("IOException.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
            }

        }

    }
}
