package com.example.firebasecrudapplication;

public class Ingredient {
    private String ingredientName;
    private String ingredientDescription;

    public Ingredient() {
    }

    public Ingredient(String ingredientName, String ingredientDescription) {
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

//    @Override
//    public int hashCode() {
//        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
//        // if deriving: appendSuper(super.hashCode()).
//        append(ingredientName).
//        append(ingredientDescription).
//        toHashCode();
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (!(obj instanceof Ingredient))
//            return false;
//        if (obj == this)
//            return true;
//
//        Ingredient rhs = (Ingredient) obj;
//        return new EqualsBuilder().
//        // if deriving: appendSuper(super.equals(obj)).
//        append(ingredientName, rhs.ingredientName).
//        append(ingredientDescription, rhs.ingredientDescription).
//        isEquals();
//    }
}
