package edu.uci.ics.khefner.service.movies.DatabaseQueries;

import edu.uci.ics.khefner.service.movies.MovieService;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.StarsIn.StarsInRequestModel;
import edu.uci.ics.khefner.service.movies.models.StarsIn.StarsInResponseModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddStarToMovie {

    public static StarsInResponseModel AddStarToMovie(StarsInRequestModel requestModel){

        String movieid = requestModel.getMovieid();
        String starid = requestModel.getStarid();

        if(!DoesMovieExist(movieid)){
            ServiceLogger.LOGGER.info("Movie does not exist...");
            StarsInResponseModel responseModel  = new StarsInResponseModel(211, "No movies found with search parameters.");
            return responseModel;
        }
        if(!DoesStarExist(starid)){
            ServiceLogger.LOGGER.info("Star does not exist...");
            StarsInResponseModel responseModel  = new StarsInResponseModel(231, "Could not add star to movie.");
            return responseModel;
        }

        if(DoesStarExistInMovie(starid, movieid)){
            ServiceLogger.LOGGER.info("Star already exists in movie...");
            StarsInResponseModel responseModel  = new StarsInResponseModel(232, "Star already exists in movie.");
            return responseModel;
        }
        //can safely add the star to the movie now...
        StarsInResponseModel responseModel = InsertStarInMovie(starid,movieid);
        return responseModel;


    }

    public static boolean DoesMovieExist(String movieid){

        try {
            String query = "SELECT * FROM movies WHERE movies.id = ? ";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, movieid);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if (!rs.next()) {
                ServiceLogger.LOGGER.info("Movie doesn't exist");

                return false;
            }
            ServiceLogger.LOGGER.info("Movie exists");
            return true;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable search Movie.");
            e.printStackTrace();
        }
        return false;
    }


    public static boolean DoesStarExist(String starid){

        try {
            String query = "SELECT * FROM stars WHERE stars.id = ? ";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, starid);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if (!rs.next()) {
                ServiceLogger.LOGGER.info("Star doesn't exist");

                return false;
            }
            ServiceLogger.LOGGER.info("Star exists");
            return true;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable search for Star.");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean DoesStarExistInMovie(String starid, String movieid){


        try {
            String query = "SELECT * FROM stars, movies, stars_in_movies WHERE stars.id = stars_in_movies.starId AND movies.id = stars_in_movies.movieId " +
                    " AND movies.id = ? AND stars.id = ?";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, movieid);
            ps.setString(2, starid);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if (!rs.next()) {
                ServiceLogger.LOGGER.info("Star doesn't exist In movie yet");

                return false;
            }
            ServiceLogger.LOGGER.info("Star exists in movie");
            return true;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable search for Star in Movie.");
            e.printStackTrace();
        }
        return true;

    }

    public static StarsInResponseModel InsertStarInMovie(String starid, String movieid){

        //only need to link star to movie by inserting into stars_in_movies table
        try {
            String query = "INSERT INTO stars_in_movies(starId, movieId) VALUES (?, ?)";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, starid);
            ps.setString(2, movieid);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query executed successfully");

            StarsInResponseModel responseModel = new StarsInResponseModel(230, "Star successfully added to movie.");
            return responseModel;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable search for Star in Movie.");
            e.printStackTrace();
        }
        StarsInResponseModel responseModel = new StarsInResponseModel(-1, "Internal server error.");
        return responseModel;

    }
}
