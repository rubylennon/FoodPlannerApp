package com.example.firebasecrudapplication;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class RecipeRVModal implements Parcelable {
    // variables
    private String recipeName;
    private String recipeCookingTime;
    private String recipeSuitedFor;
    private String recipeImg;
    private String recipeLink;
    private String recipeDescription;
    private String recipeID;

    public RecipeRVModal(){

    }

    public RecipeRVModal(String recipeName, String recipeCookingTime, String recipeSuitedFor, String recipeImg, String recipeLink, String recipeDescription, String recipeID) {
        this.recipeName = recipeName;
        this.recipeCookingTime = recipeCookingTime;
        this.recipeSuitedFor = recipeSuitedFor;
        this.recipeImg = recipeImg;
        this.recipeLink = recipeLink;
        this.recipeDescription = recipeDescription;
        this.recipeID = recipeID;
    }

    protected RecipeRVModal(Parcel in) {
        recipeName = in.readString();
        recipeCookingTime = in.readString();
        recipeSuitedFor = in.readString();
        recipeImg = in.readString();
        recipeLink = in.readString();
        recipeDescription = in.readString();
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

    public String getRecipeSuitedFor() {
        return recipeSuitedFor;
    }

    public void setRecipeSuitedFor(String recipeSuitedFor) {
        this.recipeSuitedFor = recipeSuitedFor;
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
        dest.writeString(recipeSuitedFor);
        dest.writeString(recipeImg);
        dest.writeString(recipeLink);
        dest.writeString(recipeDescription);
        dest.writeString(recipeID);
    }
}
