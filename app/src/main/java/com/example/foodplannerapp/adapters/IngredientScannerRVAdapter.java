package com.example.foodplannerapp.adapters;

/*
 * @Author: Ruby Lennon (x19128355)
 * 5th June 2023
 * IngredientScannerRVAdapter.java
 * Description - Ingredient RecyclerView Adapter class
 */

// @REF 1 - https://www.youtube.com/watch?v=-Gvpf8tXpbc

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplannerapp.R;
import com.example.foodplannerapp.models.Ingredient;

import java.util.ArrayList;

public class IngredientScannerRVAdapter extends RecyclerView.Adapter<IngredientScannerRVAdapter.MyViewHolder> {
    final ArrayList<Ingredient> list;
    public IngredientScannerRVAdapter(ArrayList<Ingredient> list){
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ingredient_card_holder,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.name.setText(list.get(i).getIngredientName());
        myViewHolder.description.setText(list.get(i).getIngredientDescription());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView description;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.SvIngredientNameTwo);
            description = itemView.findViewById(R.id.SvIngredientDescriptionTwo);
        }
    }
}
