package com.example.firebasecrudapplication.interfaces;

/*
 * @Author: Ruby Lennon (x19128355)
 * 4th July 2023
 * sortMeals.java
 * Description - Meal Plan comparator class used to sort Meal Plan Meals by date
 */

//@Ref 1: https://www.geeksforgeeks.org/java-program-to-sort-objects-in-arraylist-by-date/

import com.example.firebasecrudapplication.models.Meal;

import java.util.Comparator;

public class sortMeals implements Comparator<Meal> {
    // comparison method
    public int compare(Meal mealPlanA, Meal mealPlanB) {
        // Returning the value after comparing the objects
        // this will sort the data in Ascending order
        return mealPlanA.getDateShort().compareTo(mealPlanB.getDateShort());
    }
}
