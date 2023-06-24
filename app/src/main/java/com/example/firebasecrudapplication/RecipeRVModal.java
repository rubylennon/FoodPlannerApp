package com.example.firebasecrudapplication;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Switch;

import androidx.annotation.NonNull;

public class RecipeRVModal implements Parcelable {
    // variables
    private String recipeName;
    private String recipeCookingTime;
    private String recipeServings;
    private String recipeSuitedFor;
    private String recipeCuisine;
    private String recipeImg;
    private String recipeLink;
    private String recipeDescription;
    private String recipeMethod;
    private String recipeIngredients;
    private Boolean recipePublic;
    private String recipeID;
    private String userID;

    public RecipeRVModal(){

    }

    public RecipeRVModal(String recipeName, String recipeCookingTime, String recipeServings, String recipeSuitedFor, String recipeCuisine, String recipeImg, String recipeLink, String recipeDescription, String recipeMethod, String recipeIngredients, Boolean recipePublic, String recipeID, String userID) {
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

    protected RecipeRVModal(Parcel in) {
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

    public static final Creator<RecipeRVModal> CREATOR = new Creator<RecipeRVModal>() {
        @Override
        public RecipeRVModal createFromParcel(Parcel in) {
            return new RecipeRVModal(in);
        }

        @Override
        public RecipeRVModal[] newArray(int size) {
            return new RecipeRVModal[size];
        }
    };

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeCookingTime() {
        return recipeCookingTime;
    }

    public void setRecipeCookingTime(String recipeCookingTime) {
        this.recipeCookingTime = recipeCookingTime;
    }

    public String getRecipeServings() {
        return recipeServings;
    }

    public void setRecipeServings(String recipeServings) {
        this.recipeServings = recipeServings;
    }

    public String getRecipeSuitedFor() {
        return recipeSuitedFor;
    }

    public void setRecipeSuitedFor(String recipeSuitedFor) {
        this.recipeSuitedFor = recipeSuitedFor;
    }

    public String getRecipeCuisine() {
        return recipeCuisine;
    }

    public void setRecipeCuisine(String recipeCuisine) {
        this.recipeCuisine = recipeCuisine;
    }

    public String getRecipeImg() {
        return recipeImg;
    }

    public void setRecipeImg(String recipeImg) {
        this.recipeImg = recipeImg;
    }

    public String getRecipeLink() {
        return recipeLink;
    }

    public void setRecipeLink(String recipeLink) {
        this.recipeLink = recipeLink;
    }

    public String getRecipeDescription() {
        return recipeDescription;
    }

    public void setRecipeDescription(String recipeDescription) {
        this.recipeDescription = recipeDescription;
    }

    public String getRecipeMethod() {
        return recipeMethod;
    }

    public void setRecipeMethod(String recipeMethod) {
        this.recipeMethod = recipeMethod;
    }

    public String getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(String recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public Boolean getRecipePublic() {
        return recipePublic;
    }

    public void setRecipePublic(Boolean recipePublic) {
        this.recipePublic = recipePublic;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
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
