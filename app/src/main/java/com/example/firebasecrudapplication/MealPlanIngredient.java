package com.example.firebasecrudapplication;

import com.google.firebase.database.Exclude;

public class MealPlanIngredient {
    @Exclude
    public String key;
    private String ingredient;
    private String purchased;

    public MealPlanIngredient() {
    }

    public MealPlanIngredient(String ingredient, String purchased) {
        this.ingredient = ingredient;
        this.purchased = purchased;
    }

    public String getKey() {
        return key;
    }

    public void setKey() {
        this.key = key;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getPurchased() {
        return purchased;
    }

    public void setPurchased(String purchased) {
        this.purchased = purchased;
    }
}
