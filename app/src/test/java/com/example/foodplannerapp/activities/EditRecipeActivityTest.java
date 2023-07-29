package com.example.foodplannerapp.activities;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.os.Bundle;

import org.junit.jupiter.api.Test;

class EditRecipeActivityTest {

    @Test
    public void onCreate() {
        Bundle b = new Bundle();
        assertThrows(NullPointerException.class, () -> {
            EditRecipeActivity editRecipeActivity = new EditRecipeActivity();
            editRecipeActivity.onCreate(b);
        });
    }
}