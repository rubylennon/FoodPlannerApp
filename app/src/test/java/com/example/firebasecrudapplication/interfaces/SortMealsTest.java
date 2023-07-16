package com.example.firebasecrudapplication.interfaces;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.firebasecrudapplication.models.Meal;

import org.junit.jupiter.api.Test;

import java.util.Comparator;

class SortMealsTest{
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

        String dateShort3 = "01/04/2023";

        Meal m3 = new Meal(dateShort3, mealPlanID, recipeName, recipeCookingTime,
                recipePrepTime, recipeServings, recipeSuitedFor,
                recipeCuisine, recipeImg, recipeLink, recipeDescription,
                recipeMethod, recipeIngredients, false,
                recipeID, userID, dateLong);

        class SortMeals implements Comparator<Meal> {
                // comparison method
                public int compare(Meal mealA, Meal mealB) {
                        // Returning the value after comparing the objects
                        // this will sort the data in Ascending order
                        return mealA.getDateShort().compareTo(mealB.getDateShort());
                }
        }

        private final SortMeals sortMeals = new SortMeals();

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

                int result = sortMeals.compare(m1, m2);
                assertEquals(0, result, "expected to be equal");
        }

}