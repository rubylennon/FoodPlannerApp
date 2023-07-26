package com.example.firebasecrudapplication.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import android.view.View;

import androidx.appcompat.view.menu.MenuView;

import com.example.firebasecrudapplication.models.Ingredient;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class IngredientScannerRVAdapterTest {
    ArrayList<Ingredient> list = new ArrayList<>();
    String name = "Sugar";
    String description = "Test Description";
    Ingredient i = new Ingredient(name, description);
    Ingredient i2 = new Ingredient(name, description);
    Ingredient i3 = new Ingredient(name, description);

    @Test
    void onCreateViewHolder() {
    }

    @Test
    void onBindViewHolder() {
    }

    @Test
    void getItemCount() {
        list.add(i);
        list.add(i2);
        list.add(i3);

        assertEquals(3, list.size());
    }

    @Test
    void testOnCreateViewHolder() {
    }

    @Test
    void testOnBindViewHolder() {
    }

    @Test
    void testGetItemCount() {
        View itemView = null;
        assertThrows(IllegalArgumentException.class, () -> {
            new IngredientScannerRVAdapter.MyViewHolder(itemView);
        });
    }
}