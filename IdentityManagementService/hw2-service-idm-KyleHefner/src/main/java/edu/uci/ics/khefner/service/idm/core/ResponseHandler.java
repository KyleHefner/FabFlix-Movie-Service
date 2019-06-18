package edu.uci.ics.khefner.service.idm.core;
import static edu.uci.ics.khefner.service.idm.core.ResultCodeGlobals.*;


public class ResponseHandler {

    //given a resultCode, returns a message associated with it
    public static String GetResponseMessage(int resultCode){


        switch(resultCode){

            case INVALID_LENGTH_PASSWORD: return "Password has invalid length.";
            case INVALID_FORMAT_EMAIL: return "Email address has invalid format.";
            case INVALID_LENGTH_EMAIL: return "Email address has invalid length.";
            case JSON_PARSE_EXCEPTION : return "JSON Parse Exception.";
            case JSON_MAPPING_EXCEPTION : return "JSON Mapping Exception.";
            case INTERNAL_SERV_ERROR : return "Internal Server Error.";
            case INVALID_LENGTH_REQUIREMENTS_PASSWORD : return "Password does not meet length requirements.";
            case INVALID_CHARACTER_REQUIREMENTS_PASSWORD : return "Password does not meet character requirements.";
            case EMAIL_ALREADY_IN_USE : return "Email already in use.";
            case USER_REGISTERED_SUCCESSFULLY: return "User registered successfully.";
            case USER_LOGGED_IN_SUCCESSFULLY: return "User logged in successfully.";
            case PASSWORDS_DO_NOT_MATCH: return "Passwords do not match.";
            case USER_NOT_FOUND: return "User not found";
            case TOKEN_HAS_INVALID_LENGTH: return "Token has invalid length.";
            case SESSION_IS_ACTIVE: return "Session is active";
            case SESSION_IS_EXPIRED: return "Session is expired";
            case SESSION_IS_CLOSED: return "Session is closed";
            case SESSION_IS_REVOKED: return "Session is revoked";
            case SESSION_NOT_FOUND: return "Session not found";
            case PRIVILEGE_LEVEL_OUT_OF_RANGE: return "Privilege level out of valid range.";
            case USER_HAS_SUFFICIENT_PRIVILEGE: return "User has sufficient privilege level";
            case USER_HAS_INSUFFICIENT_PRIVILEGE: return "User has insufficient privilege level.";

            default: return "";
        }

    }
}
