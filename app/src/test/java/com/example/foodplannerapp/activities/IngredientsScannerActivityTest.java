package com.example.foodplannerapp.activities;

import static org.junit.jupiter.api.Assertions.assertThrows;

import android.os.Bundle;

import org.junit.jupiter.api.Test;

class IngredientsScannerActivityTest {

    @Test
    public void onCreate() {
        Bundle b = new Bundle();
        assertThrows(NullPointerException.class, () -> {
            IngredientsScannerActivity ingredientsScannerActivity = new IngredientsScannerActivity();
            ingredientsScannerActivity.onCreate(b);
        });
    }

    @Test
    void onStart() {
        assertThrows(NullPointerException.class, () -> {
            IngredientsScannerActivity ingredientsScannerActivity = new IngredientsScannerActivity();
            ingredientsScannerActivity.onStart();
        });
    }

    @Test
    void clearIngredientsList() {
    }

}