package com.example.firebasecrudapplication.activities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class EditAccountActivityTest {

    @Test
    void onCreate() {
    }

    @Test
    public void isValidPasswordOne() {

        // Regex to check valid password.
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.

        String invalidPassword = "pass";
        Matcher m1 = p.matcher(invalidPassword);
        // Return false if password matched the ReGex
        assertFalse(m1.matches());
    }

    @Test
    public void isValidPasswordTwo() {

        // Regex to check valid password.
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.

        String invalidPassword2 = "password";
        Matcher m2 = p.matcher(invalidPassword2);
        // Return false if password matched the ReGex
        assertFalse(m2.matches());

    }

    @Test
    public void isValidPasswordThree() {

        // Regex to check valid password.
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.

        String invalidPassword3 = "passwordwithmorethan20characters";
        Matcher m3 = p.matcher(invalidPassword3);
        // Return false if password matched the ReGex
        assertFalse(m3.matches());
    }

    @Test
    public void isValidPasswordFour() {

        // Regex to check valid password.
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.

        String invalidPassword4 = "Password";
        Matcher m4 = p.matcher(invalidPassword4);
        // Return false if password matched the ReGex
        assertFalse(m4.matches());
    }

    @Test
    public void isValidPasswordFive() {

        // Regex to check valid password.
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.

        String invalidPassword5 = "Password1";
        Matcher m5 = p.matcher(invalidPassword5);
        // Return false if password matched the ReGex
        assertFalse(m5.matches());
    }

    @Test
    public void isValidPasswordSix() {

        // Regex to check valid password.
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.

        String validPassword1 = "Password1#";
        Matcher m6 = p.matcher(validPassword1);
        // Return true if password matched the ReGex
        assertTrue(m6.matches());
    }

    @Test
    void onCreateOptionsMenu() {
    }

    @Test
    void onOptionsItemSelected() {
    }
}