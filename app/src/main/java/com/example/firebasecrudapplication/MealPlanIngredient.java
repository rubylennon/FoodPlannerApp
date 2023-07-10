package com.example.firebasecrudapplication;

import com.google.firebase.database.Exclude;

public class MealPlanIngredient {
    @Exclude
    public String key,
            ingredient,
            purchased;

    public MealPlanIngredient(String ingredient, String purchased) {
        this.ingredient = ingredient;
        this.purchased = purchased;
    }

    public String getKey() {
        return key;
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

}
