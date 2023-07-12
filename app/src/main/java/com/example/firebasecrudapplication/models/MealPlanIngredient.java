package com.example.firebasecrudapplication.models;

/*
 * @Author: Ruby Lennon (x19128355)
 * 2nd July 2023
 * MealPlanIngredient.java
 * Description - Ingredient model class used by Meal Plan Activities
 */

// imports
import com.google.firebase.database.Exclude;

public class MealPlanIngredient {
    @Exclude
    public String key,
            ingredient,
            purchased;

    @SuppressWarnings("unused")
    public MealPlanIngredient(){
    }

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
