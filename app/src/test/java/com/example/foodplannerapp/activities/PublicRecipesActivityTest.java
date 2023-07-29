package com.example.foodplannerapp.activities;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.foodplannerapp.models.Recipe;

import org.junit.jupiter.api.Test;

class PublicRecipesActivityTest {

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
    private final Boolean recipePublic = false;

    @SuppressWarnings("ConstantConditions")
    private final Recipe r = new Recipe(recipeName, recipeCookingTime,
            recipePrepTime, recipeServings, recipeSuitedFor,
            recipeCuisine, recipeImg, recipeLink, recipeDescription,
            recipeMethod, recipeIngredients, recipePublic,
            recipeID, userID);

    @Test
    void onCreate() {
    }

    @Test
    void onRecipeClick() {
//        ArrayList<Recipe> list = new ArrayList<>();
//        list.add(r);
//
//        PublicRecipesActivity publicRecipesActivity = new PublicRecipesActivity();
//        PublicRecipesActivity spy = spy(publicRecipesActivity);
//
//        spy.onRecipeClick(1);
//        verify(spy).displayBottomSheet(r);
    }

}