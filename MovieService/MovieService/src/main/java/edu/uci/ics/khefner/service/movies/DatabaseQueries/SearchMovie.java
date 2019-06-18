package edu.uci.ics.khefner.service.movies.DatabaseQueries;

import edu.uci.ics.khefner.service.movies.MovieService;
import edu.uci.ics.khefner.service.movies.core.CheckPrivilege;
import edu.uci.ics.khefner.service.movies.core.Movie;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.SearchMovie.SearchMovieRequestModel;
import edu.uci.ics.khefner.service.movies.models.SearchMovie.SearchMovieResponseModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchMovie {

    public static SearchMovieResponseModel GetMovie(SearchMovieRequestModel requestModel, String email) {


        //get header values from request model




        //check privilege of user here
        ServiceLogger.LOGGER.info("Checking privilege of client...");
        boolean hasPrivilege = CheckPrivilege.hasPrivilege(email,  3);

        ServiceLogger.LOGGER.info("Going to query for movie");
        //build search query parameters and if null, set to "%%" to be used in Like operator in sql
        String genre = (requestModel.getGenre() == null ? "" : requestModel.getGenre());
        String title = (requestModel.getTitle() == null ? "" : requestModel.getTitle());
        String director = (requestModel.getDirector() == null ? "" : requestModel.getDirector());
        int year = requestModel.getYear();
        String orderBy = requestModel.getOrderBy();
        String direction = requestModel.getDirection();
        int limit = requestModel.getLimit();
        int offset = requestModel.getOffset();

        //initalize response model to be returned;
        SearchMovieResponseModel responseModel;

        try {
            PreparedStatement ps;

            if (year > 1800 && year < 2019) {
                //year is a valid number
                String query = " SELECT * FROM (SELECT DISTINCT movies.id AS movieID, movies.title AS movieTitle, movies.director AS director, " +
                        "  movies.year AS yr, ratings.rating AS rating, ratings.numVotes AS votes, movies.hidden AS hidden, " +
                        " genres.id AS genreID, genres.name AS genreName " +
                        " FROM movies, genres, genres_in_movies, ratings " +
                        "Where  movies.id = genres_in_movies.movieId AND genres.id = genres_in_movies.genreId AND ratings.movieId = movies.id AND " +
                        "movies.title LIKE ? AND genres.name LIKE ? and movies.director LIKE ? " +
                        " AND movies.year = ? ) as movies " +
                        "GROUP BY movies.movieID " + "ORDER BY " + orderBy + " " + direction + ", movieTitle asc LIMIT ? OFFSET ?";


                ps = MovieService.getCon().prepareStatement(query);
                ps.setString(1, "%" + title + "%");
                ps.setString(2, "%" + genre + "%");
                ps.setString(3, "%" + director + "%");
                ps.setInt(4, year);
                ps.setInt(5, limit);
                ps.setInt(6, offset);
            } else {
                String query = " SELECT * FROM (SELECT DISTINCT movies.id AS movieID, movies.title AS movieTitle, movies.director AS director, " +
                        "  movies.year AS yr, ratings.rating AS rating, ratings.numVotes AS votes, movies.hidden AS hidden, " +
                        " genres.id AS genreID, genres.name AS genreName " +
                        " FROM movies, genres, genres_in_movies, ratings " +
                        "Where  movies.id = genres_in_movies.movieId AND genres.id = genres_in_movies.genreId AND ratings.movieId = movies.id AND " +
                        "movies.title LIKE ? AND genres.name LIKE ? and movies.director LIKE ? ) as movies " +
                        "GROUP BY movies.movieID " + "ORDER BY " + orderBy + " " + direction + ", movieTitle asc LIMIT ? OFFSET ?";

                ps = MovieService.getCon().prepareStatement(query);


                ps.setString(1, "%" + title + "%");
                ps.setString(2, "%" + genre + "%");
                ps.setString(3, "%" + director + "%");
                ps.setInt(4, limit);
                ps.setInt(5, offset);
            }
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if (!rs.next()) {
                ServiceLogger.LOGGER.info("Query did not return anything");
                //Search did not return anything
                List<Movie> movies = new ArrayList<Movie>();
                responseModel = new SearchMovieResponseModel(211, "No movies found with search parameters.", movies);
                return responseModel;
            }

            rs.beforeFirst();

            ServiceLogger.LOGGER.info("Query returned a result");
            List<Movie> movies = new ArrayList<Movie>();
            while (rs.next()) {
                String movieTitle = rs.getString("movieTitle");
                String movieId = rs.getString("movieID");
                String Director = rs.getString("director");
                int yr = rs.getInt("yr");
                float rating = rs.getFloat("rating");
                int numVotes = rs.getInt("votes");
                int hidden = rs.getInt("hidden");
                Boolean hidden_bool = (hidden == 0 ? false : true);

                ServiceLogger.LOGGER.info("movie title: " + movieTitle);

                if (!hasPrivilege && hidden_bool == true) {
                    continue;
                } // Client does not have privilege to see this movie
                hidden_bool = null;
                Movie movie = new Movie(movieId, movieTitle, Director, yr, rating, numVotes, hidden_bool);
                movies.add(movie);

            }
            responseModel = new SearchMovieResponseModel(210, "Found movies with search parameters.", movies);
            return responseModel;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to search movie ");
            e.printStackTrace();
        }
        List<Movie> movies = new ArrayList<Movie>();
        responseModel = new SearchMovieResponseModel( -1, "Internal server error.", movies);
        return responseModel;
    }
}
