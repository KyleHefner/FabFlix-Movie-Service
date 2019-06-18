package edu.uci.ics.khefner.service.billing.models.CreditCards;
import static edu.uci.ics.khefner.service.billing.core.ResultCodeGlobals.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.billing.logger.ServiceLogger;

import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.Date;

public class CreditCardRequestModel {

    private String id;
    private String firstName;
    private String lastName;
    private Date expiration;

    @JsonCreator
    public CreditCardRequestModel(@JsonProperty(value = "id", required = true) String id,
                                  @JsonProperty(value = "firstName", required = true) String firstName,
                                  @JsonProperty(value = "lastName", required = true) String lastName,
                                  @JsonProperty(value = "expiration", required = true) Date expiration) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        ServiceLogger.LOGGER.info(expiration.toString());
        this.expiration = expiration;

    }


    public boolean isValid(){
        if(id == null || id.length() != 19){
            return false;
        }

        if(!id.matches("[0-9]+")){
            return false;
        }

        Date currTime = new Date();
        if(expiration.before(currTime)){
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

        Date currTime = new Date();
        if(expiration.before(currTime)){
            return EXPIRATION_HAS_INVALID_VALUE;
        }
        return -1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getExpiration() {
        return expiration;
    }

    //public void setExpiration(Date expiration) {
        //this.expiration = new Date(expiration.getTime());
    //}
}
