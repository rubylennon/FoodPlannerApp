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
        // create regex pattern
        String regex = "^(?=.*\\d)"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";
        // regex compilation
        Pattern pattern = Pattern.compile(regex);
        // return false if password is empty
        if (password == null) {
            return true;
        }
        // use matcher method to check if password matches
        Matcher matcher = pattern.matcher(password);
        // Return true if password matches regex
        return matcher.matches();
    }
}