package edu.uci.ics.khefner.service.movies.DatabaseQueries;

import edu.uci.ics.khefner.service.movies.MovieService;
import edu.uci.ics.khefner.service.movies.core.Movie;
import edu.uci.ics.khefner.service.movies.core.Star;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.SearchMovie.SearchMovieResponseModel;
import edu.uci.ics.khefner.service.movies.models.SearchStar.SearchStarRequestModel;
import edu.uci.ics.khefner.service.movies.models.SearchStar.SearchStarResponseModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchStar {

    public static SearchStarResponseModel SearchStar(SearchStarRequestModel requestModel){

        //get all fields from the request model
        String starName = (requestModel.getName() == null ? "" : requestModel.getName());
        String movieTitle = (requestModel.getMovieTitle() == null ? "" : requestModel.getMovieTitle());
        Integer birthYear = requestModel.getBirthYear();
        String orderBy = requestModel.getOrderBy();
        String direction = requestModel.getDirection();
        int limit = requestModel.getLimit();
        int offset = requestModel.getOffset();

        SearchStarResponseModel responseModel;

        try{
            PreparedStatement ps;

            if (birthYear > 1800 && birthYear < 2019) {
                //year is a valid number
                String query = " SELECT * FROM (SELECT DISTINCT movies.id AS movieID, movies.title AS movieTitle, movies.director AS director, " +
                        "  movies.year AS yr, ratings.rating AS rating, ratings.numVotes AS votes, movies.hidden AS hidden, " +
                        " stars.birthYear as birthYear, stars.name as starName,  stars.id as starID " +
                        " FROM movies, stars, stars_in_movies,  ratings " +
                        "Where  movies.id = stars_in_movies.movieId AND stars.id = stars_in_movies.starId AND ratings.movieId = movies.id AND " +
                        "movies.title LIKE ? AND stars.name LIKE ? and stars.birthYear = ? " +
                         " ORDER BY " + orderBy + " " + direction + ", birthYear asc  LIMIT ? OFFSET ?) as movies " +
                        " GROUP BY movies.starName";


                ps = MovieService.getCon().prepareStatement(query);
                ps.setString(1, "%" + movieTitle + "%");
                ps.setString(2, "%" + starName + "%");
                ps.setInt(3, birthYear);
                ps.setInt(4, limit);
                ps.setInt(5, offset);
            }
            else{
                //year is invalid number
                String query = " SELECT * FROM (SELECT DISTINCT movies.id AS movieID, movies.title AS movieTitle, movies.director AS director, " +
                        "  movies.year AS yr, ratings.rating AS rating, ratings.numVotes AS votes, movies.hidden AS hidden, " +
                        " stars.birthYear as birthYear, stars.name as starName,  stars.id as starID  " +
                        " FROM movies, stars, stars_in_movies,  ratings " +
                        "Where  movies.id = stars_in_movies.movieId AND stars.id = stars_in_movies.starId AND ratings.movieId = movies.id AND " +
                        "movies.title LIKE ? AND stars.name LIKE ? " +
                        " ORDER BY " + orderBy + " " + direction + ", birthYear asc  LIMIT ? OFFSET ?) as movies " +
                        " GROUP BY movies.starName";


                ps = MovieService.getCon().prepareStatement(query);
                ps.setString(1, "%" + movieTitle + "%");
                ps.setString(2, "%" + starName + "%");
                ps.setInt(3, limit);
                ps.setInt(4, offset);

            }

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if (!rs.next()) {
                ServiceLogger.LOGGER.info("Query did not return anything...no stars found");
                //Search did not return anything
                List<Star> stars = new ArrayList<Star>();
                responseModel = new SearchStarResponseModel(213, "No stars found with search parameters.", stars);
                return responseModel;
            }

            rs.beforeFirst();

            ServiceLogger.LOGGER.info("Query returned a result");
            List<Star> stars = new ArrayList<Star>();
            while (rs.next()) {
                String starId  = rs.getString("starID");
                starName = rs.getString("starName");
                birthYear = rs.getInt("birthYear");

                Star star = new Star(starId, starName, birthYear);
                stars.add(star);

            }
            responseModel = new SearchStarResponseModel(212, "Found stars with search parameters.", stars);
            return responseModel;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to search movie ");
            e.printStackTrace();
        }

        responseModel = new SearchStarResponseModel( -1, "Internal server error.");
        return responseModel;

        }

}
