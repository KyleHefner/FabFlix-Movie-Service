package edu.uci.ics.khefner.service.movies.DatabaseQueries;

import edu.uci.ics.khefner.service.movies.MovieService;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.DeleteMovieResponseModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteMovie {

    public static DeleteMovieResponseModel DeleteMovie(String movieId){

        DeleteMovieResponseModel responseModel;
        //first check if movie exists...
        if(DoesMovieExist(movieId)){
            //movie exists
            //check if movie has already been deleted...
            if(IsMovieDeleted(movieId)){
                //movie has already been deleted...
                responseModel = new DeleteMovieResponseModel(242, "Movie has been already removed.");
                return responseModel;

            }
            //movie exists, but has not been deleted yet, so delete it
            try{
                String query = "UPDATE movies set hidden = 1 WHERE movies.id = ?";
                PreparedStatement ps = MovieService.getCon().prepareStatement(query);
                ps.setString(1, movieId);

                ServiceLogger.LOGGER.info("Executing query " + ps.toString());
                ps.executeUpdate();
                ServiceLogger.LOGGER.info("Query executed successfully");
                responseModel = new DeleteMovieResponseModel(240, "Movie has been successfully removed.");
                return responseModel;

            }catch (SQLException e) {
                ServiceLogger.LOGGER.warning("Unable to search movie.");
                e.printStackTrace();
                responseModel = new DeleteMovieResponseModel(-1, "Internal Server Error.");
                return responseModel;
            }
        }
        //movie doesn't exist so return response model with status code 241...
        responseModel = new DeleteMovieResponseModel(241, "Could not remove movie.");
        return responseModel;

    }

    public static boolean DoesMovieExist(String movieId){

        try{
            String query = "SELECT * FROM movies WHERE movies.id = ?";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, movieId);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(rs.next()){
                ServiceLogger.LOGGER.info("movie exists...");
                return true;
            }
            ServiceLogger.LOGGER.info("movie doesn't exist...");
            return false;

        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to search movie.");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean IsMovieDeleted(String movieId){
        try{
            String query = "SELECT * FROM movies WHERE movies.id = ?";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, movieId);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            rs.next();

            int hidden = rs.getInt("hidden");

            if(hidden == 0){
                ServiceLogger.LOGGER.info("movie is not deleted");
                return false;
            }
            ServiceLogger.LOGGER.info("movie is already deleted");
            return true;




        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to see if movie is deleted or not.");
            e.printStackTrace();
            return false;
        }

    }
}
