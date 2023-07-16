package com.example.firebasecrudapplication.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class IngredientTest {
    private final String name = "Sugar";
    private final String description = "Test Description";
    private final Ingredient i = new Ingredient(name, description);

    @Test
    void getIngredientName() {
        assertEquals("Sugar", i.getIngredientName());
    }

    @Test
    void getIngredientDescription() {
        assertEquals("Test Description", i.getIngredientDescription());
    }
}