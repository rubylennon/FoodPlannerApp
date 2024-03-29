package com.example.foodplannerapp.interfaces;

/*
 * @Author: Ruby Lennon (x19128355)
 * 4th July 2023
 * SortMeals.java
 * Description - Meal Plan comparator class used to sort Meal Plan Meals by date
 */

// imports
import com.example.foodplannerapp.models.Meal;

import java.util.Comparator;

// @Reference - https://www.geeksforgeeks.org/java-program-to-sort-objects-in-arraylist-by-date/
// Reference description - tutorial on how to sort Objects in ArrayList by Date
public class SortMeals implements Comparator<Meal> {
    // comparison method
    public int compare(Meal mealA, Meal mealB) {
        // return value after comparing the objects to sort meals in Ascending order
        return mealA.getDateShort().compareTo(mealB.getDateShort());
    }
}
