package com.example.firebasecrudapplication.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

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