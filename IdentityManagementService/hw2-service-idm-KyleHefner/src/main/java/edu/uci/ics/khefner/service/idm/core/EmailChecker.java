package edu.uci.ics.khefner.service.idm.core;

public class EmailChecker {
    //Class to check if email data is valid when Requesting to register
    private String Email;

    public EmailChecker (String Email){
        this.Email = Email;
    }


    public boolean isEmailValidLength(){ return Email != null && Email.length() > 0 && Email.length() <= 50 ;}

    public boolean isEmailValidFormat() {
        String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{1,6}$";
        return Email.matches(regex);
    }


}
