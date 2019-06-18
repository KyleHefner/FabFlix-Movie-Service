package edu.uci.ics.khefner.service.idm.core.VerifyPrivilegeCore;

import edu.uci.ics.khefner.service.idm.BasicService;
import edu.uci.ics.khefner.service.idm.core.ResponseHandler;
import edu.uci.ics.khefner.service.idm.logger.ServiceLogger;
import edu.uci.ics.khefner.service.idm.models.Login.LoginResponseModel;
import edu.uci.ics.khefner.service.idm.models.VerifyPrivilege.VerifyPrivilegeResponseModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static edu.uci.ics.khefner.service.idm.core.ResultCodeGlobals.*;

public class VerifyPrivilegeDBQueries {

    public static VerifyPrivilegeResponseModel VerifyPrivilege(String email, int plevel){
        //Verifies privilege level of user in db
        try {
            String query = "SELECT plevel FROM users WHERE email = ?";

            PreparedStatement ps = BasicService.getCon().prepareStatement(query);
            ps.setString(1, email);

            ServiceLogger.LOGGER.info("Executing query " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query executed successfully");

            if(rs.next()){
                ServiceLogger.LOGGER.info("User was found");

                //check plevel
                int dbPlevel = rs.getInt("plevel");

                if(dbPlevel <= plevel){
                    ServiceLogger.LOGGER.info("User has sufficient privilege");
                    //User has sufficient privilege
                    int resultCode = USER_HAS_SUFFICIENT_PRIVILEGE;
                    String message = ResponseHandler.GetResponseMessage(resultCode);
                    return new VerifyPrivilegeResponseModel(resultCode,message);

                }
                else{
                    ServiceLogger.LOGGER.info("User has insufficient privilege");
                    //User doesn't have sufficient privilege
                    int resultCode = USER_HAS_INSUFFICIENT_PRIVILEGE;
                    String message = ResponseHandler.GetResponseMessage(resultCode);
                    return new VerifyPrivilegeResponseModel(resultCode,message);
                }
            }
            else{
                ServiceLogger.LOGGER.info("User not found");
                int resultCode = USER_NOT_FOUND;
                String message = ResponseHandler.GetResponseMessage(resultCode);
                return new VerifyPrivilegeResponseModel(resultCode,message);
            }
        }catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to verify privilege level for " + email);
            e.printStackTrace();
        }
        int resultCode = INTERNAL_SERV_ERROR;
        String message = ResponseHandler.GetResponseMessage(resultCode);
        return new VerifyPrivilegeResponseModel(resultCode, message);

    }
}
