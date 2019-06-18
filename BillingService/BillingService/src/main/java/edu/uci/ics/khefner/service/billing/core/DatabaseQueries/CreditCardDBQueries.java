package edu.uci.ics.khefner.service.billing.core.DatabaseQueries;

import edu.uci.ics.khefner.service.billing.BillingService;
import edu.uci.ics.khefner.service.billing.core.CreditCard;
import edu.uci.ics.khefner.service.billing.logger.ServiceLogger;
import edu.uci.ics.khefner.service.billing.models.CreditCards.CreditCardRequestModel;
import edu.uci.ics.khefner.service.billing.models.CreditCards.CreditCardRequestModel2;
import edu.uci.ics.khefner.service.billing.models.CreditCards.CreditCardResponseModel;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;

public class CreditCardDBQueries {

    public static CreditCardResponseModel InsertCreditCard(CreditCardRequestModel requestModel){
        try{

            String query = "INSERT INTO creditcards(id, firstName, lastName, expiration) VALUES (?, ?, ?, ?)";

            PreparedStatement ps = BillingService.getCon().prepareStatement(query);

            ps.setString(1, requestModel.getId());
            ps.setString(2, requestModel.getFirstName());
            ps.setString(3, requestModel.getLastName());
            ServiceLogger.LOGGER.info("date: " + requestModel.getExpiration().toString());
            LocalDate expiration = requestModel.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Date sqlExpiration = Date.valueOf(expiration);
            ps.setDate(4, sqlExpiration);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query executed successfully");

            CreditCardResponseModel responseModel = new CreditCardResponseModel(3200, "Credit card inserted successfully.");
            return responseModel;

        }catch (SQLException e) {
            if(e instanceof java.sql.SQLIntegrityConstraintViolationException){
                ServiceLogger.LOGGER.info("Duplicate entry detected");
                CreditCardResponseModel responseModel = new CreditCardResponseModel(325, "Duplicate insertion.");
                return responseModel;
            }
            ServiceLogger.LOGGER.warning("Unable to Insert to Cart ");
            e.printStackTrace();

            CreditCardResponseModel responseModel = new CreditCardResponseModel(-1, "Internal Server Error");
            return responseModel;
        }


    }

    public static CreditCardResponseModel UpdateCreditCard(CreditCardRequestModel requestModel){
        try{


            String query1 = "SELECT * FROM creditcards WHERE id = ?";

            PreparedStatement ps = BillingService.getCon().prepareStatement(query1);
            ps.setString(1, requestModel.getId());

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");
            if(!rs.next()){
                ServiceLogger.LOGGER.info("No entry exists in creditcard database");
                CreditCardResponseModel responseModel = new CreditCardResponseModel(324, "Credit card does not exist.");
                return responseModel;
            }


            String query2 = "UPDATE creditcards SET firstName = ?, lastName = ?, expiration = ? WHERE id = ?";

            PreparedStatement ps2 = BillingService.getCon().prepareStatement(query2);

            ps2.setString(4, requestModel.getId());
            ps2.setString(1, requestModel.getFirstName());
            ps2.setString(2, requestModel.getLastName());
            LocalDate expiration = requestModel.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Date sqlExpiration = Date.valueOf(expiration);
            ps2.setDate(3, sqlExpiration);

            ServiceLogger.LOGGER.info("Executing query " + ps2.toString());
            ps2.executeUpdate();
            ServiceLogger.LOGGER.info("Query executed successfully");

            CreditCardResponseModel responseModel = new CreditCardResponseModel(3210, "Credit card updated successfully.");
            return responseModel;

        }catch (SQLException e) {
            if(e instanceof java.sql.SQLIntegrityConstraintViolationException){
                ServiceLogger.LOGGER.info("Duplicate entry detected");
                CreditCardResponseModel responseModel = new CreditCardResponseModel(325, "Duplicate insertion.");
                return responseModel;
            }
            ServiceLogger.LOGGER.warning("Unable to Insert to Cart ");
            e.printStackTrace();

            CreditCardResponseModel responseModel = new CreditCardResponseModel(-1, "Internal Server Error");
            return responseModel;
        }


    }

    public static CreditCardResponseModel DeleteCreditCard(CreditCardRequestModel2 requestModel){
        try{

            String query1 = "SELECT * FROM creditcards WHERE id =?";


            PreparedStatement ps = BillingService.getCon().prepareStatement(query1);

            ps.setString(1, requestModel.getId());
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(!rs.next()){
                CreditCardResponseModel responseModel = new CreditCardResponseModel(324, "Credit card does not exist.");
                return responseModel;
            }


            String query2 = "DELETE FROM creditcards WHERE id = ?";

            PreparedStatement ps2 = BillingService.getCon().prepareStatement(query2);

            ps2.setString(1, requestModel.getId());
            ServiceLogger.LOGGER.info("Executing query " + ps2.toString());
            ps2.execute();
            ServiceLogger.LOGGER.info("Query executed successfully");

            CreditCardResponseModel responseModel = new CreditCardResponseModel(3220, "Credit card deleted successfully.");
            return responseModel;

        }catch (SQLException e) {

            ServiceLogger.LOGGER.warning("Unable to delete from Cart ");
            e.printStackTrace();

            CreditCardResponseModel responseModel = new CreditCardResponseModel(-1, "Internal Server Error");
            return responseModel;
        }


    }


    public static CreditCardResponseModel RetrieveCreditCard(CreditCardRequestModel2 requestModel){
        try{

            String query = "SELECT * FROM creditcards WHERE id = ?";

            PreparedStatement ps = BillingService.getCon().prepareStatement(query);

            ps.setString(1, requestModel.getId());


            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(!rs.next()){
                ServiceLogger.LOGGER.info("Credit card not found");
                CreditCardResponseModel responseModel = new CreditCardResponseModel(324, "Credit card does not exist.");
                return responseModel;
            }

            //set cursor for resultset back to beginning
            rs.first();

            ServiceLogger.LOGGER.info("Credit card found");
            String firstName = rs.getString("firstName");
            String lastName = rs.getString("lastName");
            String id = rs.getString("id");
            String expiration = rs.getDate("expiration").toString();
            CreditCard creditcard = new CreditCard(id,firstName,lastName,expiration);


            CreditCardResponseModel responseModel = new CreditCardResponseModel(3230, "Credit card retrieved successfully.", creditcard);
            return responseModel;

        }catch (SQLException e) {

            ServiceLogger.LOGGER.warning("Unable to retrieve from Cart ");
            e.printStackTrace();

            CreditCardResponseModel responseModel = new CreditCardResponseModel(-1, "Internal Server Error");
            return responseModel;
        }


    }
}
