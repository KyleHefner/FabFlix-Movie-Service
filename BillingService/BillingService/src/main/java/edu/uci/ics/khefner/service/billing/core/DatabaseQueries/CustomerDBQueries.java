package edu.uci.ics.khefner.service.billing.core.DatabaseQueries;

import edu.uci.ics.khefner.service.billing.BillingService;
import edu.uci.ics.khefner.service.billing.core.Customer;
import edu.uci.ics.khefner.service.billing.logger.ServiceLogger;
import edu.uci.ics.khefner.service.billing.models.Customers.CustomerRequestModel;
import edu.uci.ics.khefner.service.billing.models.Customers.CustomerRequestModel1;
import edu.uci.ics.khefner.service.billing.models.Customers.CustomerResponseModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDBQueries {

    public static CustomerResponseModel InsertCustomer(CustomerRequestModel requestModel){

        //query the credit card table to see if there is already a credit card for this customer

        try{

            String query = "SELECT * FROM creditcards WHERE id = ?";

            PreparedStatement ps = BillingService.getCon().prepareStatement(query);

            ps.setString(1, requestModel.getCcId());
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(!rs.next()){
                ServiceLogger.LOGGER.info("credit card not found");
                CustomerResponseModel responseModel = new CustomerResponseModel(331, "Credit card ID not found.");
                return responseModel;
            }
            //credit card exists
            //now check if customer already exists in customers table
            String query1 = "SELECT * FROM customers WHERE email = ?";
            PreparedStatement ps1 = BillingService.getCon().prepareStatement(query1);
            ps1.setString(1, requestModel.getEmail());

            ServiceLogger.LOGGER.info("Executing query " + ps1.toString());
            ResultSet rs1 = ps1.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(rs1.next()){
                ServiceLogger.LOGGER.info("duplicate customer detected");
                CustomerResponseModel responseModel = new CustomerResponseModel(333, "Duplicate insertion.");
                return responseModel;
            }


            String query2 = "INSERT INTO customers (email, firstName, lastName, ccId, address) VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps2 = BillingService.getCon().prepareStatement(query2);
            ps2.setString(1, requestModel.getEmail());
            ps2.setString(2, requestModel.getFirstName());
            ps2.setString(3, requestModel.getLastName());
            ps2.setString(4, requestModel.getCcId());
            ps2.setString(5, requestModel.getAddress());

            ServiceLogger.LOGGER.info("Executing query " + ps2.toString());
            ps2.execute();
            ServiceLogger.LOGGER.info("Query executed successfully");

            CustomerResponseModel responseModel = new CustomerResponseModel(3300, "Customer inserted successfully.");
            return responseModel;
        }catch (SQLException e) {
            if(e instanceof java.sql.SQLIntegrityConstraintViolationException){
                ServiceLogger.LOGGER.info("Duplicate entry detected");
                CustomerResponseModel responseModel = new CustomerResponseModel(333, "Duplicate insertion.");
                return responseModel;
            }
            ServiceLogger.LOGGER.warning("Unable to Insert to Cart ");
            e.printStackTrace();

            CustomerResponseModel responseModel = new CustomerResponseModel(-1, "Internal Server Error");
            return responseModel;
        }


    }



    public static CustomerResponseModel UpdateCustomer(CustomerRequestModel requestModel){

        //query the credit card table to see if there is already a credit card for this customer

        try{

            String query = "SELECT * FROM creditcards WHERE id = ?";

            PreparedStatement ps = BillingService.getCon().prepareStatement(query);

            ps.setString(1, requestModel.getCcId());
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(!rs.next()){
                ServiceLogger.LOGGER.info("credit card not found");
                CustomerResponseModel responseModel = new CustomerResponseModel(331, "Credit card ID not found.");
                return responseModel;
            }
            //credit card exists
            //now check if customer already exists in customers table
            String query1 = "SELECT * FROM customers WHERE email = ?";
            PreparedStatement ps1 = BillingService.getCon().prepareStatement(query1);
            ps1.setString(1, requestModel.getEmail());

            ServiceLogger.LOGGER.info("Executing query " + ps1.toString());
            ResultSet rs1 = ps1.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(!rs1.next()){
                ServiceLogger.LOGGER.info("No customer found");
                CustomerResponseModel responseModel = new CustomerResponseModel(332, "Customer does not exist.");
                return responseModel;
            }


            String query2 = "UPDATE customers SET firstName = ?, lastName = ?, ccId = ?, address = ? WHERE email = ?";

            PreparedStatement ps2 = BillingService.getCon().prepareStatement(query2);

            ps2.setString(1, requestModel.getFirstName());
            ps2.setString(2, requestModel.getLastName());
            ps2.setString(3, requestModel.getCcId());
            ps2.setString(4, requestModel.getAddress());
            ps2.setString(5, requestModel.getEmail());

            ServiceLogger.LOGGER.info("Executing query " + ps2.toString());
            ps2.executeUpdate();
            ServiceLogger.LOGGER.info("Query executed successfully");

            CustomerResponseModel responseModel = new CustomerResponseModel(3310, "Customer updated successfully.");
            return responseModel;
        }catch (SQLException e) {

            ServiceLogger.LOGGER.warning("Unable to Update to Cart ");
            e.printStackTrace();

            CustomerResponseModel responseModel = new CustomerResponseModel(-1, "Internal Server Error");
            return responseModel;
        }


    }


    public static CustomerResponseModel RetrieveCustomer(CustomerRequestModel1 requestModel){

        //query the credit card table to see if there is already a credit card for this customer

        try{

            //now check if customer already exists in customers table
            String query1 = "SELECT * FROM customers WHERE email = ?";
            PreparedStatement ps1 = BillingService.getCon().prepareStatement(query1);
            ps1.setString(1, requestModel.getEmail());

            ServiceLogger.LOGGER.info("Executing query " + ps1.toString());
            ResultSet rs1 = ps1.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(!rs1.next()){
                ServiceLogger.LOGGER.info("No customer found");
                CustomerResponseModel responseModel = new CustomerResponseModel(332, "Customer does not exist.");
                return responseModel;
            }

            rs1.first();
            String email = rs1.getString("email");
            String firstName = rs1.getString("firstName");
            String lastName = rs1.getString("lastName");
            String ccId = rs1.getString("ccId");
            String address = rs1.getString("address");
            Customer customer = new Customer(email, firstName, lastName, ccId, address);

            CustomerResponseModel responseModel = new CustomerResponseModel(3320, "Customer retrieved successfully.", customer);
            return responseModel;
        }catch (SQLException e) {

            ServiceLogger.LOGGER.warning("Unable to Retrieve from to Cart ");
            e.printStackTrace();

            CustomerResponseModel responseModel = new CustomerResponseModel(-1, "Internal Server Error");
            return responseModel;
        }


    }
}
