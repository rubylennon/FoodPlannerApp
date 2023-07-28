package com.example.firebasecrudapplication.activities;

import static org.junit.jupiter.api.Assertions.assertThrows;

import android.content.Intent;
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