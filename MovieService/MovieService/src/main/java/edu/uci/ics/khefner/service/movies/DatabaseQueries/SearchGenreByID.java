package edu.uci.ics.khefner.service.movies.DatabaseQueries;

import edu.uci.ics.khefner.service.movies.MovieService;
import edu.uci.ics.khefner.service.movies.core.CheckPrivilege;
import edu.uci.ics.khefner.service.movies.core.Genre;
import edu.uci.ics.khefner.service.movies.core.Movie;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.SearchGenreByIdResponseModel;
import edu.uci.ics.khefner.service.movies.models.SearchMovieByIdResponseModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchGenreByID {

    public static SearchGenreByIdResponseModel GetGenreByID(String movieId, String email){

        //need to verify client by using email, transactionID, and sessionID...
        boolean hasPrivilege = CheckPrivilege.hasPrivilege(email,  3 );

        try{
            String query = " SELECT movies.id AS movieID, movies.title AS movieTitle, movies.director AS director, " +
                    "                          movies.year AS yr, movies.revenue AS revenue, movies.backdrop_path AS backdrop, movies.budget AS budget, movies.overview AS overview, " +
                    "                         movies.poster_path AS posterpath, movies.rating AS rating, movies.numVotes AS votes, movies.hidden AS hidden, " +
                    "                         genres.id AS genreID, genres.name AS genreName, stars.id AS starsID, stars.name AS starsName, stars.birthYear AS starsBirthYear " +
                    "                         FROM (SELECT * From movies LEFT OUTER JOIN ratings r ON movies.id = r.movieId) as movies,genres,genres_in_movies,stars,stars_in_movies " +
                    "                        Where  movies.id = genres_in_movies.movieId AND genres.id = genres_in_movies.genreId AND " +
                    "                        movies.id = movies.movieId AND movies.id = stars_in_movies.movieId AND stars.id = stars_in_movies.starId" +
                    "                        AND movies.id = ? ";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, movieId);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if (!rs.next()) {
                ServiceLogger.LOGGER.info("Did not find a movie with the given movie ID");
                //Search did not return anything
                List<Movie> genres = new ArrayList<Movie>();
                SearchGenreByIdResponseModel responseModel = new SearchGenreByIdResponseModel(211, "No movies found with search parameters.");
                return responseModel;
            }

            rs.beforeFirst();
            ServiceLogger.LOGGER.info("movie found...");
            SearchGenreByIdResponseModel responseModel = GetGenresFromMovie(rs, hasPrivilege);
            ServiceLogger.LOGGER.info("Got movie response");
            return responseModel;


        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to search movie " );
            e.printStackTrace();

        }
        SearchGenreByIdResponseModel responseModel = new SearchGenreByIdResponseModel(-1, "Internal Server Error.");

        return responseModel;

    }

    public static SearchGenreByIdResponseModel GetGenresFromMovie(ResultSet rs, boolean hasPrivilege){
        ServiceLogger.LOGGER.info("Building response model from result set");
        try{
            rs.next();
            int hidden = rs.getInt("hidden");
            Boolean hidden_bool = (hidden ==0?false:true);
            if(!hasPrivilege && hidden_bool == true){
                ServiceLogger.LOGGER.info("Client does not have the privilege to see this movie");
                SearchGenreByIdResponseModel responseModel = new SearchGenreByIdResponseModel(141, "User has insufficient privilege.");
                return responseModel;
            }

            List<Genre> genreList = new ArrayList<>();
            List<Integer> genreIdsAdded = new ArrayList<>();

            rs.beforeFirst();
            while(rs.next()){
                int genreID = rs.getInt("genreID");
                String genreName = rs.getString("genreName");

                if(!genreIdsAdded.contains(genreID)){
                    //add genre to genre list
                    Genre genre = new Genre(genreID, genreName);
                    genreList.add(genre);
                    genreIdsAdded.add(genreID);
                }

            }
            ServiceLogger.LOGGER.info("genres successfully retrieved ");
            SearchGenreByIdResponseModel responseModel = new SearchGenreByIdResponseModel(219, "Genres successfully retrieved.", genreList);
            return responseModel;

        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to search movie " );
            e.printStackTrace();
        }
        SearchGenreByIdResponseModel responseModel = new SearchGenreByIdResponseModel(-1, "Internal server error.");
        return responseModel;
    }
}
