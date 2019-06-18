package edu.uci.ics.khefner.service.movies.DatabaseQueries;

import edu.uci.ics.khefner.service.movies.MovieService;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.AddGenre.AddGenreRequestModel;
import edu.uci.ics.khefner.service.movies.models.AddGenre.AddGenreResponseModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddGenre {

    public static AddGenreResponseModel AddGenre(AddGenreRequestModel requestModel) {

        try {
            String query = "SELECT * FROM genres WHERE genres.name = ?";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getName());

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if (rs.next()) {
                ServiceLogger.LOGGER.info("Genre already exists...");
                AddGenreResponseModel responseModel = new AddGenreResponseModel(218, "Genre could not be added.");
                return responseModel;
            }

            ServiceLogger.LOGGER.info("Genre does not exist in database, so adding it");

            String query2 = "INSERT INTO genres (name) Values (?)";
            PreparedStatement ps2 = MovieService.getCon().prepareStatement(query2);
            ps2.setString(1, requestModel.getName());

            ServiceLogger.LOGGER.info("Executing query " + ps2.toString());
            ps2.execute();
            ServiceLogger.LOGGER.info("Query executed successfully");

            AddGenreResponseModel responseModel = new AddGenreResponseModel(217, "Genre successfully added.");
            return responseModel;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to add genre.");
            e.printStackTrace();
        }
        AddGenreResponseModel responseModel = new AddGenreResponseModel(-1, "Internal Server Error.");
        return responseModel;
    }
}
