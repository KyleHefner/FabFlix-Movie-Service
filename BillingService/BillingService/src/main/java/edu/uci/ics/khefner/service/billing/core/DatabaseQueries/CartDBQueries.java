package edu.uci.ics.khefner.service.billing.core.DatabaseQueries;

import edu.uci.ics.khefner.service.billing.BillingService;
import edu.uci.ics.khefner.service.billing.core.CartItem;
import edu.uci.ics.khefner.service.billing.logger.ServiceLogger;
import edu.uci.ics.khefner.service.billing.models.Carts.CartRequestModel;
import edu.uci.ics.khefner.service.billing.models.Carts.CartRequestModel2;
import edu.uci.ics.khefner.service.billing.models.Carts.CartRequestModel3;
import edu.uci.ics.khefner.service.billing.models.Carts.CartResponseModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CartDBQueries {


    public static CartResponseModel InsertIntoCart(CartRequestModel requestModel) {

        try {
            String query = "INSERT INTO carts(email, movieId, quantity) Values (?,?,?)";

            PreparedStatement ps = BillingService.getCon().prepareStatement(query);

            ps.setString(1, requestModel.getEmail());
            ps.setString(2, requestModel.getMovieId());
            ps.setInt(3, requestModel.getQuantity());

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query executed successfully");

            CartResponseModel responseModel = new CartResponseModel(3100, "Shopping cart item inserted successfully.");
            return responseModel;

        } catch (SQLException e) {
            if(e instanceof java.sql.SQLIntegrityConstraintViolationException){
                ServiceLogger.LOGGER.info("Duplicate entry detected");
                CartResponseModel responseModel = new CartResponseModel(311, "Duplicate insertion.");
                return responseModel;
            }
            ServiceLogger.LOGGER.warning("Unable to Insert to Cart ");
            e.printStackTrace();

            CartResponseModel responseModel = new CartResponseModel(-1, "Internal Server Error");
            return responseModel;
        }
    }



    public static CartResponseModel UpdateCart(CartRequestModel requestModel){
        try {
            String query1 = "SELECT * FROM carts WHERE email = ? and movieId = ?";



            PreparedStatement ps1 = BillingService.getCon().prepareStatement(query1);
            ps1.setString(1, requestModel.getEmail());
            ps1.setString(2, requestModel.getMovieId());

            ServiceLogger.LOGGER.info("Executing query " + ps1.toString());
            ResultSet rs = ps1.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(!rs.next()){
                ServiceLogger.LOGGER.info("No entry exists in cart database");
                CartResponseModel responseModel = new CartResponseModel(312, "Shopping item does not exist.");
                return responseModel;
            }

            //item exists, now update it
            String query2 = "UPDATE carts SET quantity = ? WHERE email = ? and movieId = ?";
            PreparedStatement ps2 = BillingService.getCon().prepareStatement(query2);

            ps2.setInt(1, requestModel.getQuantity());
            ps2.setString(2, requestModel.getEmail());
            ps2.setString(3, requestModel.getMovieId());

            ServiceLogger.LOGGER.info("Executing query " + ps2.toString());
            ps2.executeUpdate();
            ServiceLogger.LOGGER.info("Query executed successfully");


            CartResponseModel responseModel = new CartResponseModel(3110, "Shopping cart item updated successfully.");
            return responseModel;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to Insert to Cart ");
            e.printStackTrace();

            CartResponseModel responseModel = new CartResponseModel(-1, "Internal Server Error");
            return responseModel;
        }
    }

    public static CartResponseModel DeleteFromCart(CartRequestModel2 requestModel){
        try {
            String query1 = "SELECT * FROM carts WHERE email = ? and movieId = ?";



            PreparedStatement ps1 = BillingService.getCon().prepareStatement(query1);
            ps1.setString(1, requestModel.getEmail());
            ps1.setString(2, requestModel.getMovieId());

            ServiceLogger.LOGGER.info("Executing query " + ps1.toString());
            ResultSet rs = ps1.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(!rs.next()){
                ServiceLogger.LOGGER.info("No entry exists in cart database");
                CartResponseModel responseModel = new CartResponseModel(312, "Shopping item does not exist.");
                return responseModel;
            }

            //item exists, now delete it
            String query2 = "DELETE FROM carts  WHERE email = ? and movieId = ?";
            PreparedStatement ps2 = BillingService.getCon().prepareStatement(query2);

            ps2.setString(1, requestModel.getEmail());
            ps2.setString(2, requestModel.getMovieId());

            ServiceLogger.LOGGER.info("Executing query " + ps2.toString());
            ps2.executeUpdate();
            ServiceLogger.LOGGER.info("Query executed successfully");

            CartResponseModel responseModel = new CartResponseModel(3120, "Shopping cart item deleted successfully.");
            return responseModel;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to Insert to Cart ");
            e.printStackTrace();

            CartResponseModel responseModel = new CartResponseModel(-1, "Internal Server Error");
            return responseModel;
        }
    }

    public static CartResponseModel ClearCart(CartRequestModel3 requestModel) {

        try {
            String query = "DELETE FROM carts WHERE email = ?";

            PreparedStatement ps = BillingService.getCon().prepareStatement(query);

            ps.setString(1, requestModel.getEmail());


            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query executed successfully");

            CartResponseModel responseModel = new CartResponseModel(3140, "Shopping cart cleared successfully.");
            return responseModel;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to Clear Cart ");
            e.printStackTrace();

            CartResponseModel responseModel = new CartResponseModel(-1, "Internal Server Error");
            return responseModel;
        }
    }


    public static CartResponseModel RetrieveCart(CartRequestModel3 requestModel) {

        try {
            String query = "SELECT * FROM carts WHERE email = ?";

            PreparedStatement ps = BillingService.getCon().prepareStatement(query);

            ps.setString(1, requestModel.getEmail());


            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(!rs.next()){
                //Shopping item does not exist
                ServiceLogger.LOGGER.info("No entry exists in cart database");
                CartResponseModel responseModel = new CartResponseModel(312, "Shopping item does not exist.");
                return responseModel;

            }
            //reset cursor of ResultSet rs
            rs.beforeFirst();

            CartResponseModel responseModel = new CartResponseModel(3130, "Shopping cart retrieved successfully.");
            while(rs.next()){
                String email = rs.getString("email");
                String movieId = rs.getString("movieId");
                Integer quantity = rs.getInt("quantity");
                CartItem item = new CartItem(email, movieId, quantity);
                responseModel.addToItemList(item);
            }

            return responseModel;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to Clear Cart ");
            e.printStackTrace();

            CartResponseModel responseModel = new CartResponseModel(-1, "Internal Server Error");
            return responseModel;
        }
    }

}
