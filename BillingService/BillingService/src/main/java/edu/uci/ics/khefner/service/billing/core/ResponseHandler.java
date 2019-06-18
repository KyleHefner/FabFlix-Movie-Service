package edu.uci.ics.khefner.service.billing.core;

import static edu.uci.ics.khefner.service.billing.core.ResultCodeGlobals.*;


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
            case QUANTITY_HAS_INVALID_VALUE: return "Quantity has invalid value.";
            case SHOPPING_CART_ITEM_CLEARED_SUCCESSFULLY: return "Shopping cart cleared successfully.";
            case SHOPPING_CART_ITEM_DELETED_SUCCESSFULLY: return "Shopping cart item deleted successfully.";
            case SHOPPING_CART_ITEM_INSERTED_SUCCESSFULLY: return "Shopping cart item inserted successfully.";
            case SHOPPING_CART_ITEM_RETRIEVED_SUCCESSFULLY: return "Shopping cart retrieved successfully.";
            case SHOPPING_CART_ITEM_UPDATED_SUCCESSFULLY: return "Shopping cart item updated successfully.";
            case SHOPPING_ITEM_DOES_NOT_EXIST: return "Shopping item does not exist.";
            case DUPLICATE_INSERTION: return "Duplicate insertion.";

            case CREDIT_CARD_ID_HAS_INVALID_LENGTH: return "Credit card ID has invalid length.";
            case CREDIT_CARD_ID_HAS_INVALID_VALUE: return "Credit card ID has invalid value.";
            case EXPIRATION_HAS_INVALID_VALUE: return "expiration has invalid value.";
            case CREDIT_CARD_DOES_NOT_EXIST: return "Credit card does not exist.";

            case CREDIT_CARD_INSERTED_SUCCESSFULLY : return "Credit card inserted successfully.";
            case CREDIT_CARD_UPDATED_SUCCESSFULLY : return "Credit card updated successfully.";
            case CREDIT_CARD_DELETED_SUCCESSFULLY : return "Credit card deleted successfully.";
            case CREDIT_CARD_RETRIEVED_SUCCESSFULLY : return "Credit card retrieved successfully.";

            default: return "";
        }

    }
}
