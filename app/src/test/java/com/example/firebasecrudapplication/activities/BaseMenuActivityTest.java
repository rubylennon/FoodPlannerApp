package com.example.firebasecrudapplication.activities;

import static org.junit.jupiter.api.Assertions.*;

import android.view.Menu;

import org.checkerframework.checker.nullness.qual.AssertNonNullIfNonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class BaseMenuActivityTest {

    @Test
    void onCreateOptionsMenu() {
        assertThrows(NullPointerException.class, () -> {
            BaseMenuActivity baseMenuActivity = new BaseMenuActivity();
            baseMenuActivity.onCreateOptionsMenu(null);
        });
    }

    @Test
    void onOptionsItemSelected() {
        assertThrows(NullPointerException.class, () -> {
            BaseMenuActivity baseMenuActivity = new BaseMenuActivity();
            baseMenuActivity.onOptionsItemSelected(null);
        });
    }
}