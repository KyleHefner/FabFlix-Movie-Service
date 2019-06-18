package edu.uci.ics.khefner.service.billing.core;


import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import edu.uci.ics.khefner.service.billing.BillingService;
import edu.uci.ics.khefner.service.billing.logger.ServiceLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaypalFunctions {

    private static String clientId = "AdDZGoL3SCqYR7PWcf1ruVqwjoEVmbwLO7kQ5evdjBKeaVcE-n6pfIm7M0ljxZ3n4VCiY9C29vQIv33W";
    private static String secret = "EDrl198ra4he7Bmzi0YMYgzNaL1jzLrdG9NXcP0GWJh8W69sRCHV3b4tNc_A9iLaCOGrrtqnmy7LsV34";

    public static Map<String, String> createPayment (String sum){
        Map<String, String> response = new HashMap<String, String>();
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(sum);
        ServiceLogger.LOGGER.info("Amount details: " + amount.toString());
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:3990");
        redirectUrls.setReturnUrl("http://"+ BillingService.getConfigs().getHostName()+":" + BillingService.getConfigs().getPort()+"/api/billing/order/complete");
        payment.setRedirectUrls(redirectUrls);

        try{
            APIContext apiContext = new APIContext(clientId, secret, "sandbox");
            Payment createdPayment = payment.create(apiContext);
            String redirectUrl = "";
            if(createdPayment!=null){
                ServiceLogger.LOGGER.info("Payment was created");
                List<Links> links = createdPayment.getLinks();
                for (Links link:links) {
                    if(link.getRel().equals("approval_url")){
                        redirectUrl = link.getHref();
                        ServiceLogger.LOGGER.info("redirect URL: " + redirectUrl);
                        break;
                    }
                }
                response.put("status", "success");
                response.put("redirect_url", redirectUrl);
            }
            else{
                ServiceLogger.LOGGER.info("Payment was not created");
            }


        }catch(PayPalRESTException e){
            ServiceLogger.LOGGER.info("Error happened during payment creation!");
            response.put("status", "failure");
        }catch (Exception ex){
            ServiceLogger.LOGGER.info("Error happened during payment creation! UNKNOWN ERROR");
            response.put("status", "failure");
        }
        return response;
    }


    public static Map<String, String>CompletePayment(String paymentId, String payerID){
        Map<String, String> response = new HashMap();
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerID);
        try{
            APIContext context = new APIContext(clientId, secret, "sandbox");
            Payment createdPayment = payment.execute(context, paymentExecution);

            if(createdPayment!=null){
                response.put("status", "success");
                response.put("transactionId", createdPayment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId());
            }
            else{
                response.put("status", "failure");
            }
        }catch (PayPalRESTException e) {
            ServiceLogger.LOGGER.info("Failed to complete payment");
            response.put("status", "failure");
        }
        return response;
    }


    public static String getClientId() {
        return clientId;
    }

    public static void setClientId(String clientId) {
        PaypalFunctions.clientId = clientId;
    }

    public static String getSecret() {
        return secret;
    }

    public static void setSecret(String secret) {
        PaypalFunctions.secret = secret;
    }
}
