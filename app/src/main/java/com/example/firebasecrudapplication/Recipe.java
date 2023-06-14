package com.example.firebasecrudapplication;

public class Recipe {

    private String recipeName;
    private String recipeLink;
    private String recipeSuitedFor;
    private String recipeServings;
    private String recipeDescription;
    private String recipeIngredients;
    private String recipeCookingTime;
    private String recipeImg;
    private String recipeMethod;
    private Boolean recipePublic;
    private String userID;
    private String recipeID;

    public Recipe() {
    }

    public Recipe(String recipeName, String recipeLink, String recipeSuitedFor, String recipeServings, String recipeDescription, String recipeIngredients, String recipeCookingTime, String recipeImg, String recipeMethod, Boolean recipePublic, String userID, String recipeID) {
        this.recipeName = recipeName;
        this.recipeLink = recipeLink;
        this.recipeSuitedFor = recipeSuitedFor;
        this.recipeServings = recipeServings;
        this.recipeDescription = recipeDescription;
        this.recipeIngredients = recipeIngredients;
        this.recipeCookingTime = recipeCookingTime;
        this.recipeImg = recipeImg;
        this.recipeMethod = recipeMethod;
        this.recipePublic = recipePublic;
        this.userID = userID;
        this.recipeID = recipeID;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeLink() {
        return recipeLink;
    }

    public void setRecipeLink(String recipeLink) {
        this.recipeLink = recipeLink;
    }

    public String getRecipeSuitedFor() {
        return recipeSuitedFor;
    }

    public void setRecipeSuitedFor(String recipeSuitedFor) {
        this.recipeSuitedFor = recipeSuitedFor;
    }

    public String getRecipeServings() {
        return recipeServings;
    }

    public void setRecipeServings(String recipeServings) {
        this.recipeServings = recipeServings;
    }

    public String getRecipeDescription() {
        return recipeDescription;
    }

    public void setRecipeDescription(String recipeDescription) {
        this.recipeDescription = recipeDescription;
    }

    public String getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(String recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public String getRecipeCookingTime() {
        return recipeCookingTime;
    }

    public void setRecipeCookingTime(String recipeCookingTime) {
        this.recipeCookingTime = recipeCookingTime;
    }

    public String getRecipeImg() {
        return recipeImg;
    }

    public void setRecipeImg(String recipeImg) {
        this.recipeImg = recipeImg;
    }

    public String getRecipeMethod() {
        return recipeMethod;
    }

    public void setRecipeMethod(String recipeMethod) {
        this.recipeMethod = recipeMethod;
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
}
