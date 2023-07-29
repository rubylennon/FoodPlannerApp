package com.example.foodplannerapp.activities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BaseMenuActivityTest {

    @Test
    void onCreateOptionsMenu() {
        assertThrows(NullPointerException.class, () -> {
            new BaseMenuActivity().onCreateOptionsMenu(null);
        });
    }

    @Test
    void onOptionsItemSelected() {
        assertThrows(NullPointerException.class, () -> {
            BaseMenuActivity baseMenuActivity = new BaseMenuActivity();
            baseMenuActivity.onOptionsItemSelected(null);
        });
    }
}