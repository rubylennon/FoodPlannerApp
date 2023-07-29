package com.example.foodplannerapp.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MealTest {
    private final String recipeName = "Test Name";
    private final String recipeCookingTime = "45";
    private final String recipePrepTime = "15";
    private final String recipeServings = "1";
    private final String recipeSuitedFor = "Everyone";
    private final String recipeCuisine = "American";
    private final String recipeImg = "https://img.example.com/123";
    private final String recipeLink = "https://example.com";
    private final String recipeDescription = "Test Description";
    private final String recipeMethod = "Step 1, Step 2, Step 3.";
    private final String recipeIngredients = "Ingredient 1, Ingredient 2, Ingredient 3";
    private final String recipeID = "-N_QAXvVJBdKqhQWjD2T";
    private final String userID = "Bz7rj0xdxJSRwgcoaaFjh8uWRo83";
    private final String dateShort = "01/04/2023";
    private final String mealPlanID = "-NZXV0WSyWjYNZBXydi9";
    private final String dateLong = "Saturday, 1 April 2023";
    private final Boolean recipePublic = false;

    @SuppressWarnings("ConstantConditions")
    private final Meal m = new Meal(dateShort, mealPlanID, recipeName, recipeCookingTime,
            recipePrepTime, recipeServings, recipeSuitedFor,
            recipeCuisine, recipeImg, recipeLink, recipeDescription,
            recipeMethod, recipeIngredients, recipePublic,
            recipeID, userID, dateLong);

    @Test
    void getMealPlanID() {
        assertEquals("-NZXV0WSyWjYNZBXydi9", m.getMealPlanID());
    }

    @Test
    void getRecipeName() {
        assertEquals("Test Name", m.getRecipeName());
    }

    @Test
    void getRecipeCookingTime() {
        assertEquals("45", m.getRecipeCookingTime());
    }

    @Test
    void getRecipePrepTime() {
        assertEquals("15", m.getRecipePrepTime());
    }

    @Test
    void getRecipeServings() {
        assertEquals("1", m.getRecipeServings());
    }

    @Test
    void getRecipeSuitedFor() {
        assertEquals("Everyone", m.getRecipeSuitedFor());
    }

    @Test
    void getRecipeCuisine() {
        assertEquals("American", m.getRecipeCuisine());
    }

    @Test
    void getRecipeImg() {
        assertEquals("https://img.example.com/123", m.getRecipeImg());
    }

    @Test
    void getRecipeLink() {
        assertEquals("https://example.com", m.getRecipeLink());
    }

    @Test
    void getRecipeDescription() {
        assertEquals("Test Description", m.getRecipeDescription());
    }

    @Test
    void getRecipeMethod() {
        assertEquals("Step 1, Step 2, Step 3.", m.getRecipeMethod());
    }

    @Test
    void getRecipeIngredients() {
        assertEquals("Ingredient 1, Ingredient 2, Ingredient 3", m.getRecipeIngredients());
    }

    @Test
    void getRecipePublic() {
        assertEquals(false, m.getRecipePublic());
    }

    @Test
    void getUserID() {
        assertEquals("Bz7rj0xdxJSRwgcoaaFjh8uWRo83", m.getUserID());
    }

    @Test
    void getRecipeID() {
        assertEquals("-N_QAXvVJBdKqhQWjD2T", m.getRecipeID());
    }

    @Test
    void getDateShort() {
        assertEquals("01/04/2023", m.getDateShort());
    }

    @Test
    void getDateLong() {
        assertEquals("Saturday, 1 April 2023", m.getDateLong());
    }

    @Test
    void describeContents() {
        assertEquals(m.describeContents(), 0);
    }

    @Test
    void writeToParcel() {
    }
}