package com.example.firebasecrudapplication;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IngredientTest {
    String name = "Sugar";
    String description = "Test Description";
    Ingredient i = new Ingredient(name, description);

    @Test
    void getIngredientName() {
        assertEquals("Sugar", i.getIngredientName());
    }

    @Test
    void setIngredientName() {
        Ingredient i2 = new Ingredient();
        i2.setIngredientName("Sugar");
        i2.setIngredientDescription("Test Description");
        assertEquals("Sugar", i2.getIngredientName());
    }

    @Test
    void getIngredientDescription() {
        assertEquals("Test Description", i.getIngredientDescription());
    }

    @Test
    void setIngredientDescription() {
        Ingredient i3 = new Ingredient();
        i3.setIngredientName("Sugar");
        i3.setIngredientDescription("Test Description");
        assertEquals("Test Description", i3.getIngredientDescription());
    }
}