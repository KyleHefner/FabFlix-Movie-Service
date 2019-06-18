package edu.uci.ics.khefner.service.idm.core.RegisterUserCore;


import edu.uci.ics.khefner.service.idm.BasicService;
import edu.uci.ics.khefner.service.idm.Security.Crypto;
import edu.uci.ics.khefner.service.idm.logger.ServiceLogger;
import static edu.uci.ics.khefner.service.idm.Security.Crypto.*;
import edu.uci.ics.khefner.service.idm.models.Register.RegisterRequestModel;
import edu.uci.ics.khefner.service.idm.models.Register.RegisterResponseModel;
import org.apache.commons.codec.binary.Hex;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;



import static edu.uci.ics.khefner.service.idm.core.ResultCodeGlobals.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//class used by "/api/BasicService/register" api endpoint to send queries to the database
public class RegisterUserDBQueries {

    //check if email of a newly registering user is already in use
    //return 0: when email is already in use
    //       1: when email is not in use
    //       2: error could not query db
    public static int isEmailInUse(String Email){

        try {
            String query = "SELECT email From users WHERE email=?";

            PreparedStatement ps = BasicService.getCon().prepareStatement(query);

            ps.setString(1, Email);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                String dbEmail = rs.getString("email");
                ServiceLogger.LOGGER.info("Query returned: " + dbEmail);
                if (dbEmail.equals(Email)) {
                    ServiceLogger.LOGGER.info("Email already in use");
                    return 0;
                }
            }
            else{
                ServiceLogger.LOGGER.info("Email not in use");
                return 1;
            }


        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve email.");
            //return internal server error response model
            //return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            //e.printStackTrace();
        }
        return 2;
    }

    //enter email and password into db with password salted and hashed
    public static boolean InsertNewUser(String email, char[] password){
        //first generate a salt
        byte[] salt = Crypto.genSalt();

        //now hash the password using the salt
        String hashedPassword = Hex.encodeHexString(Crypto.hashPassword(password, salt, ITERATIONS, KEY_LENGTH));
        String encodedSalt = Hex.encodeHexString(salt);



        int status = 1; //for ACTIVE status
        //should be set to 2 for Closed status after response on piazza post
        int privLevel = 5; //for USER

        //enter user into db
        try {
            String query = "INSERT INTO users(email,plevel,salt,pword,status) VALUES (?,?,?,?,?)";

            PreparedStatement ps = BasicService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ps.setInt(2, privLevel);
            ps.setString(3, encodedSalt);
            ps.setString(4, hashedPassword);
            ps.setInt(5, status);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            return true;
        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to create user " + email);
            e.printStackTrace();
        }
        return false;
    }

}
