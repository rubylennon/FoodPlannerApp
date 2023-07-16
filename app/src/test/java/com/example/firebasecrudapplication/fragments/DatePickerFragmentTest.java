package com.example.firebasecrudapplication.fragments;

import static org.junit.jupiter.api.Assertions.*;

import android.app.DatePickerDialog;

import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.util.Calendar;

class DatePickerFragmentTest {

    @Test
    void onCreateDialog() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(2023, 11, 24);

        assertEquals(DateFormat.getDateInstance(DateFormat.SHORT)
                .format(calendar.getTime()), "24/12/2023");
    }
}