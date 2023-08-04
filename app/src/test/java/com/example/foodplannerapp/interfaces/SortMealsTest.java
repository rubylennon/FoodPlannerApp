package com.example.foodplannerapp.interfaces;

import static org.junit.jupiter.api.Assertions.*;

import com.example.foodplannerapp.models.Meal;

import org.junit.jupiter.api.Test;

class SortMealsTest {
    String recipeName = "Test Name";
    String recipeCookingTime = "45";
    String recipePrepTime = "15";
    String recipeServings = "1";
    String recipeSuitedFor = "Everyone";
    String recipeCuisine = "American";
    String recipeImg = "https://img.example.com/123";
    String recipeLink = "https://example.com";
    String recipeDescription = "Test Description";
    String recipeMethod = "Step 1, Step 2, Step 3.";
    String recipeIngredients = "Ingredient 1, Ingredient 2, Ingredient 3";
    String recipeID = "-N_QAXvVJBdKqhQWjD2T";
    String userID = "Bz7rj0xdxJSRwgcoaaFjh8uWRo83";
    String mealPlanID = "-NZXV0WSyWjYNZBXydi9";
    String dateLong = "Saturday, 1 April 2023";

    @Test
    public void compare() {
        String dateShort = "01/04/2023";

        Meal m1 = new Meal(dateShort, mealPlanID, recipeName, recipeCookingTime,
                recipePrepTime, recipeServings, recipeSuitedFor,
                recipeCuisine, recipeImg, recipeLink, recipeDescription,
                recipeMethod, recipeIngredients, false,
                recipeID, userID, dateLong);

        String dateShort2 = "01/04/2023";

        Meal m2 = new Meal(dateShort2, mealPlanID, recipeName, recipeCookingTime,
                recipePrepTime, recipeServings, recipeSuitedFor,
                recipeCuisine, recipeImg, recipeLink, recipeDescription,
                recipeMethod, recipeIngredients, false,
                recipeID, userID, dateLong);

        String dateShort3 = "20/04/2023";

        Meal m3 = new Meal(dateShort3, mealPlanID, recipeName, recipeCookingTime,
                recipePrepTime, recipeServings, recipeSuitedFor,
                recipeCuisine, recipeImg, recipeLink, recipeDescription,
                recipeMethod, recipeIngredients, false,
                recipeID, userID, dateLong);

        SortMeals sortMeals = new SortMeals();

        assertEquals(0, sortMeals.compare(m1, m2), "expected to be equal");
        assertEquals(-2, sortMeals.compare(m2, m3), "expected to be greater");
        assertEquals(2, sortMeals.compare(m3, m1), "expected to be less than");

    }

}