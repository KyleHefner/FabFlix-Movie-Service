package edu.uci.ics.khefner.service.idm.core.LoginUserCore;

import edu.uci.ics.khefner.service.idm.BasicService;
import edu.uci.ics.khefner.service.idm.Security.Crypto;
import edu.uci.ics.khefner.service.idm.Security.Session;
import edu.uci.ics.khefner.service.idm.Security.Token;
import edu.uci.ics.khefner.service.idm.core.ResponseHandler;
import edu.uci.ics.khefner.service.idm.logger.ServiceLogger;
import edu.uci.ics.khefner.service.idm.models.Login.LoginResponseModel;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import static edu.uci.ics.khefner.service.idm.core.ResultCodeGlobals.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static edu.uci.ics.khefner.service.idm.Security.Crypto.ITERATIONS;
import static edu.uci.ics.khefner.service.idm.Security.Crypto.KEY_LENGTH;

public class LoginUserDBQueries {

    public static LoginResponseModel LoginUser(String email, char[] password) {



        //now hash the password using the salt

        try {
            String query = "SELECT * FROM users WHERE email = ?";

            PreparedStatement ps = BasicService.getCon().prepareStatement(query);
            ps.setString(1, email);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if (rs.next()) {
                //User exists
                //now retrieve salt from db and use it to salt and hash the clients password
                String saltString = rs.getString("salt");
                String dbPassword = rs.getString("pword");



               byte[] decodedSalt = new byte[1];
                try {
                   decodedSalt = Hex.decodeHex(saltString);

                }catch(DecoderException d){ServiceLogger.LOGGER.info("Error decoding salt");}

                String hashedPassword = Hex.encodeHexString(Crypto.hashPassword(password,decodedSalt,ITERATIONS,KEY_LENGTH));

                //now check if the hashed passwords match

                ServiceLogger.LOGGER.info("hashed password from db: " + dbPassword);
                ServiceLogger.LOGGER.info("hashed password from client: " + hashedPassword);
                if (dbPassword.equals(hashedPassword)) {
                    ServiceLogger.LOGGER.info("Passwords matched");



                    //now see if there is already an active session in sessions table
                    //if there is, revoke it and insert new session
                    //if not, only insert a new session
                    LoginResponseModel responseModel = InsertSession(email);
                    ServiceLogger.LOGGER.info("Login successfull");
                    return responseModel;


                }else {

                    ServiceLogger.LOGGER.info("Passwords did not match");
                    int resultCode = PASSWORDS_DO_NOT_MATCH;
                    String message = ResponseHandler.GetResponseMessage(resultCode);
                    return new LoginResponseModel(resultCode, message);
                }


            } else {
                //user doesn't exist so return appropriate response model
                ServiceLogger.LOGGER.info("User not found in database");
                int resultCode = USER_NOT_FOUND;
                String message = ResponseHandler.GetResponseMessage(resultCode);
                return new LoginResponseModel(resultCode, message);
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to login user " + email);
            e.printStackTrace();
        }
        int resultCode = INTERNAL_SERV_ERROR;
        String message = ResponseHandler.GetResponseMessage(resultCode);
        return new LoginResponseModel(resultCode, message);
    }






    public static LoginResponseModel InsertSession(String email){
        //Inserts new session while also revoking all previous sessions
        // returns true if successfull, false otherwise
        if(revokeSessions(email)){
            //sessions successfully revoked
            ServiceLogger.LOGGER.info("Revoked all sessions of email: " + email);
        }else{
            //error revoking sessions return response model
            ServiceLogger.LOGGER.info("Error revoking sessions of email: " + email);
            int resultCode = INTERNAL_SERV_ERROR;
            String message = ResponseHandler.GetResponseMessage(resultCode);
            return new LoginResponseModel(resultCode,message);
        }
            //Inserts new session
        try {
            String query = "INSERT INTO sessions(sessionID,email,status,timeCreated,lastUsed,exprTime)" +
                            "VALUES (?,?,?,?,?,?)";

            ServiceLogger.LOGGER.info("Creating a new session for email: " + email);
            Session session = Session.createSession(email);

            PreparedStatement ps = BasicService.getCon().prepareStatement(query);
            ps.setString(1, session.getSessionID().toString());
            ps.setString(2, session.getEmail());
            ps.setInt(3, Session.ACTIVE);
            ps.setTimestamp(4, session.getTimeCreated());
            ps.setTimestamp(5, session.getLastUsed());
            ps.setTimestamp(6, session.getExprTime());

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query executed successfully");

            //build a response model with sessionId then return it
            String sessionID = session.getSessionID().toString();
            int resultCode = USER_LOGGED_IN_SUCCESSFULLY;
            String message = ResponseHandler.GetResponseMessage(resultCode);
            LoginResponseModel responseModel = new LoginResponseModel(resultCode,message);
            responseModel.setSessionID(sessionID);
            return responseModel;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to create user " + email);
            e.printStackTrace();
        }
        int resultCode = INTERNAL_SERV_ERROR;
        String messsage = ResponseHandler.GetResponseMessage(resultCode);
        return new LoginResponseModel(resultCode,messsage);
    }







    public static boolean revokeSessions(String email){
            //revokes all sessions with this email
        try {
            String query = "UPDATE sessions SET status = 4, lastUsed = ? " +
                    "WHERE email = ? AND status = 1";

            PreparedStatement ps = BasicService.getCon().prepareStatement(query);
            //get current timestamp
            Timestamp lastUsd = new Timestamp(System.currentTimeMillis());
            //enter values into query
            ps.setTimestamp(1,lastUsd);
            ps.setString(2, email);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query executed successfully");
            return true;
        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to create user " + email);
            e.printStackTrace();
        }
        return false;
    }
}
