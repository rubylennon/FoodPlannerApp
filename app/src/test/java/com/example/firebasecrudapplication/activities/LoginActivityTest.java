package com.example.firebasecrudapplication.activities;

import static org.junit.jupiter.api.Assertions.assertThrows;

import android.os.Bundle;

import org.junit.jupiter.api.Test;

class LoginActivityTest {

    @Test
    void onCreate() {
        Bundle b = new Bundle();
        assertThrows(NullPointerException.class, () -> {
            LoginActivity loginActivity = new LoginActivity();
            loginActivity.onCreate(b);
        });
    }

    @Test
    void onStart() {
    }
}