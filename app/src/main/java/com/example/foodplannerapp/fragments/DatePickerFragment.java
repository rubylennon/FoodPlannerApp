package com.example.foodplannerapp.fragments;

/*
 * @Author: Ruby Lennon (x19128355)
 * 1st July 2023
 * DatePickerFragment.java
 * Description - Dialog Fragment for Meal Plan Date selection on the Recipe Details page
 */

// imports
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

// @Reference - https://www.youtube.com/watch?v=33BFCdL0Di0
// Reference description - tutorial on how to create a date picker dialog fragment
// dialog fragment class for meal plan date selection on recipe view page
public class DatePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // return the selected date to the view recipe class
        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
    }
}
