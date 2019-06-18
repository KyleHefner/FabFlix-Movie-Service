package edu.uci.ics.khefner.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.uci.ics.khefner.service.movies.DatabaseQueries.SearchMovie;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.SearchMovie.SearchMovieRequestModel;
import edu.uci.ics.khefner.service.movies.models.SearchMovie.SearchMovieResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;

@Path("search")
public class SearchMoviePage {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response SearchMovies(@Context HttpHeaders headers,
                                 @QueryParam("title") String title,
                                 @QueryParam("genre") String genre,
                                 @QueryParam("year") int year,
                                 @QueryParam("director") String director,
                                 @DefaultValue("false")@QueryParam("hidden") Boolean hidden,
                                 @DefaultValue("0")@QueryParam("offset") int offset,
                                 @DefaultValue("10")@QueryParam("limit") int limit,
                                 @DefaultValue("rating")@QueryParam("orderby") String orderBy,
                                 @DefaultValue("DESC")@QueryParam("direction") String direction) {

        ServiceLogger.LOGGER.info("Received Request to Search Movie ");
        ServiceLogger.LOGGER.info("title: " + title);
        ServiceLogger.LOGGER.info("genre: " + genre);
        ServiceLogger.LOGGER.info("year: " + year);
        ServiceLogger.LOGGER.info("director: " + director);
        ServiceLogger.LOGGER.info("hidden: " + hidden);
        ServiceLogger.LOGGER.info("offset: " + offset);
        ServiceLogger.LOGGER.info("limit " + limit);
        ServiceLogger.LOGGER.info("direction: " + direction);
        ServiceLogger.LOGGER.info("orderBy: " + orderBy);

        String email = headers.getHeaderString("email");
        String transactionID = headers.getHeaderString("transactionID");
        String sessionID = headers.getHeaderString("sessionID");
        ObjectMapper mapper = new ObjectMapper();
        SearchMovieRequestModel requestModel;
        SearchMovieResponseModel responseModel;

        try {
            ObjectNode obj = mapper.createObjectNode();
            obj.put("title", title);
            obj.put("genre", genre);
            obj.put("year", year);
            obj.put("director", director);
            obj.put("hidden", hidden);
            obj.put("offset", offset);
            obj.put("limit", limit);
            obj.put("direction", direction);
            obj.put("orderBy", orderBy);

            ServiceLogger.LOGGER.info("Json txt: " + obj.toString());
            JsonNode node = mapper.readTree(obj.toString());
            ServiceLogger.LOGGER.info("Json node: " + node.toString());

            requestModel = mapper.readValue(node.toString(), SearchMovieRequestModel.class);


            responseModel = SearchMovie.GetMovie(requestModel, email);

            ServiceLogger.LOGGER.info("built response model");

            if(responseModel.getResultCode() == -1){return Response.status(Status.INTERNAL_SERVER_ERROR).build();}
            return Response.status(Status.OK).entity(responseModel).header("email", email ).header("sessionID", sessionID).header("transactionID", transactionID).build();

        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");

            } else {
                ServiceLogger.LOGGER.warning("IOException.");
            }

        }


        return Response.status(Status.OK).build();
    }



}