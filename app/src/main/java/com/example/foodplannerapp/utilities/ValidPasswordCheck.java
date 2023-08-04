package com.example.foodplannerapp.utilities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 7th July 2023
 * ValidPasswordCheck.java
 * Description - Utility class for checking if password meets minimum requirements
 */

// imports
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// @Reference - https://www.geeksforgeeks.org/how-to-validate-a-password-using-regular-expressions-in-java/
// method for checking if user supplied password is valid
public final class ValidPasswordCheck{

    private ValidPasswordCheck(){}

    public static boolean isPasswordValid(String password) {
        // Regex to check valid password.
        String regex = "^(?=.*\\d)"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the password is empty
        // return false
        if (password == null) {
            return true;
        }

        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.
        Matcher m = p.matcher(password);

        // Return if the password
        // matched the ReGex
        return m.matches();
    }
}