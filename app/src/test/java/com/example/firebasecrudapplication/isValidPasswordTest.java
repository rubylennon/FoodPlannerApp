package com.example.firebasecrudapplication;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.firebasecrudapplication.activities.EditAccountActivity;
import com.example.firebasecrudapplication.activities.RegistrationActivity;

import org.junit.jupiter.api.Test;

public class isValidPasswordTest {
    @Test
    void isValidPasswordEditAccount() {
        assertTrue(EditAccountActivity.isValidPassword("Password1#"));
        assertFalse(EditAccountActivity.isValidPassword("Password"));
        assertFalse(EditAccountActivity.isValidPassword("Pass"));
        assertFalse(EditAccountActivity.isValidPassword("Password1"));
        assertFalse(EditAccountActivity.isValidPassword("password1#"));
    }

    @Test
    void isValidPasswordRegistration() {
        assertTrue(RegistrationActivity.isValidPassword("Password1#"));
        assertFalse(RegistrationActivity.isValidPassword("Password"));
        assertFalse(RegistrationActivity.isValidPassword("Pass"));
        assertFalse(RegistrationActivity.isValidPassword("Password1"));
        assertFalse(RegistrationActivity.isValidPassword("password1#"));
    }
}
