package com.example.firebasecrudapplication;

public class IngredientTwo {
    private String ingredientName;
    private String ingredientDescription;

    public IngredientTwo() {
    }

    public IngredientTwo(String ingredientName, String ingredientDescription) {
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
