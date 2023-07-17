package com.example.firebasecrudapplication.activities;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AddRecipeActivityTest{

    @Test
    public void onCreate() {
    }

    @Test
    public void onCreateOptionsMenu() {
        assertTrue(AddRecipeActivity.onCreateOptionsMenu(null));
    }

    @Test
    public void onOptionsItemSelected() {
    }
}