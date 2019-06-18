package edu.uci.ics.khefner.service.idm.models.Register;

import static edu.uci.ics.khefner.service.idm.core.ResultCodeGlobals.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.idm.core.EmailChecker;
import edu.uci.ics.khefner.service.idm.core.PasswordChecker;
import edu.uci.ics.khefner.service.idm.logger.ServiceLogger;


public class RegisterRequestModel {

        private String email;
        private char[] password;


        public RegisterRequestModel() {}

        @JsonCreator
        public RegisterRequestModel(@JsonProperty(value = "email", required = true) String email,
                                    @JsonProperty (value = "password", required = true) char[] password) {
            this.email = email;
            ServiceLogger.LOGGER.info("checking password");
            if (password == null){
                ServiceLogger.LOGGER.info("password is empty");
                this.password = new char[0];
            }else {
                this.password = new char[password.length];
                this.password = password.clone();
            }



        }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public boolean isValid(){
            PasswordChecker checkPassword = new PasswordChecker(password);
            EmailChecker checkEmail = new EmailChecker(email);

            if(checkPassword.isPasswordEmpty()) {
                ServiceLogger.LOGGER.info("Password is empty");
                return false;
            }

            if(!checkPassword.isPasswordValidLength()) {
                ServiceLogger.LOGGER.info("Password is not valid length");
                return false;
            }

            if(!checkPassword.isPasswordValidFormat()) {
                ServiceLogger.LOGGER.info("Password is not valid format");
                return false;
            }

            if(!checkEmail.isEmailValidLength()){
                ServiceLogger.LOGGER.info("Email is not valid length");
                return false;
            }

            if(!checkEmail.isEmailValidFormat()){
                ServiceLogger.LOGGER.info("Email is not valid format");
                return false;
            }

            return true;
    }

    //return a resultCode based on password and email validation
    public int getResultCode (){
            PasswordChecker checkPassword = new PasswordChecker(password);
            EmailChecker checkEmail = new EmailChecker(email);

            //Check edu.ics.uci.khefner.service.idm.core.ResponseHandler for info on numbers being returned
            if(checkPassword.isPasswordEmpty()) {return INVALID_LENGTH_PASSWORD;}
            if(!checkPassword.isPasswordValidLength()) {return INVALID_LENGTH_REQUIREMENTS_PASSWORD;}
            if(!checkPassword.isPasswordValidFormat()) {return INVALID_CHARACTER_REQUIREMENTS_PASSWORD;}

            if(!checkEmail.isEmailValidLength()) {return INVALID_LENGTH_EMAIL;}
            if(!checkEmail.isEmailValidFormat()) {return INVALID_FORMAT_EMAIL;}

            return 0;

        }

    public void clearPassword(){
            for(int i = 0; i>=password.length; i++){
                this.password[i]= Character.MIN_VALUE;
            }
    }

}
