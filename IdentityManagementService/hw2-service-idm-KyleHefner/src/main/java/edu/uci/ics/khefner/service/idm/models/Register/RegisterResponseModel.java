package edu.uci.ics.khefner.service.idm.models.Register;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


public class RegisterResponseModel {

    private int resultCode;
    private String message;

    public RegisterResponseModel() {}


    public RegisterResponseModel(int resultCode, String message){
        this.resultCode = resultCode;
        this.message = message;

    }


    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    //stopped here looking at piazza post should finish this class
}
