package edu.uci.ics.khefner.service.movies.DatabaseQueries;

import edu.uci.ics.khefner.service.movies.MovieService;
import edu.uci.ics.khefner.service.movies.core.Star;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.Rating.RatingRequestModel;
import edu.uci.ics.khefner.service.movies.models.Rating.RatingResponseModel;
import edu.uci.ics.khefner.service.movies.models.SearchStarByIdResponseModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateRating {

    public static RatingResponseModel UpdateRating(RatingRequestModel requestModel){
        String id = requestModel.getId();
        Float rating = requestModel.getRating();

        //need to check if movie exists...
        //use function from class AddStarToMovie

        if(!AddStarToMovie.DoesMovieExist(id)){
            ServiceLogger.LOGGER.info("movie does not exist...");
            RatingResponseModel responseModel = new RatingResponseModel(211, "No movies found with search parameters.");
        }

        //movie exists, now update its rating...
        //need to get original rating and numvotes...
        try{
        String query = "SELECT * FROM movies, ratings WHERE ratings.movieId = movies.id AND movies.id = ? ";
        PreparedStatement ps = MovieService.getCon().prepareStatement(query);
        ps.setString(1, id);

        ServiceLogger.LOGGER.info("Executing query " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query executed successfully");


        //getting rating and numvotes from movie...
        rs.first();
        Float oldRating = rs.getFloat("rating");
        Integer numVotes = rs.getInt("numVotes");

           // newRating + (oldRating * numVotes) / (numVotes + 1)
        Integer newNumVotes = numVotes + 1;
        Float newRating = (Float) rating + (oldRating * numVotes) / (newNumVotes);

        ServiceLogger.LOGGER.info("old rating: " + oldRating);
        ServiceLogger.LOGGER.info("new rating: " + newRating);
            ServiceLogger.LOGGER.info("old numvotes: " + numVotes);
            ServiceLogger.LOGGER.info("new numvotes: " + newNumVotes);

        //now update movie with neNumVotes and newRating...
        String query2 = "UPDATE ratings SET rating = ? AND numVotes = ? WHERE ratings.movieId = ?";
        PreparedStatement ps2 = MovieService.getCon().prepareStatement(query2);
        ps2.setFloat(1, newRating);
        ps2.setInt(2, newNumVotes);
        ps2.setString(3, id);

        ServiceLogger.LOGGER.info("Executing query " + ps2.toString());
        ps2.executeUpdate();
        ServiceLogger.LOGGER.info("Query executed successfully");

        RatingResponseModel responseModel = new RatingResponseModel(250, "Rating successfully updated.");
        return responseModel;



    }catch (
    SQLException e) {
        ServiceLogger.LOGGER.warning("Unable to update rating ");
        e.printStackTrace();
    }
        RatingResponseModel responseModel = new RatingResponseModel(-1, "Internal server error.");
        return responseModel;

    }
}
