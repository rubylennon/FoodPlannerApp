package com.example.foodplannerapp.activities;

import static org.junit.jupiter.api.Assertions.assertThrows;

import android.os.Bundle;

import org.junit.jupiter.api.Test;

class EditAccountActivityTest {

    @Test
    public void onCreate() {
        Bundle b = new Bundle();
        assertThrows(NullPointerException.class, () -> {
            EditAccountActivity editAccountActivity = new EditAccountActivity();
            editAccountActivity.onCreate(b);
        });
    }

}