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
    void isValidPassword() {
        assertTrue(EditAccountActivity.isValidPassword("Password1#"));
        assertFalse(EditAccountActivity.isValidPassword("Password"));
        assertFalse(EditAccountActivity.isValidPassword("Pass"));
        assertFalse(EditAccountActivity.isValidPassword("Password1"));
        assertFalse(EditAccountActivity.isValidPassword("password1#"));
    }

}