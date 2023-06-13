package com.example.firebasecrudapplication;

public class Recipe {

    private String recipeName;
    private String recipeCookingTime;
    private String recipeServings;
    private String recipeSuitedFor;
    private String recipeImg;
    private String recipeLink;
    private String recipeDescription;
    private String recipeMethod;
    private String recipeIngredients;
    private Boolean recipePublic;
    private String recipeID;
    private String userID;

    public Recipe() {
    }

    public Recipe(String recipeName, String recipeCookingTime, String recipeServings, String recipeSuitedFor, String recipeImg, String recipeLink, String recipeDescription, String recipeMethod, String recipeIngredients, Boolean recipePublic, String recipeID, String userID) {
        this.recipeName = recipeName;
        this.recipeCookingTime = recipeCookingTime;
        this.recipeServings = recipeServings;
        this.recipeSuitedFor = recipeSuitedFor;
        this.recipeImg = recipeImg;
        this.recipeLink = recipeLink;
        this.recipeDescription = recipeDescription;
        this.recipeMethod = recipeMethod;
        this.recipeIngredients = recipeIngredients;
        this.recipePublic = recipePublic;
        this.recipeID = recipeID;
        this.userID = userID;
    }

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

    public String getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
