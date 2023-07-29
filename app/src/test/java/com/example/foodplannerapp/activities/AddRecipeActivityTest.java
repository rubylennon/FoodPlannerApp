package com.example.foodplannerapp.activities;

import static org.junit.jupiter.api.Assertions.assertThrows;

import android.os.Bundle;

import org.junit.jupiter.api.Test;

public class AddRecipeActivityTest {

    @Test
    public void onCreate() {
        Bundle b = new Bundle();
        assertThrows(NullPointerException.class, () -> {
            AddRecipeActivity addRecipeActivity = new AddRecipeActivity();
            addRecipeActivity.onCreate(b);
        });
    }

}