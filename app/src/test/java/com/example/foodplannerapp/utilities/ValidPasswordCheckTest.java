package com.example.foodplannerapp.utilities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ValidPasswordCheckTest {

    @Test
    void isPasswordValid() {
        assertTrue(ValidPasswordCheck.isPasswordValid("Password1#"));
        assertFalse(ValidPasswordCheck.isPasswordValid("Password1"));
        assertFalse(ValidPasswordCheck.isPasswordValid("Password#"));
        assertFalse(ValidPasswordCheck.isPasswordValid("Password"));
        assertFalse(ValidPasswordCheck.isPasswordValid("pass"));
        assertFalse(ValidPasswordCheck.isPasswordValid("password1#"));
    }
}