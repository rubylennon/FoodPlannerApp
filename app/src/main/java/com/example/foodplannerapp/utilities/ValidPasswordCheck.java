package com.example.foodplannerapp.utilities;

// @Reference - https://www.geeksforgeeks.org/how-to-validate-a-password-using-regular-expressions-in-java/
// method for checking if user supplied password is valid
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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