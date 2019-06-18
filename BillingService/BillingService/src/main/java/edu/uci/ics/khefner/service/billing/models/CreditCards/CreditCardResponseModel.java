package edu.uci.ics.khefner.service.billing.models.CreditCards;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.khefner.service.billing.core.CreditCard;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CreditCardResponseModel {
    @JsonProperty(value = "resultCode")
    int resultCode;
    @JsonProperty(value = "message")
    String message;
    @JsonProperty(value = "creditcard")
    CreditCard creditcard;

    public CreditCardResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
        this.creditcard = null;
    }

    public CreditCardResponseModel(int resultCode, String message, CreditCard card){
        this.resultCode = resultCode;
        this.message = message;
        this.creditcard = new CreditCard(card.getId(), card.getFirstName(), card.getLastName(), card.getExpiration());
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



}
