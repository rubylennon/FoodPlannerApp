package com.example.foodplannerapp.models;

/*
 * @Author: Ruby Lennon (x19128355)
 * 1st July 2023
 * Meal.java
 * Description - Meal Plan model class used by Meal Planner Activities
 */

// imports
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Meal implements Parcelable {
    // variables
    private String recipeName,
            recipeCookingTime,
            recipePrepTime,
            recipeServings,
            recipeSuitedFor,
            recipeCuisine,
            recipeImg,
            recipeLink,
            recipeDescription,
            recipeMethod,
            recipeIngredients,
            recipeID,
            userID,
            dateShort,
            mealPlanID,
            dateLong;
    private Boolean recipePublic;

    @SuppressWarnings("unused")
    public Meal(){
    }

    // @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
    // Reference description - tutorial on how to add an object to the Firebase Realtime Database
    public Meal(String dateShort, String mealPlanID, String recipeName, String recipeCookingTime,
                String recipePrepTime, String recipeServings, String recipeSuitedFor,
                String recipeCuisine, String recipeImg, String recipeLink, String recipeDescription,
                String recipeMethod, String recipeIngredients, Boolean recipePublic,
                String recipeID, String userID, String dateLong) {
        this.dateShort = dateShort;
        this.mealPlanID = mealPlanID;
        this.recipeName = recipeName;
        this.recipeCookingTime = recipeCookingTime;
        this.recipePrepTime = recipePrepTime;
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
        this.dateLong = dateLong;
    }

    // @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
    // Reference description - tutorial on how to add an object to the Firebase Realtime Database
    protected Meal(Parcel in) {
        dateShort = in.readString();
        mealPlanID = in.readString();
        recipeName = in.readString();
        recipeCookingTime = in.readString();
        recipePrepTime = in.readString();
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
        dateLong = in.readString();
    }

    public static final Creator<Meal> CREATOR = new Creator<Meal>() {
        @Override
        public Meal createFromParcel(Parcel in) {
            return new Meal(in);
        }

        @Override
        public Meal[] newArray(int size) {
            return new Meal[size];
        }
    };

    public String getMealPlanID() {
        return mealPlanID;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getRecipeCookingTime() {
        return recipeCookingTime;
    }

    public String getRecipePrepTime() {
        return recipePrepTime;
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

    @SuppressWarnings("unused")
    public String getRecipeID() {
        return recipeID;
    }

    public String getDateShort() {
        return dateShort;
    }

    public String getDateLong() {
        return dateLong;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
    // Reference description - tutorial on how to add an object to the Firebase Realtime Database
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(dateShort);
        dest.writeString(mealPlanID);
        dest.writeString(recipeName);
        dest.writeString(recipeCookingTime);
        dest.writeString(recipePrepTime);
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
        dest.writeSerializable(dateLong);
    }
}
