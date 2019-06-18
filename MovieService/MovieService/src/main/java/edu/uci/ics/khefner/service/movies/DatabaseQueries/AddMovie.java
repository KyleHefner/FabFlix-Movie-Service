package edu.uci.ics.khefner.service.movies.DatabaseQueries;

import edu.uci.ics.khefner.service.movies.MovieService;
import edu.uci.ics.khefner.service.movies.core.CheckPrivilege;
import edu.uci.ics.khefner.service.movies.core.Genre;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.AddGenre.AddGenreRequestModel;
import edu.uci.ics.khefner.service.movies.models.AddGenre.AddGenreResponseModel;
import edu.uci.ics.khefner.service.movies.models.AddMovie.AddMovieRequestModel;
import edu.uci.ics.khefner.service.movies.models.AddMovie.AddMovieResponseModel;
import edu.uci.ics.khefner.service.movies.models.SearchMovieByIdResponseModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AddMovie {

    public static AddMovieResponseModel AddMovie(AddMovieRequestModel requestModel, String email){


        //verify client's privilege
        boolean hasPrivilege = CheckPrivilege.hasPrivilege(email, 3);

        if(!hasPrivilege){
            ServiceLogger.LOGGER.info("Client does not have privilege to add movie");
            AddMovieResponseModel responseModel = new AddMovieResponseModel(141, "User has insufficient privilege.");
            return responseModel;
        }


        try{
            AddMovieResponseModel responseModel = DoesMovieExist(requestModel, email);

            if(responseModel.getResultCode() == 216){ //means movie already exists in database, so just return it
                return responseModel;
            }

            //need to get last added movie's id
            ServiceLogger.LOGGER.info("going to get movie id of last movie added...");
            String query = "SELECT * FROM movies where id LIKE '%cs%' Order by id Desc";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);


            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            String id;
            if(!rs.next()){
                ServiceLogger.LOGGER.info("this is the first movie being added");
                ServiceLogger.LOGGER.info("Adding movie with id = cs0000001");

                responseModel = InsertMovie(requestModel,"cs0000001");
                id = "cs0000001";

            }
            else {
                ServiceLogger.LOGGER.info("A movie has already been added... trying to get the latest movieid");
                rs.first();
                //getting latest movieid of form "csxxxxxxxx"
                String latestMovieId = rs.getString("id");
                ServiceLogger.LOGGER.info("last added movieid is : " + latestMovieId);
                String newId = generateNewID(latestMovieId);



                responseModel = InsertMovie(requestModel, newId);
                id = newId;
            }


            List<Integer> genreIdList = GetGenreIdList(id);
            ServiceLogger.LOGGER.info("genreID list: " + genreIdList.toString());
            responseModel = new AddMovieResponseModel(214, "Movie successfully added.", id, genreIdList);
            return responseModel;


        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to search movie ");
            e.printStackTrace();
        }
            AddMovieResponseModel responseModel = new AddMovieResponseModel(215, "Could not add movie.");
            return responseModel;
    }

    private static AddMovieResponseModel InsertMovie(AddMovieRequestModel requestModel, String id){
        ServiceLogger.LOGGER.info("Inserting movie with id = " + id);
        String title = requestModel.getTitle();
        String director = requestModel.getDirector();
        int year = requestModel.getYear();
        String backdrop_path = requestModel.getBackdrop_path();
        Integer budget = requestModel.getBudget();
        String overview = requestModel.getOverview();
        String poster_path = requestModel.getPoster_path();
        Integer revenue = requestModel.getRevenue();
        int hidden = 0;

        ServiceLogger.LOGGER.info("finished getting fields from request model...");

        try {

            String query = "INSERT INTO movies (id, title, director, year, backdrop_path, budget, overview, poster_path, revenue, hidden) " +
                    "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);




            ps.setString(1, id);
            ps.setString(2, title);
            ps.setString(3, director);
            ps.setInt(4, year);
            if(backdrop_path == null){ ps.setNull(5, Types.VARCHAR); }
            else{ps.setString(5, backdrop_path);}

            if(budget == null){ ps.setNull(6, Types.INTEGER); }
            else{ps.setInt(6, budget);}

            if(overview == null){ ps.setNull(7, Types.VARCHAR); }
            else{ps.setString(7, overview);}

            if(poster_path == null){ ps.setNull(8, Types.VARCHAR); }
            else{ps.setString(8, poster_path);}

            if(revenue == null){ ps.setNull(9, Types.INTEGER); }
            else{ ps.setInt(9, revenue);}


            ps.setInt(10, hidden);


            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query executed successfully");

            ServiceLogger.LOGGER.info("adding rating and numvotes to new movie...");

            String query2 = "INSERT INTO ratings (movieid, rating, numVotes) VALUES (?, 0.0, 0)";
            PreparedStatement ps2 = MovieService.getCon().prepareStatement(query2);
            ps2.setString(1, id);

            ServiceLogger.LOGGER.info("Executing query " + ps2.toString());
            ps2.execute();
            ServiceLogger.LOGGER.info("Query executed successfully");

            //now need to insert records into genres and genres_in_movies table
            //loop through genres list from request model
            List<Genre> genres = requestModel.getGenres();
            for(Genre genre: genres){
                if(!DoesGenreExist(genre.getName())){
                    ServiceLogger.LOGGER.info("Adding genre : " + genre.getName() + "to genres table" );
                    AddGenreRequestModel requestModel1 = new AddGenreRequestModel(genre.getName());
                    AddGenreResponseModel responseModel1 = AddGenre.AddGenre(requestModel1);
                    boolean addToGenreInMovies = AddGenreInMovies(id, genre.getName());
                    if(addToGenreInMovies){ServiceLogger.LOGGER.info("successfully added movieid = " + id + "  genreName = " + genre.getName() +" to genres_in_movies");}
                    else{ServiceLogger.LOGGER.info("could not add movieid = " + id + "  genreName = " + genre.getName() +" to genres_in_movies");}

                }
                else {
                    ServiceLogger.LOGGER.info("Genre already existed, now just adding record to genres_in_movies for this genre and movie id");
                    AddGenreInMovies(id, genre.getName());
                }
            }

            AddMovieResponseModel responseModel = new AddMovieResponseModel(214, "Movie successfully added.");
            return responseModel;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to add movie ");
            e.printStackTrace();
        }

        AddMovieResponseModel responseModel = new AddMovieResponseModel(215, "Could not add movie." );
        return responseModel;
    }

    private static AddMovieResponseModel DoesMovieExist(AddMovieRequestModel requestModel, String email) {

        ServiceLogger.LOGGER.info("Checking if movie exists...");
        String title = requestModel.getTitle();
        String director = requestModel.getDirector();
        int year = requestModel.getYear();
        String backdrop_path = requestModel.getBackdrop_path();
        Integer budget;
        if(requestModel.getBudget() == null){
            budget = 0;
        }
        else{
            budget = requestModel.getBudget();
        }


        String overview = requestModel.getOverview();
        String poster_path = requestModel.getPoster_path();
        Integer revenue;
        if(requestModel.getRevenue() == null){
            revenue = 0;
        }
        else{
            revenue = requestModel.getRevenue();
        }
        List<Genre> genres = requestModel.getGenres();

        try {
            //first need to check if movie already exists in the db...
            String query = "SELECT movies.id as movieid FROM movies " +
                    "WHERE movies.title = ? AND movies.director = ? AND movies.year = ? ";



            ServiceLogger.LOGGER.info("Building query...");
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, title);
            ps.setString(2, director);
            ps.setInt(3, year);


            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if (!rs.next()) {
                ServiceLogger.LOGGER.info("Movie does not exist in the database...");
                //putting random resultcode and message for this case
                AddMovieResponseModel responseModel = new AddMovieResponseModel(0, "Movie does not exist.");
                return responseModel;
            }
            rs.first();
            String movieid = rs.getString("movieid");
            ServiceLogger.LOGGER.info("Movie already exists in the database...");
            ServiceLogger.LOGGER.info("Getting the movie information...");
            //need to get movie ID and genreID list to put into the response model...
            //SearchMovieByIdResponseModel responseModel1 = SearchMovieByID.GetMovieByID(movieid, email);
            List<Integer> genreIdList = GetGenreIdList(movieid);

            AddMovieResponseModel responseModel = new AddMovieResponseModel(216, "Movie already exists.", movieid, genreIdList);



            return responseModel;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to search movie ");
            e.printStackTrace();
        }
        AddMovieResponseModel responseModel = new AddMovieResponseModel(-1, "Internal Server Error.");
        return responseModel;
    }

    public static boolean DoesGenreExist(String genreName){

        try {
            ServiceLogger.LOGGER.info("Checking if the genre from request model already exists...");
            String query = "SELECT * FROM genres WHERE genres.name = ?";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            ps.setString(1, genreName);
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(rs.next()){
                ServiceLogger.LOGGER.info("genre " + genreName + " already exists...");
                return true;
            }
            ServiceLogger.LOGGER.info("genre " + genreName + " doesn't exist in genres table");
            return false;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to search genre ");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean AddGenreInMovies(String movieid, String genreName){
        //first get the genre by its genreName
        try {
            String query = "SELECT * FROM genres WHERE genres.name = ?";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            ps.setString(1, genreName);
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            rs.next();
            int genreid = rs.getInt("id");

            //now insert genreid and movieid into genres_in_movies table

            String query2 = "INSERT INTO genres_in_movies(genreid,movieid) Values (?, ?)";
            PreparedStatement ps2 = MovieService.getCon().prepareStatement(query2);
            ps2.setInt(1, genreid);
            ps2.setString(2, movieid);

            ServiceLogger.LOGGER.info("Executing query " + ps2.toString());
            ps2.execute();
            ServiceLogger.LOGGER.info("Query executed successfully");

            return true;

        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to Add to genres_in_movies table ");
            e.printStackTrace();
        }
        return false;
    }


    public static List<Integer> GetGenreIdList(String movieid){
        //retrieves genre id list given a movieid...

        try{
            String query = "SELECT * FROM movies, genres, genres_in_movies " +
                    "WHERE movies.id = genres_in_movies.movieId AND genres.id = genres_in_movies.genreId AND movies.id = ?";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, movieid);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(!rs.next()){
                ServiceLogger.LOGGER.info("movieID = " + movieid + " has not genres...");
                return new ArrayList<Integer>();

            }
            rs.beforeFirst();
            List<Integer> genreIdList = new ArrayList<Integer>();
            while(rs.next()){
                int genreId = rs.getInt("genreId");
                genreIdList.add(genreId);
            }
            return genreIdList;


        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to get Genre list from movie id :  " + movieid);
            e.printStackTrace();
            return new ArrayList<Integer>();
        }
    }

    public static String generateNewID(String lastMovieId){
        //will generate a new id
        String [] splitStr = lastMovieId.split("cs[0]+");
        Integer currentID = Integer.parseInt(splitStr[1]);
        Integer newID = currentID + 1;
        String finalID = String.format("%07d", newID);
        finalID = "cs" + finalID;
        ServiceLogger.LOGGER.info("new id: " + finalID);


        return finalID;
    }
}
