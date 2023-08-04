package com.example.foodplannerapp.adapters;

/*
 * @Author: Ruby Lennon (x19128355)
 * 5th June 2023
 * IngredientScannerRVAdapter.java
 * Description - Ingredient RecyclerView Adapter class
 */

// imports
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplannerapp.R;
import com.example.foodplannerapp.models.Ingredient;

import java.util.ArrayList;

// @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
// Reference description - tutorial on how to create a Register Activity for Firebase Authenticate
public class IngredientScannerRVAdapter extends RecyclerView.Adapter<IngredientScannerRVAdapter.MyViewHolder> {
    // declare variables
    final ArrayList<Ingredient> list;

    // create constructor
    public IngredientScannerRVAdapter(ArrayList<Ingredient> list){
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // inflate specified layout
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ingredient_card_holder,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        // set data to recycler view
        myViewHolder.name.setText(list.get(i).getIngredientName());
        myViewHolder.description.setText(list.get(i).getIngredientDescription());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        // create variables image and text views
        final TextView name;
        final TextView description;
        // initialize variables
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.SvIngredientNameTwo);
            description = itemView.findViewById(R.id.SvIngredientDescriptionTwo);
        }
    }
}
