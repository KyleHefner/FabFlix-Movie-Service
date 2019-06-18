package edu.uci.ics.khefner.service.idm.models.VerifyPrivilege;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.idm.core.EmailChecker;
import edu.uci.ics.khefner.service.idm.logger.ServiceLogger;
import static edu.uci.ics.khefner.service.idm.core.ResultCodeGlobals.*;
import static edu.uci.ics.khefner.service.idm.core.ResultCodeGlobals.*;

public class VerifyPrivilegeRequestModel {
    private String email;
    private int plevel;
    @JsonIgnore
    private boolean valid;
    @JsonIgnore
    private int resultCode;

    @JsonCreator
    public VerifyPrivilegeRequestModel(@JsonProperty(value = "email", required = true) String email,
                                       @JsonProperty(value = "plevel", required = true) int plevel) {
        this.email = email;
        this.plevel = plevel;

    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPlevel() {
        return plevel;
    }

    public void setPlevel(int plevel) {
        this.plevel = plevel;
    }

    @JsonIgnore
    public boolean Valid() {
        EmailChecker checkEmail = new EmailChecker(email);

        if (plevel < 1 || plevel > 5) {
            ServiceLogger.LOGGER.info("plevel is not valid integer");
            return false;
        }

        if (!checkEmail.isEmailValidLength()) {
            ServiceLogger.LOGGER.info("Email is not valid length");
            return false;
        }

        if (!checkEmail.isEmailValidFormat()) {
            ServiceLogger.LOGGER.info("Email is not valid format");
            return false;
        }
        return true;
    }

    @JsonIgnore
    public int ResultCode() {

        EmailChecker checkEmail = new EmailChecker(email);

        //Check edu.ics.uci.khefner.service.idm.core.ResponseHandler for info on numbers being returned
        if (plevel < 1 || plevel > 5) {
            return PRIVILEGE_LEVEL_OUT_OF_RANGE;
        }
        if (!checkEmail.isEmailValidLength()) {
            return INVALID_LENGTH_EMAIL;
        }
        if (!checkEmail.isEmailValidFormat()) {
            return INVALID_FORMAT_EMAIL;
        }

        return 0;
    }

}

