package com.example.firebasecrudapplication.activities;

import static org.junit.jupiter.api.Assertions.assertThrows;

import android.content.Intent;

import org.junit.jupiter.api.Test;

class IngredientsScannerActivityTest {

    @Test
    void onCreate() {
    }

    @Test
    void onStart() {
    }

    @Test
    void onActivityResult() {
        Intent data = new Intent();
        assertThrows(NullPointerException.class, () -> {
            IngredientsScannerActivity ingredientsScannerActivity = new IngredientsScannerActivity();
            ingredientsScannerActivity.onActivityResult(1,1,data);
        });
    }

    @Test
    void clearIngredientsList() {
    }

}