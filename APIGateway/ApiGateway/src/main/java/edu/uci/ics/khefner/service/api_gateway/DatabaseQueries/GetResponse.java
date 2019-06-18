package edu.uci.ics.khefner.service.api_gateway.DatabaseQueries;

import edu.uci.ics.khefner.service.api_gateway.GatewayService;
import edu.uci.ics.khefner.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.khefner.service.api_gateway.models.NoContentResponseModel;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetResponse {

    public static Response GetResponse (String transactionID){

        //get connection from connection pool
        Connection con = GatewayService.getConPool().requestCon();

        try {
            String query = "SELECT * FROM responses WHERE transactionid = ?";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, transactionID);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(!rs.next()){
                ServiceLogger.LOGGER.info("transactionID not found in responses table");
                NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
                return Response.status(Response.Status.NO_CONTENT).entity(responseModel).header("transactionID", transactionID).build();
            }
            //found response in response table
            rs.first();
            String sessionid = rs.getString("sessionid");
            String email = rs.getString("email");
            String jsonResponse = rs.getString("response");
            int httpStatus = rs.getInt("httpstatus");
            return Response.status(httpStatus).entity(jsonResponse).header("email", email).header("transactionID", transactionID).header("sessionID", sessionid).build();
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to Get response because of database query error.");
            e.printStackTrace();
        }

        //return connection to connection pool
        GatewayService.getConPool().releaseCon(con);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("transactionID", transactionID).build();

    }
}
