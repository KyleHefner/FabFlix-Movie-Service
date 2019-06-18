package edu.uci.ics.khefner.service.movies.DatabaseQueries;

import edu.uci.ics.khefner.service.movies.MovieService;
import edu.uci.ics.khefner.service.movies.core.*;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.SearchMovieByIdResponseModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchMovieByID {

    public static SearchMovieByIdResponseModel GetMovieByID(String movieId, String email){

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
                List<Movie> movies = new ArrayList<Movie>();
                SearchMovieByIdResponseModel responseModel = new SearchMovieByIdResponseModel(211, "No movies found with search parameters.");
                return responseModel;
            }

            rs.beforeFirst();
            ServiceLogger.LOGGER.info("movie found...");
            SearchMovieByIdResponseModel responseModel = GetMovieResponse(rs, hasPrivilege);
            ServiceLogger.LOGGER.info("Got movie response");
            return responseModel;


        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to search movie " );
            e.printStackTrace();

        }
        SearchMovieByIdResponseModel responseModel = new SearchMovieByIdResponseModel(-1, "Internal Server Error.");

        return responseModel;

    }





    public static SearchMovieByIdResponseModel GetMovieResponse(ResultSet rs, boolean hasPrivilege){
        ServiceLogger.LOGGER.info("Building response model from result set");

        ServiceLogger.LOGGER.info("Result set it: " + rs.toString());
        try {
            rs.next();
            String movieTitle = rs.getString("movieTitle");
            String movieId = rs.getString("movieID");
            String Director = rs.getString("director");
            int year = rs.getInt("yr");
            String backdrop = rs.getString("backdrop");
            int budget = rs.getInt("budget");
            String overview = rs.getString("overview");
            String posterPath = rs.getString("posterpath");
            float rating = rs.getFloat("rating");
            int numVotes = rs.getInt("votes");
            int hidden = rs.getInt("hidden");
            int revenue = rs.getInt("revenue");
            Boolean hidden_bool = (hidden ==0?false:true);
            List<Genre> genreList = new ArrayList<Genre>();
            List<Star> starList = new ArrayList<Star>();

            if(!hasPrivilege && hidden_bool == true){
                ServiceLogger.LOGGER.info("Client does not have the privilege to see this movie");
                SearchMovieByIdResponseModel responseModel = new SearchMovieByIdResponseModel(141, "User has insufficient privilege.");
                return responseModel;
            }
            FullMovie movie = new FullMovie(movieId,movieTitle,Director,year,backdrop, budget, overview,posterPath,rating, revenue,numVotes,hidden_bool,genreList,starList);

            rs.beforeFirst();
            while (rs.next()) {

                int genreID = rs.getInt("genreID");
                String genreName = rs.getString("genreName");
                String starsId = rs.getString("starsID");
                String starsName = rs.getString("starsName");
                Integer birthYear = rs.getInt("starsBirthYear");



                ServiceLogger.LOGGER.info("Got all items from ResultSet current Row");

                    //movie has been added, but adding star and genre to its star and genre lists if they arent there already
                    ServiceLogger.LOGGER.info("Adding to existing Movie: " + movieTitle + " with Genre= " + genreName + " Star= " + starsName);
                    Genre genre = new Genre(genreID,genreName);
                    Star star = new Star(starsId,starsName, birthYear);

                    if(!movie.containsGenre(genreID)){movie.addGenre(genre);}
                    if(!movie.containsStar(starsId)){movie.addStar(star);}

            }
            ServiceLogger.LOGGER.info("Returning response model with movies information");
            SearchMovieByIdResponseModel responseModel = new SearchMovieByIdResponseModel(210, "Found movies with search parameters.",  movie);
            return responseModel;
        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to search movie " );
            e.printStackTrace();
        }
        SearchMovieByIdResponseModel responseModel = new SearchMovieByIdResponseModel(-1, "Internal Server Error.");

        return responseModel;
    }
}
