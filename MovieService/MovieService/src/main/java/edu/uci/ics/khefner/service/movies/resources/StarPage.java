package edu.uci.ics.khefner.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.uci.ics.khefner.service.movies.DatabaseQueries.*;
import edu.uci.ics.khefner.service.movies.core.CheckPrivilege;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.AddMovie.AddMovieRequestModel;
import edu.uci.ics.khefner.service.movies.models.AddMovie.AddMovieResponseModel;
import edu.uci.ics.khefner.service.movies.models.AddStar.AddStarRequestModel;
import edu.uci.ics.khefner.service.movies.models.AddStar.AddStarResponseModel;
import edu.uci.ics.khefner.service.movies.models.SearchMovie.SearchMovieRequestModel;
import edu.uci.ics.khefner.service.movies.models.SearchMovie.SearchMovieResponseModel;
import edu.uci.ics.khefner.service.movies.models.SearchStar.SearchStarRequestModel;
import edu.uci.ics.khefner.service.movies.models.SearchStar.SearchStarResponseModel;
import edu.uci.ics.khefner.service.movies.models.SearchStarByIdResponseModel;
import edu.uci.ics.khefner.service.movies.models.StarsIn.StarsInRequestModel;
import edu.uci.ics.khefner.service.movies.models.StarsIn.StarsInResponseModel;
import edu.uci.ics.khefner.service.movies.models.VerifyPrivilege.VerifyPrivilegeResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("star")
public class StarPage {

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response GetStarById(@PathParam("id") String id, @Context HttpHeaders headers){
        ServiceLogger.LOGGER.info("Received request to get star by starID");

        String email = headers.getHeaderString("email");
        String transactionID = headers.getHeaderString("transactionID");
        String sessionID = headers.getHeaderString("sessionID");


        SearchStarByIdResponseModel responseModel;

        responseModel = SearchStarByID.GetStarById(id);

        ServiceLogger.LOGGER.info("built response model");

        if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();}
        return Response.status(Response.Status.OK).entity(responseModel).header("email", email ).header("sessionID", sessionID).header("transactionID", transactionID).build();


    }

    @Path("add")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response AddStar(String jsonTxt, @Context HttpHeaders headers){

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");

        //need to verify privilege of Client...
        boolean hasPrivilege = CheckPrivilege.hasPrivilege(email, 3);

        if(!hasPrivilege){
            VerifyPrivilegeResponseModel responseModel = new VerifyPrivilegeResponseModel(141, "User has insufficient privilege.");
            return Response.status(Response.Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
        }

        AddStarRequestModel requestModel;
        ObjectMapper mapper = new ObjectMapper();

        try{
            requestModel = mapper.readValue(jsonTxt, AddStarRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            AddStarResponseModel responseModel = AddStar.AddStar(requestModel);
            if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email ).header("sessionID", sessionID).header("transactionID", transactionID).build();}

            return Response.status(Response.Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                AddStarResponseModel responseModel = new AddStarResponseModel(-2, "JSON mapping exception");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                AddStarResponseModel responseModel = new AddStarResponseModel(-3, "JSON parse exception");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

            } else {
                ServiceLogger.LOGGER.warning("IOException.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
            }

        }

    }

    @Path("starsin")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response StarsIn(String jsonTxt, @Context HttpHeaders headers){
        ServiceLogger.LOGGER.info("Received request to add star to a movie");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");

        //need to verify privilege of Client...
        boolean hasPrivilege = CheckPrivilege.hasPrivilege(email, 3);

        if(!hasPrivilege){
            VerifyPrivilegeResponseModel responseModel = new VerifyPrivilegeResponseModel(141, "User has insufficient privilege.");
            return Response.status(Response.Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
        }

        StarsInRequestModel requestModel;
        ObjectMapper mapper = new ObjectMapper();

        try{
            requestModel = mapper.readValue(jsonTxt, StarsInRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            StarsInResponseModel responseModel = AddStarToMovie.AddStarToMovie(requestModel);
            if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email ).header("sessionID", sessionID).header("transactionID", transactionID).build();}

            return Response.status(Response.Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                AddStarResponseModel responseModel = new AddStarResponseModel(-2, "JSON mapping exception");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                AddStarResponseModel responseModel = new AddStarResponseModel(-3, "JSON parse exception");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();

            } else {
                ServiceLogger.LOGGER.warning("IOException.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
            }

        }

    }

    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response SearchStar(@Context HttpHeaders headers,
                               @QueryParam("name") String name,
                               @QueryParam("movieTitle") String movieTitle,
                               @QueryParam("birthYear") int birthYear,
                               @DefaultValue("0")@QueryParam("offset") int offset,
                               @DefaultValue("10")@QueryParam("limit") int limit,
                               @DefaultValue("name")@QueryParam("orderby") String orderBy,
                               @DefaultValue("ASC")@QueryParam("direction") String direction){

        ServiceLogger.LOGGER.info("Received Request to Search Star ");
        ServiceLogger.LOGGER.info("movieTitle: " + movieTitle);
        ServiceLogger.LOGGER.info("name: " + name);
        ServiceLogger.LOGGER.info("birthYear: " + birthYear);
        ServiceLogger.LOGGER.info("offset: " + offset);
        ServiceLogger.LOGGER.info("limit " + limit);
        ServiceLogger.LOGGER.info("direction: " + direction);
        ServiceLogger.LOGGER.info("orderBy: " + orderBy);

        String email = headers.getHeaderString("email");
        String transactionID = headers.getHeaderString("transactionID");
        String sessionID = headers.getHeaderString("sessionID");
        ObjectMapper mapper = new ObjectMapper();
        SearchStarRequestModel requestModel;
        SearchStarResponseModel responseModel;

        try {
            ObjectNode obj = mapper.createObjectNode();
            obj.put("name", name);
            obj.put("movieTitle", movieTitle);
            obj.put("birthYear", birthYear);
            obj.put("offset", offset);
            obj.put("limit", limit);
            obj.put("direction", direction);
            obj.put("orderBy", orderBy);

            ServiceLogger.LOGGER.info("Json txt: " + obj.toString());
            JsonNode node = mapper.readTree(obj.toString());
            ServiceLogger.LOGGER.info("Json node: " + node.toString());

            requestModel = mapper.readValue(node.toString(), SearchStarRequestModel.class);


            responseModel = SearchStar.SearchStar(requestModel);

            ServiceLogger.LOGGER.info("built response model");

            if(responseModel.getResultCode() == -1){return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();}
            return Response.status(Response.Status.OK).entity(responseModel).header("email", email ).header("sessionID", sessionID).header("transactionID", transactionID).build();

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

        return Response.status(Response.Status.OK).build();

    }
}
