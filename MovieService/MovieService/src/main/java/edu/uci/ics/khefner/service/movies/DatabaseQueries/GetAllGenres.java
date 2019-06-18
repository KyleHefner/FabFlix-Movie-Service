package edu.uci.ics.khefner.service.movies.DatabaseQueries;

import edu.uci.ics.khefner.service.movies.MovieService;
import edu.uci.ics.khefner.service.movies.core.Genre;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.GetAllGenresResponseModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetAllGenres {

    public static GetAllGenresResponseModel GetGenres() {

        try {

            String query = "SELECT * FROM genres";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            List<Genre> genres = new ArrayList<Genre>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                Genre genre = new Genre(id, name);
                genres.add(genre);
            }
            GetAllGenresResponseModel responseModel = new GetAllGenresResponseModel(219, "Genres successfully retrieved.", genres);
            return responseModel;


        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to search movie ");
            e.printStackTrace();
        }
        GetAllGenresResponseModel responseModel = new GetAllGenresResponseModel(-1, "Internal Server Error.");
        return responseModel;

    }
}
