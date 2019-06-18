package edu.uci.ics.khefner.service.api_gateway.DatabaseQueries;

import edu.uci.ics.khefner.service.api_gateway.GatewayService;
import edu.uci.ics.khefner.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.khefner.service.api_gateway.models.NoContentResponseModel;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteResponse {

    public static void DeleteResponse(String transactionID){

        //get connection from connection pool
        Connection con = GatewayService.getConPool().requestCon();

        try {
            String query = "DELETE FROM responses WHERE transactionid = ?";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, transactionID);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query executed successfully");

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to delete response because of database query error.");
            e.printStackTrace();
        }

        //return connection to connection pool
        GatewayService.getConPool().releaseCon(con);

    }
}
