package edu.uci.ics.khefner.service.movies.DatabaseQueries;

import edu.uci.ics.khefner.service.movies.MovieService;
import edu.uci.ics.khefner.service.movies.core.Star;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.SearchStarByIdResponseModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchStarByID {

    public static SearchStarByIdResponseModel GetStarById(String id){

        try{
            String query = "SELECT * FROM stars WHERE stars.id = ?";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, id);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(!rs.next()){
                //no star found
                SearchStarByIdResponseModel responseModel = new SearchStarByIdResponseModel(213, "No stars found with search parameters.");
                return responseModel;

            }
            //star was found;
            rs.first();
            String name = rs.getString("name");
            String starId = rs.getString("id");
            Integer birthYear = rs.getInt("birthYear");

            Star star = new Star(id, name, birthYear);

            SearchStarByIdResponseModel responseModel = new SearchStarByIdResponseModel(212, "Found stars with search parameters.", star);
            return responseModel;

        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to search movie ");
            e.printStackTrace();
        }
        SearchStarByIdResponseModel responseModel = new SearchStarByIdResponseModel(-1, "Internal server error.");
        return responseModel;

    }
}
