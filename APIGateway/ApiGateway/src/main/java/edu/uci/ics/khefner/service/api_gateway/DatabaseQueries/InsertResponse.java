package edu.uci.ics.khefner.service.api_gateway.DatabaseQueries;

import edu.uci.ics.khefner.service.api_gateway.GatewayService;
import edu.uci.ics.khefner.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.khefner.service.api_gateway.threadpool.ClientRequest;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InsertResponse {

    public static void InsertResponse(ClientRequest clientRequest, Response response) {

        //get connection from connection pool
        Connection con = GatewayService.getConPool().requestCon();

        try {
            String query = "INSERT INTO responses(transactionid, email, sessionid, response, httpstatus) Values (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, clientRequest.getTransactionID());
            ps.setString(2, clientRequest.getEmail());
            ps.setString(3, clientRequest.getSessionID());
            ps.setString(4, response.readEntity(String.class));
            ps.setInt(5, response.getStatus());

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query executed successfully");
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to Insert response.");
            e.printStackTrace();
        }

        //return connection to connection pool
        GatewayService.getConPool().releaseCon(con);
    }


}
