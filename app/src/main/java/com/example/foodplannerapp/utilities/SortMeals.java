package com.example.foodplannerapp.utilities;

/*
 * @Author: Ruby Lennon (x19128355)
 * 4th July 2023
 * SortMeals.java
 * Description - Meal Plan comparator class used to sort Meal Plan Meals by date
 */

import com.example.foodplannerapp.models.Meal;

import java.util.Comparator;

public class SortMeals implements Comparator<Meal> {
    // comparison method
    //Reference - https://www.geeksforgeeks.org/java-program-to-sort-objects-in-arraylist-by-date/
    public int compare(Meal mealA, Meal mealB) {
        // Returning the value after comparing the objects
        // this will sort the data in Ascending order
        return mealA.getDateShort().compareTo(mealB.getDateShort());
    }
}
