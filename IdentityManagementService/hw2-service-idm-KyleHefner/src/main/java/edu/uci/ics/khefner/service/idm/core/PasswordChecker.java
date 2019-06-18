package edu.uci.ics.khefner.service.idm.core;
import java.lang.*;

public class PasswordChecker {
    //class to check if password data is valid when requesting to register
    private char[] password;

    public PasswordChecker(char[] password){
        this.password = password;
    }


    //check if password is empty or null
    public boolean isPasswordEmpty(){
        if (password == null ){ return true;}
        if(password.length == 0) {return true;}
        return false;

    }

    //check if password is a valid length >=7 and <=16
    public boolean isPasswordValidLength(){
        return password.length >= 7 && password.length <= 16;
    }

    //checks if password has at least one of each of the following:
    //       upper case letter
    //       lower case letter
    //       number 0-9
    //       special character
    public boolean isPasswordValidFormat() {
        //String regularExpression = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[\\Q !@##$%^&*()-_=+|\\'\";:,.<>/?[]{}~` \\E])";
        //   (?=.*[a-z]) is a lookahead to match at least one lower case letter
        //   (?=.*[A-Z]) is a lookahead to match at least one upper case letter
        //   (?=.*[0-9]) is a lookahead to match at least one number from 0-9
        //   (?=.*[\Q !@##$%^&*()-_=+|\'";:,.<>/?[]{}~` \E]) is a lookahead to match at least one special character
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasNumber = false;
        boolean hasSpecialChar = false;
        String specialCharacters = " !#$%&'()*+,-./:;<=>?@[]^_`{|}";

        int i;
        for (i = 0; i < password.length; i++) {

            //checks if current index "i" is Lowercase letter
            if (Character.isAlphabetic(password[i]) &&
                    Character.isLowerCase(password[i])) { hasLowerCase = true; }

            //checks if current index "i" is UpperCase letter
            if (Character.isAlphabetic(password[i]) &&
                    Character.isUpperCase(password[i])) { hasUpperCase = true; }

            //checks if current index "i" is a number
            if (Character.isDigit(password[i])) { hasNumber = true; }

            //checks if current index "i" is a special character
            // note: string.indexOf() returns >=0 if password[i] is present in the string
            //       and returns -1 if it isn't
            if (specialCharacters.indexOf(password[i]) >= 0) { hasSpecialChar = true;}

        }

        return hasLowerCase && hasUpperCase && hasNumber && hasSpecialChar;
    }

    //returns a result code
}
