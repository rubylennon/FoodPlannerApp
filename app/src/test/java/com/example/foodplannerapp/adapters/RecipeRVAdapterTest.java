package com.example.foodplannerapp.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.content.Context;
import android.view.View;

import com.example.foodplannerapp.models.Recipe;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class RecipeRVAdapterTest {
    private View mockView;
    private final ArrayList<Recipe> list = new ArrayList<>();
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

    private final Recipe r1 = new Recipe(recipeName, recipeCookingTime,
            recipePrepTime, recipeServings, recipeSuitedFor,
            recipeCuisine, recipeImg, recipeLink, recipeDescription,
            recipeMethod, recipeIngredients, false,
            recipeID, userID);

    private final Recipe r2 = new Recipe(recipeName, recipeCookingTime,
            recipePrepTime, recipeServings, recipeSuitedFor,
            recipeCuisine, recipeImg, recipeLink, recipeDescription,
            recipeMethod, recipeIngredients, false,
            recipeID, userID);

    private final Recipe r3 = new Recipe(recipeName, recipeCookingTime,
            recipePrepTime, recipeServings, recipeSuitedFor,
            recipeCuisine, recipeImg, recipeLink, recipeDescription,
            recipeMethod, recipeIngredients, false,
            recipeID, userID);

    @Test
    void onCreateViewHolder() {
    }

    @Test
    void onBindViewHolder() {
    }

    @Test
    void getItemCount() {
        list.add(r1);
        list.add(r2);
        list.add(r3);

        assertEquals(3, list.size());

        Context context = null;

        assertEquals(3, new RecipeRVAdapter(list, context, new RecipeRVAdapter.RecipeClickInterface() {
            @Override
            public void onRecipeClick(int position) {

            }
        }).getItemCount());
    }

    @Test
    void getActivity() {
        Context context = null;
        assertNull(new RecipeRVAdapter(list, context, new RecipeRVAdapter.RecipeClickInterface() {
            @Override
            public void onRecipeClick(int position) {

            }
        }).getActivity(context));
    }

    @Test
    void viewHolderNullTest() {
        View itemView = null;
        assertThrows(IllegalArgumentException.class, () -> {
            new RecipeRVAdapter.ViewHolder(itemView);
        });
    }
}