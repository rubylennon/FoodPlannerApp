package com.example.firebasecrudapplication;

//@Ref 1: https://www.geeksforgeeks.org/java-program-to-sort-objects-in-arraylist-by-date/

import java.util.Comparator;

class sortMeals implements Comparator<MealPlanRVModal> {

    // Method of this class
    // @Override
    public int compare(MealPlanRVModal a, MealPlanRVModal b)
    {

        // Returning the value after comparing the objects
        // this will sort the data in Ascending order
        return a.getDateShort().compareTo(b.getDateShort());
    }
}
