package com.example.firebasecrudapplication;

public class Ingredient {
    private String ingredientName,
            ingredientDescription;

    public Ingredient() {
    }

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
