package com.example.firebasecrudapplication.activities;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.os.Bundle;

import androidx.appcompat.widget.ThemedSpinnerAdapter;

import com.example.firebasecrudapplication.adapters.RecipeRVAdapter;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
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