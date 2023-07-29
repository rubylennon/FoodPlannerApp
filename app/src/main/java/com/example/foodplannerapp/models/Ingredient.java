package com.example.foodplannerapp.models;

/*
 * @Author: Ruby Lennon (x19128355)
 * 30th May 2023
 * Ingredient.java
 * Description - Ingredient model class used by Ingredient Scanner and Recipe Search Activities
 */

public class Ingredient {
    private String ingredientName,
            ingredientDescription;

    @SuppressWarnings("unused")
    public Ingredient() {
    }

    @SuppressWarnings("unused")
    public Ingredient(String ingredientName, String ingredientDescription) {
        this.ingredientName = ingredientName;
        this.ingredientDescription = ingredientDescription;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public String getIngredientDescription() {
        return ingredientDescription;
    }

}
