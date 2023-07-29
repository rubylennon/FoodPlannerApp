package com.example.foodplannerapp.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MealIngredientTest {
    private final String ingredient = "Test Ingredient";
    private final String purchased = "false";
    private final MealIngredient mi = new MealIngredient(ingredient, purchased);

    @Test
    void getKey() {
        mi.key = "-NZXV0WUNm2eHOVSmFIt";
        assertEquals("-NZXV0WUNm2eHOVSmFIt", mi.getKey());
    }

    @Test
    void getIngredient() {
        assertEquals("Test Ingredient", mi.getIngredient());
    }

    @Test
    void getPurchased() {
        assertEquals("false", mi.getPurchased());
    }
}