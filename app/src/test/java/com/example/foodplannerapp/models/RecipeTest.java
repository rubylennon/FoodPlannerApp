package com.example.foodplannerapp.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RecipeTest {
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
    void getRecipeName() {
        assertEquals("Test Name", r.getRecipeName());
    }

    @Test
    void getRecipeCookingTime() {
        assertEquals("45", r.getRecipeCookingTime());
    }

    @Test
    void getRecipePrepTime() {
        assertEquals("15", r.getRecipePrepTime());
    }

    @Test
    void getRecipeServings() {
        assertEquals("1", r.getRecipeServings());
    }

    @Test
    void getRecipeSuitedFor() {
        assertEquals("Everyone", r.getRecipeSuitedFor());
    }

    @Test
    void getRecipeCuisine() {
        assertEquals("American", r.getRecipeCuisine());
    }

    @Test
    void getRecipeImg() {
        assertEquals("https://img.example.com/123", r.getRecipeImg());
    }

    @Test
    void getRecipeLink() {
        assertEquals("https://example.com", r.getRecipeLink());
    }

    @Test
    void getRecipeDescription() {
        assertEquals("Test Description", r.getRecipeDescription());
    }

    @Test
    void getRecipeMethod() {
        assertEquals("Step 1, Step 2, Step 3.", r.getRecipeMethod());
    }

    @Test
    void getRecipeIngredients() {
        assertEquals("Ingredient 1, Ingredient 2, Ingredient 3", r.getRecipeIngredients());
    }

    @Test
    void getRecipePublic() {
        assertEquals(false, r.getRecipePublic());
    }

    @Test
    void getUserID() {
        assertEquals("Bz7rj0xdxJSRwgcoaaFjh8uWRo83", r.getUserID());
    }

    @Test
    void getRecipeID() {
        assertEquals("-N_QAXvVJBdKqhQWjD2T", r.getRecipeID());
    }

    @Test
    void describeContents() {
        assertEquals(r.describeContents(), 0);
    }

    @Test
    void writeToParcel() {
//        Recipe r2 = new Recipe(recipeName, recipeCookingTime,
//                recipePrepTime, recipeServings, recipeSuitedFor,
//                recipeCuisine, recipeImg, recipeLink, recipeDescription,
//                recipeMethod, recipeIngredients, recipePublic,
//                recipeID, userID);
//
//        Recipe recipe = new Recipe();
//
//        // Obtain a Parcel object and write the parcelable object to it:
//        Parcel parcel = Parcel.obtain();
//        r2.writeToParcel(parcel, 0);
//
//        //>>>>> Record dataPosition
//        int eop = parcel.dataPosition();
//
//        // After you're done with writing, you need to reset the parcel for reading:
//        parcel.setDataPosition(0);
//
//        // Reconstruct object from parcel and asserts:
//        Recipe createdFromParcel = Recipe.CREATOR.createFromParcel(parcel);
//        assertEquals(r2, createdFromParcel);
//
//        //>>>>> Verify dataPosition
//        assertEquals(eop, parcel.dataPosition());
    }
}