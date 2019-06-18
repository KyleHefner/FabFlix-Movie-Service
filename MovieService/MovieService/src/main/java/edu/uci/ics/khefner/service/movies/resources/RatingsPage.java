package edu.uci.ics.khefner.service.movies.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.khefner.service.movies.DatabaseQueries.AddMovie;
import edu.uci.ics.khefner.service.movies.DatabaseQueries.UpdateRating;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.AddMovie.AddMovieRequestModel;
import edu.uci.ics.khefner.service.movies.models.AddMovie.AddMovieResponseModel;
import edu.uci.ics.khefner.service.movies.models.Rating.RatingRequestModel;
import edu.uci.ics.khefner.service.movies.models.Rating.RatingResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("rating")
public class RatingsPage {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response UpdateRating(String jsonTxt, @Context HttpHeaders headers){
        ServiceLogger.LOGGER.info("Received request to update rating for movie");

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");

        ObjectMapper mapper = new ObjectMapper();
        RatingRequestModel requestModel;

        try{
            requestModel = mapper.readValue(jsonTxt, RatingRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            RatingResponseModel responseModel = UpdateRating.UpdateRating(requestModel);
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
