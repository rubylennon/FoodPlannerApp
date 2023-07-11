package com.example.firebasecrudapplication;

//@Ref 1: https://www.geeksforgeeks.org/java-program-to-sort-objects-in-arraylist-by-date/

import java.util.Comparator;

class sortMeals implements Comparator<MealPlanRVModal> {
    // comparison method
    public int compare(MealPlanRVModal mealPlanA, MealPlanRVModal mealPlanB) {
        // Returning the value after comparing the objects
        // this will sort the data in Ascending order
        return mealPlanA.getDateShort().compareTo(mealPlanB.getDateShort());
    }
}
