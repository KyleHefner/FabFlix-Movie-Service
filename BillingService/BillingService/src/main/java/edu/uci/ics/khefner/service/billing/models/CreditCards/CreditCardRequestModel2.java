package edu.uci.ics.khefner.service.billing.models.CreditCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import static edu.uci.ics.khefner.service.billing.core.ResultCodeGlobals.*;

public class CreditCardRequestModel2 {

    private String id;


    @JsonCreator
    public CreditCardRequestModel2(@JsonProperty(value = "id", required = true) String id) {
        this.id = id;
    }

    public boolean isValid(){
        if(id == null || id.length() != 19){
            return false;
        }

        if(!id.matches("[0-9]+")){
            return false;
        }
        return true;

    }

    public int getResultCode(){
        if(id == null || id.length() != 19){
            return CREDIT_CARD_ID_HAS_INVALID_LENGTH;
        }

        if(!id.matches("[0-9]+")){
            return CREDIT_CARD_ID_HAS_INVALID_VALUE;
        }


        return -1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
