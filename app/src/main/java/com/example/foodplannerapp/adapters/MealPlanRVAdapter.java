package com.example.foodplannerapp.adapters;

/*
 * @Author: Ruby Lennon (x19128355)
 * 1st July 2023
 * MealPlanRVAdapter.java
 * Description - Ingredient RecyclerView Adapter class
 */

// @REF 1 - https://www.youtube.com/watch?v=-Gvpf8tXpbc

// imports
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplannerapp.models.Meal;
import com.example.foodplannerapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MealPlanRVAdapter extends RecyclerView.Adapter<MealPlanRVAdapter.ViewHolder> {
    int lastPos = -1;
    private final ArrayList<Meal> mealArrayList;
    private final Context context;
    private final MealPlanClickInterface mealPlanClickInterface;

    public MealPlanRVAdapter(ArrayList<Meal> mealArrayList, Context context,
                             MealPlanClickInterface mealPlanClickInterface) {
        this.mealArrayList = mealArrayList;
        this.context = context;
        this.mealPlanClickInterface = mealPlanClickInterface;
    }

    @NonNull
    @Override
    public MealPlanRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                           int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.meal_rv_item,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealPlanRVAdapter.ViewHolder holder, int position) {
        Meal meal = mealArrayList.get(position);
        holder.recipeNameTV.setText(meal.getRecipeName());
        holder.mealPlanDate.setText(meal.getDateLong());
        Picasso.get().load(meal.getRecipeImg()).into(holder.recipeTV);
        setAnimation(holder.itemView, position);
        holder.itemView.setOnClickListener(v -> mealPlanClickInterface.onMealPlanClick(position));
    }

    private void setAnimation(View itemView, int position){
        if(position > lastPos){
            Animation animation = AnimationUtils.loadAnimation(context,
                    android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPos = position;
        }
    }

    @Override
    public int getItemCount() {
        return mealArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView recipeNameTV,
                mealPlanDate;
        private final ImageView recipeTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameTV = itemView.findViewById(R.id.idTVRecipeName);
            mealPlanDate = itemView.findViewById(R.id.idTVDate);
            recipeTV = itemView.findViewById(R.id.idTVRecipe);
        }
    }

    public interface MealPlanClickInterface{
        void onMealPlanClick(int position);
    }
}
