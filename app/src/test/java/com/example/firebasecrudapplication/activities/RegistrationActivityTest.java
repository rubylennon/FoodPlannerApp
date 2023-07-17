package com.example.firebasecrudapplication.activities;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RegistrationActivityTest {

    @Test
    void onCreate() {

    }

    @Test
    void isValidPassword() {
        assertTrue(RegistrationActivity.isValidPassword("Password1#"));
        assertFalse(RegistrationActivity.isValidPassword("Password"));
        assertFalse(RegistrationActivity.isValidPassword("Pass"));
        assertFalse(RegistrationActivity.isValidPassword("Password1"));
        assertFalse(RegistrationActivity.isValidPassword("password1#"));
    }
}