package edu.uci.ics.khefner.service.idm.core.VerifySessionCore;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import edu.uci.ics.khefner.service.idm.BasicService;
import edu.uci.ics.khefner.service.idm.Security.Session;
import edu.uci.ics.khefner.service.idm.core.ResponseHandler;
import edu.uci.ics.khefner.service.idm.logger.ServiceLogger;
import edu.uci.ics.khefner.service.idm.models.VerifySession.VerifySessionResponseModel;
import static edu.uci.ics.khefner.service.idm.core.ResultCodeGlobals.*;


public class VerifySessionDBQueries {
    public static VerifySessionResponseModel VerifySession(String email, String sessionID){
        //verify a session
        ServiceLogger.LOGGER.info("Attempting to verify session for email = " + email +"\n sessionID = " + sessionID);
        try {

            String query2 = "SELECT * FROM users WHERE email = ?";
            String query = "SELECT * FROM sessions WHERE sessionID = ?";


            PreparedStatement ps = BasicService.getCon().prepareStatement(query);
            PreparedStatement ps2 = BasicService.getCon().prepareStatement(query2);

            ps.setString(1, sessionID);
            ps2.setString(1, email);

            ServiceLogger.LOGGER.info("Executing query " + ps2.toString());
            ResultSet rs2 = ps2.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(!rs2.next()){
                //no User found
                ServiceLogger.LOGGER.info("User not found for " + email);
                int resultCode = USER_NOT_FOUND;
                String message = ResponseHandler.GetResponseMessage(resultCode);
                return new VerifySessionResponseModel(resultCode,message);
            }else{
                //user found
                ServiceLogger.LOGGER.info("User found for " + email);
                //now handle query for session
                if(rs.next()){
                    //session found
                    ServiceLogger.LOGGER.info("Session found for user " + email);


                    Timestamp lastUsed = rs.getTimestamp("lastUsed");
                    Timestamp timeCreated = rs.getTimestamp("timeCreated");
                    Timestamp exprTime = rs.getTimestamp("exprTime");
                    int status = rs.getInt("status");

                    //first check if session status is Closed, Revoked, or Expired already and return response model
                    //If the session is Active, then continue to check the last used time
                    if(status == Session.REVOKED)
                    {
                        ServiceLogger.LOGGER.info("Session is revoked");
                        return new VerifySessionResponseModel(SESSION_IS_REVOKED, ResponseHandler.GetResponseMessage(SESSION_IS_REVOKED));
                    }
                    if(status == Session.EXPIRED)
                    {
                        ServiceLogger.LOGGER.info("Session is Expired");
                        return new VerifySessionResponseModel(SESSION_IS_EXPIRED, ResponseHandler.GetResponseMessage(SESSION_IS_EXPIRED));
                    }
                    if(status == Session.CLOSED)
                    {
                        ServiceLogger.LOGGER.info("Session is Closed");
                        return new VerifySessionResponseModel(SESSION_IS_CLOSED, ResponseHandler.GetResponseMessage(SESSION_IS_CLOSED));
                    }

                    //session status is ACTIVE. Create a new session incase the Session in question needs to be replaced
                    Session session = Session.createSession(email);

                    //Session active too long before last use, so Revoke it and User must repeat login(Login process not done here)
                    if((session.getTimeCreated().getTime() - lastUsed.getTime() > Session.SESSION_TIMEOUT)){
                        ServiceLogger.LOGGER.info("Session timeout for sessionID = " + sessionID + "\n setting status to Revoked");
                        //revoke session and return response model
                        VerifySessionResponseModel responseModel = UpdateSession(sessionID, Session.REVOKED);
                        return responseModel;
                    }
                    //Session has expired. Set it to expired.
                    else if(session.getTimeCreated().getTime() > exprTime.getTime()){
                        ServiceLogger.LOGGER.info("sessionID = " + sessionID + "\n has expired. Setting status to Expired");
                        //session is expired. set it as expired then return response model
                        VerifySessionResponseModel responseModel = UpdateSession(sessionID,Session.EXPIRED);
                    }
                    //Session is still active, set it to revoked and create and return a new seesionID
                    else if(exprTime.getTime() - session.getTimeCreated().getTime() < Session.SESSION_TIMEOUT){
                        //revoke current session and generate a new session for User
                        UpdateSession(sessionID,Session.REVOKED);
                        //create new session obj
                        Session newSession = Session.createSession(email);
                        ServiceLogger.LOGGER.info("Revoking sessionID = " + sessionID + "\n new sessionID = " + newSession.getSessionID().toString());
                        VerifySessionResponseModel responseModel = InsertSession(newSession);
                        return responseModel;
                    }
                    //session is still active and valid.
                    // need to update lastused time.
                    else if(lastUsed.getTime() < exprTime.getTime()){
                        ServiceLogger.LOGGER.info("sessionID = " + sessionID + "\n is still Active and Valid. Updating last used time");
                        VerifySessionResponseModel responseModel = UpdateLastUsedTime(sessionID);
                        return responseModel;
                    }

                }else{
                    ServiceLogger.LOGGER.info("Session not found");
                    int resultCode = SESSION_NOT_FOUND;
                    String message = ResponseHandler.GetResponseMessage(resultCode);
                    return new VerifySessionResponseModel(resultCode,message);
                }

            }
        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to create user " + email);
            e.printStackTrace();
        }
        int resultCode = INTERNAL_SERV_ERROR;
        String message = ResponseHandler.GetResponseMessage(resultCode);
        return new VerifySessionResponseModel(resultCode, message);

    }

    public static VerifySessionResponseModel UpdateSession(String sessionID, int status){
        try{
            String query = "UPDATE sessions SET status = ? WHERE sessionID = ?";
            PreparedStatement ps = BasicService.getCon().prepareStatement(query);
            ps.setInt(1, status);
            ps.setString(2, sessionID);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(status == Session.REVOKED){
                //return response model with Session is revoked
                int resultCode = SESSION_IS_REVOKED;
                String message = ResponseHandler.GetResponseMessage(resultCode);
                return new VerifySessionResponseModel(resultCode,message);
            }
            else if (status == Session.EXPIRED){
                int resultCode = SESSION_IS_EXPIRED;
                String message = ResponseHandler.GetResponseMessage(resultCode);
                return new VerifySessionResponseModel(resultCode,message);
            }

        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to verify session:  " + sessionID);
            e.printStackTrace();
        }
        int resultCode = INTERNAL_SERV_ERROR;
        String message = ResponseHandler.GetResponseMessage(resultCode);
        return new VerifySessionResponseModel(resultCode, message);

    }

    public static VerifySessionResponseModel InsertSession(Session session){
        //Insert new session into session db
        try {
            String query = "INSERT INTO sessions(sessionID,email,status,timeCreated,lastUsed,exprTime)" +
                    "VALUES (?,?,?,?,?,?)";


            PreparedStatement ps = BasicService.getCon().prepareStatement(query);
            ps.setString(1, session.getSessionID().toString());
            ps.setString(2, session.getEmail());
            ps.setInt(3, Session.ACTIVE);
            ps.setTimestamp(4, session.getTimeCreated());
            ps.setTimestamp(5, session.getLastUsed());
            ps.setTimestamp(6, session.getExprTime());

            ServiceLogger.LOGGER.info("executing query " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query executed successfully");

            //return response model with new sessionID and Session is Active
            int resultCode = SESSION_IS_ACTIVE;
            String message = ResponseHandler.GetResponseMessage(resultCode);
            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(resultCode,message);
            responseModel.setSessionID(session.getSessionID().toString());
            return responseModel;

        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to create session ");
            e.printStackTrace();
        }
        int resultCode = INTERNAL_SERV_ERROR;
        String messsage = ResponseHandler.GetResponseMessage(resultCode);
        return new VerifySessionResponseModel(resultCode,messsage);
    }

    public static VerifySessionResponseModel UpdateLastUsedTime(String sessionID){
        //updates last used time of the session to current time
        try {
            String query = "UPDATE sessions SET lastUsed = ? WHERE sessionID = ?";
            PreparedStatement ps = BasicService.getCon().prepareStatement(query);

            Timestamp lastUsed = new Timestamp(System.currentTimeMillis());

            ps.setTimestamp(1,lastUsed);
            ps.setString(2, sessionID);

            ServiceLogger.LOGGER.info("Executing Query " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query executed successfully");
            //return response model Session is active
            int resultCode = SESSION_IS_ACTIVE;
            String message = ResponseHandler.GetResponseMessage(resultCode);
            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(resultCode,message);
            responseModel.setSessionID(sessionID);
            return responseModel;

        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to create session ");
            e.printStackTrace();
        }
        int resultCode = INTERNAL_SERV_ERROR;
        String messsage = ResponseHandler.GetResponseMessage(resultCode);
        return new VerifySessionResponseModel(resultCode,messsage);
    }

}
