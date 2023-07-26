package com.example.firebasecrudapplication.fragments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import android.os.Bundle;

import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

class DatePickerFragmentTest {

    @Test
    void onCreateDialog() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(2023, 11, 24);

        assertEquals(DateFormat.getDateInstance(DateFormat.SHORT)
                .format(calendar.getTime()), "24/12/2023");

        DatePickerFragment datePickerFragment = new DatePickerFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(String.valueOf(Calendar.SHORT), new Date());

        assertNotNull(datePickerFragment.onCreateDialog(bundle));
    }
}