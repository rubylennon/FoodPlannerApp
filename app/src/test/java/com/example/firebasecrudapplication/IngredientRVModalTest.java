package com.example.firebasecrudapplication;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IngredientRVModalTest {
    String name = "Sugar";
    String description = "Test Description";
    IngredientRVModal i = new IngredientRVModal(name, description);

    @Test
    void getIngredientName() {
        assertEquals("Sugar", i.getIngredientName());
    }

    @Test
    void setIngredientName() {
        IngredientRVModal i2 = new IngredientRVModal();
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
        IngredientRVModal i3 = new IngredientRVModal();
        i3.setIngredientName("Sugar");
        i3.setIngredientDescription("Test Description");
        assertEquals("Test Description", i3.getIngredientDescription());
    }
}