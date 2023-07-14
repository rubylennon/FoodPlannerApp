package com.example.firebasecrudapplication.models;

/*
 * @Author: Ruby Lennon (x19128355)
 * 27th February 2023
 * Recipe.java
 * Description - Recipe Model class
 */

// @REF 1 - GeeksForGeeks Tutorial - https://www.youtube.com/watch?v=-Gvpf8tXpbc

// imports
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Recipe implements Parcelable {
    // variables
    private String recipeName,
            recipeCookingTime,
            recipeServings,
            recipeSuitedFor,
            recipeCuisine,
            recipeImg,
            recipeLink,
            recipeDescription,
            recipeMethod,
            recipeIngredients,
            recipeID,
            userID;
    private Boolean recipePublic;

    @SuppressWarnings("unused")
    public Recipe(){

    }

    public Recipe(String recipeName, String recipeCookingTime, String recipeServings,
                  String recipeSuitedFor, String recipeCuisine, String recipeImg,
                  String recipeLink, String recipeDescription, String recipeMethod,
                  String recipeIngredients, Boolean recipePublic, String recipeID,
                  String userID) {
        this.recipeName = recipeName;
        this.recipeCookingTime = recipeCookingTime;
        this.recipeServings = recipeServings;
        this.recipeSuitedFor = recipeSuitedFor;
        this.recipeCuisine = recipeCuisine;
        this.recipeImg = recipeImg;
        this.recipeLink = recipeLink;
        this.recipeDescription = recipeDescription;
        this.recipeMethod = recipeMethod;
        this.recipeIngredients = recipeIngredients;
        this.recipePublic = recipePublic;
        this.userID = userID;
        this.recipeID = recipeID;
    }

    protected Recipe(Parcel in) {
        recipeName = in.readString();
        recipeCookingTime = in.readString();
        recipeServings = in.readString();
        recipeSuitedFor = in.readString();
        recipeCuisine = in.readString();
        recipeImg = in.readString();
        recipeLink = in.readString();
        recipeDescription = in.readString();
        recipeMethod = in.readString();
        recipeIngredients = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            recipePublic = in.readBoolean();
        }
        userID = in.readString();
        recipeID = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getRecipeName() {
        return recipeName;
    }

    public String getRecipeCookingTime() {
        return recipeCookingTime;
    }

    public String getRecipeServings() {
        return recipeServings;
    }

    public String getRecipeSuitedFor() {
        return recipeSuitedFor;
    }

    public String getRecipeCuisine() {
        return recipeCuisine;
    }

    public String getRecipeImg() {
        return recipeImg;
    }

    public String getRecipeLink() {
        return recipeLink;
    }

    public String getRecipeDescription() {
        return recipeDescription;
    }

    public String getRecipeMethod() {
        return recipeMethod;
    }

    public String getRecipeIngredients() {
        return recipeIngredients;
    }

    public Boolean getRecipePublic() {
        return recipePublic;
    }

    @SuppressWarnings("unused")
    public String getUserID() {
        return userID;
    }

    public String getRecipeID() {
        return recipeID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(recipeName);
        dest.writeString(recipeCookingTime);
        dest.writeString(recipeServings);
        dest.writeString(recipeSuitedFor);
        dest.writeString(recipeCuisine);
        dest.writeString(recipeImg);
        dest.writeString(recipeLink);
        dest.writeString(recipeDescription);
        dest.writeString(recipeMethod);
        dest.writeString(recipeIngredients);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(recipePublic);
        }
        dest.writeString(userID);
        dest.writeString(recipeID);
    }
}
