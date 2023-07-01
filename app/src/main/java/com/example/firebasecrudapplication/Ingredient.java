package com.example.firebasecrudapplication;

public class Ingredient {
    private String ingredientName;
    private String ingredientDescription;

    public Ingredient() {
    }

    public Ingredient(String ingredientName, String ingredientDescription) {
        this.ingredientName = ingredientName;
        this.ingredientDescription = ingredientDescription;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getIngredientDescription() {
        return ingredientDescription;
    }

    public void setIngredientDescription(String ingredientDescription) {
        this.ingredientDescription = ingredientDescription;
    }
}
