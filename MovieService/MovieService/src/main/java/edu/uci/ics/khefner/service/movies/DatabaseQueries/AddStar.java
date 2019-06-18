package edu.uci.ics.khefner.service.movies.DatabaseQueries;

import edu.uci.ics.khefner.service.movies.MovieService;
import edu.uci.ics.khefner.service.movies.logger.ServiceLogger;
import edu.uci.ics.khefner.service.movies.models.AddStar.AddStarRequestModel;
import edu.uci.ics.khefner.service.movies.models.AddStar.AddStarResponseModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddStar {

    public static AddStarResponseModel AddStar(AddStarRequestModel requestModel){
        //adds star to stars table...

        String name = requestModel.getName();
        Integer birthYear = requestModel.getBirthYear();
        if(DoesStarExist(name, birthYear)){
        //return a response model...
            AddStarResponseModel responseModel = new AddStarResponseModel(222, "Star already exists.");
            return responseModel;
        }

        try {
            ServiceLogger.LOGGER.info("going to get star id of last movie added...");
            String query = "SELECT * FROM stars where id LIKE '%ss%' Order by id Desc";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);


            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");


            AddStarResponseModel responseModel;
            if (!rs.next()) {
                ServiceLogger.LOGGER.info("this is the first star being added");
                ServiceLogger.LOGGER.info("Adding star with id = ss0000001");

                responseModel = InsertStar(name, "ss0000001", birthYear);


            } else {
                ServiceLogger.LOGGER.info("A star has already been added... trying to get the latest starId");
                rs.first();
                //getting latest starid of form "ssxxxxxxxx"
                String latestStarId = rs.getString("id");
                ServiceLogger.LOGGER.info("last added starId is : " + latestStarId);
                String newId = generateNewStarID(latestStarId);


                responseModel = InsertStar(name, newId, birthYear);

            }
            return responseModel;


        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable Add star.");
            e.printStackTrace();
        }
        AddStarResponseModel responseModel = new AddStarResponseModel(-1, "Internal server error.");
        return responseModel;
    }



    public static boolean DoesStarExist(String starName, Integer birthYear) {
        try {
            String query = "SELECT * FROM stars WHERE stars.name = ? AND stars.birthYear = ?";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, starName);
            ps.setInt(2, birthYear);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if (!rs.next()) {
                ServiceLogger.LOGGER.info("Star doesn't exist, so able to add it");

                return false;
            }
            ServiceLogger.LOGGER.info("Star already exists");
            return true;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable search star.");
            e.printStackTrace();
        }
        return true;
    }

    public static AddStarResponseModel InsertStar(String name, String id, Integer birthYear){

        try{
            String query = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, id);
            ps.setString(2, name);
            if(birthYear < 2019){ps.setInt(3, birthYear);}
            else{ps.setNull(3, java.sql.Types.INTEGER);}

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query executed successfully");

            AddStarResponseModel responseModel = new AddStarResponseModel(220, "Star successfully added.");
            return responseModel;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable search star.");
            e.printStackTrace();
        }
        AddStarResponseModel responseModel = new AddStarResponseModel(-1, "Internal server error.");
        return responseModel;
    }

    public static String generateNewStarID(String lastStarId){
        //will generate a new id
        String [] splitStr = lastStarId.split("ss[0]+");
        Integer currentID = Integer.parseInt(splitStr[1]);
        Integer newID = currentID + 1;
        String finalID = String.format("%07d", newID);
        finalID = "ss" + finalID;
        ServiceLogger.LOGGER.info("new id: " + finalID);


        return finalID;
    }

}



