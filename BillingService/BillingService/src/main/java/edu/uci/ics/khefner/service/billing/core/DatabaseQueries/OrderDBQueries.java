package edu.uci.ics.khefner.service.billing.core.DatabaseQueries;

import com.paypal.api.payments.Sale;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import edu.uci.ics.khefner.service.billing.BillingService;
import edu.uci.ics.khefner.service.billing.core.*;
import edu.uci.ics.khefner.service.billing.logger.ServiceLogger;
import edu.uci.ics.khefner.service.billing.models.Orders.OrderRequestModel;
import edu.uci.ics.khefner.service.billing.models.Orders.OrderResponseModel;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class OrderDBQueries {

    public static OrderResponseModel PlaceOrder(OrderRequestModel requestModel){


        try{

            //first check if customer exists..
            String query1 = "SELECT * FROM customers WHERE email = ?";
            PreparedStatement ps1 = BillingService.getCon().prepareStatement(query1);
            ps1.setString(1, requestModel.getEmail());

            ServiceLogger.LOGGER.info("Executing query " + ps1.toString());
            ResultSet rs1 = ps1.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(!rs1.next()){
                ServiceLogger.LOGGER.info("No customer found");
                OrderResponseModel responseModel = new OrderResponseModel(332, "Customer does not exist.");
                return responseModel;
            }

            //now retrieve cart items from cart

            String query2 = "SELECT * FROM carts, movie_prices WHERE carts.movieId = movie_prices.movieId AND email = ?";
            PreparedStatement ps2 = BillingService.getCon().prepareStatement(query2);
            ps2.setString(1, requestModel.getEmail());

            ServiceLogger.LOGGER.info("Executing query " + ps2.toString());
            ResultSet rs2 = ps2.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(!rs2.next()){
                ServiceLogger.LOGGER.info("Cart found for customer");
                OrderResponseModel responseModel = new OrderResponseModel(341, "Shopping cart for this customer not found.");
                return responseModel;
            }

            //get total cost of shopping cart
            rs2.beforeFirst();
            float totalCost = 0; //cost to be used in PaypalFunctions payment
            OrderResponseModel responseModel = new OrderResponseModel(3400, "Order placed successfully.");
            while(rs2.next()){
                int quantity = rs2.getInt("quantity");
                float unit_price = rs2.getFloat("unit_price");
                float discount = rs2.getFloat("discount");
                totalCost += quantity * unit_price * discount;
            }

            //create a payment using total cost
            DecimalFormat df = new DecimalFormat("0.00");
            String totalCostString = df.format(totalCost);

            Map<String, String> paymentResponse = PaypalFunctions.createPayment(totalCostString);
            if(paymentResponse.get("status") == "failure"){
                ServiceLogger.LOGGER.info("created payment failure");
                responseModel = new OrderResponseModel(342, "Create payment failed.");
                return responseModel;
            }

            //create payment success, now use stored procedure to put insert into sales and transactions table at the same time...
            ServiceLogger.LOGGER.info("created payment success");
            String redirectUrl = paymentResponse.get("redirect_url");
            String token = redirectUrl.split("token=")[1];
            responseModel = new OrderResponseModel(3400, "Order placed successfully.", token, redirectUrl );

            //loop through resultSet again to get all inputs to the insert of the stored procedure
            rs2.beforeFirst();
            CallableStatement cs = null;
            while(rs2.next()) {

                    String email = rs2.getString("email");
                    String movieId = rs2.getString("movieId");
                    int quantity = rs2.getInt("quantity");
                    Date date = new Date();
                    LocalDate expiration = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    java.sql.Date today = java.sql.Date.valueOf(expiration);

                try{
                    cs = BillingService.getCon().prepareCall("{call insert_sales_transactions(?,?,?,?,?)}");
                    cs.setString(1, email);
                    cs.setString(2, movieId);
                    cs.setInt(3, quantity);
                    cs.setDate(4, today);
                    cs.setString(5, token);

                    ServiceLogger.LOGGER.info("Executing query " + cs.toString());
                    cs.execute();
                    ServiceLogger.LOGGER.info("Query executed successfully");

                } catch (SQLException e) {
                    ServiceLogger.LOGGER.warning("Error when calling stored procedure 'insert_sales_transactions' ");
                    e.printStackTrace();
                }
            }



            // now clear shopping cart
            String query = "DELETE FROM carts WHERE email = ?";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getEmail());
            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query executed successfully");


            return responseModel;

        }catch (SQLException e) {
                ServiceLogger.LOGGER.warning("Unable to Place order ");
                e.printStackTrace();

                OrderResponseModel responseModel = new OrderResponseModel(-1, "Internal Server Error");
                return responseModel;
        }
    }

    public static OrderResponseModel RetrieveOrder(OrderRequestModel requestModel){

        try{
            //first check if customer exists..
            String query1 = "SELECT * FROM customers WHERE email = ?";
            PreparedStatement ps1 = BillingService.getCon().prepareStatement(query1);
            ps1.setString(1, requestModel.getEmail());

            ServiceLogger.LOGGER.info("Executing query " + ps1.toString());
            ResultSet rs1 = ps1.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(!rs1.next()){
                ServiceLogger.LOGGER.info("No customer found");
                OrderResponseModel responseModel = new OrderResponseModel(332, "Customer does not exist.");
                return responseModel;
            }

            //Customer exists, now retrieve order from sales, transactions, and movie_prices tables
            String query2 = "SELECT * FROM sales, transactions, movie_prices WHERE sales.id = transactions.sId AND movie_prices.movieId = sales.movieId AND email = ?";
            PreparedStatement ps2 = BillingService.getCon().prepareStatement(query2);
            ps2.setString(1, requestModel.getEmail());

            ServiceLogger.LOGGER.info("Executing query " + ps2.toString());
            ResultSet rs2 = ps2.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(!rs2.next()){
                ServiceLogger.LOGGER.info("Customer has no order history");
                OrderResponseModel responseModel = new OrderResponseModel(3410, "Orders retrieved successfully.");
                return responseModel;
            }

            //Customer has Order history...
            rs2.beforeFirst();
            List<String> transactionsAdded = new ArrayList<String>();
            OrderResponseModel responseModel = new OrderResponseModel(3410, "Orders retrieved successfully.");
            while(rs2.next()){
                String email = rs2.getString("email");
                String movieId = rs2.getString("movieId");
                int quantity = rs2.getInt("quantity");
                String saleDate = rs2.getDate("saleDate").toString();
                String transactionId = rs2.getString("transactionId");
                float unit_price = rs2.getFloat("unit_price");
                float discount = rs2.getFloat("discount");
                String state;
                Amounts amount;
                TransactionFee transactionFee;
                String createTime;
                String updateTime;

                //now get sale information using the transaction ID...
                if(!transactionsAdded.contains(transactionId) && transactionId != null) {
                    APIContext apiContext = new APIContext(PaypalFunctions.getClientId(), PaypalFunctions.getSecret(), "sandbox");
                    try {
                        //new transaction found
                        Sale sale = Sale.get(apiContext, transactionId);
                        ServiceLogger.LOGGER.info("Got sale information with the transactionId...");
                        state = sale.getState();
                        amount = new Amounts(sale.getAmount().getTotal(), sale.getAmount().getCurrency());
                        transactionFee = new TransactionFee(sale.getTransactionFee().getValue(), sale.getTransactionFee().getCurrency());
                        createTime = sale.getCreateTime();
                        updateTime = sale.getUpdateTime();
                        Order order = new Order(email, movieId, quantity, unit_price, discount, saleDate);

                        //now create transaction object
                        Transactions transaction = new Transactions(transactionId,state,amount,transactionFee,createTime,updateTime);
                        transaction.addOrder(order);
                        responseModel.addTransaction(transaction);
                        transactionsAdded.add(transactionId);


                    } catch (PayPalRESTException e) {
                        ServiceLogger.LOGGER.info("Error in getting sale information");
                        responseModel = new OrderResponseModel(-1, "Internal Server Error.");
                        return responseModel;
                    }


                }
                else{
                    //need to add item to an existing transaction...
                    if(transactionId != null) {
                        ServiceLogger.LOGGER.info("Adding an item to existing transaction");
                        Order order = new Order(email, movieId, quantity, unit_price, discount, saleDate);
                        responseModel.addOrderToTransaction(transactionId, order);
                    }
                }
            }
            return responseModel;

        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to Place order ");
            e.printStackTrace();

            OrderResponseModel responseModel = new OrderResponseModel(-1, "Internal Server Error");
            return responseModel;
        }


    }

    public static OrderResponseModel CompleteOrder(String transactionId,  String token) {

        try {
            String query1 = "SELECT * FROM transactions WHERE token = ?";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query1);
            ps.setString(1, token);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if (!rs.next()) {
                ServiceLogger.LOGGER.info("Token not found");
                OrderResponseModel responseModel = new OrderResponseModel(3421, "Token not found.");
                return responseModel;
            }

            //token found
            String query2 = "UPDATE transactions SET transactionId = ? WHERE token = ?";
            PreparedStatement ps2 = BillingService.getCon().prepareStatement(query2);
            ps2.setString(1, transactionId);
            ps2.setString(2, token);

            ServiceLogger.LOGGER.info("Executing query " + ps2.toString());
            ps2.executeUpdate();
            ServiceLogger.LOGGER.info("Query executed successfully");

            OrderResponseModel responseModel = new OrderResponseModel(3420, "Payment is completed successfully.");
            return responseModel;


        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable Update transactions table ");
            OrderResponseModel responseModel = new OrderResponseModel(-1, "Internal Server Error.");
            return responseModel;
        }

    }



}
