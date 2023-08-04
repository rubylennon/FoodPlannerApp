package com.example.foodplannerapp.adapters;

/*
 * @Author: Ruby Lennon (x19128355)
 * 1st July 2023
 * MealPlanRVAdapter.java
 * Description - Ingredient RecyclerView Adapter class
 */

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

// @Reference - https://www.geeksforgeeks.org/user-authentication-and-crud-operation-with-firebase-realtime-database-in-android/
// Reference description - tutorial on how to create a Recycler View Adapter
public class MealPlanRVAdapter extends RecyclerView.Adapter<MealPlanRVAdapter.ViewHolder> {
    // declare variables
    int lastPos = -1;
    private final ArrayList<Meal> mealArrayList;
    private final Context context;
    private final MealPlanClickInterface mealPlanClickInterface;

    // create constructor
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
        // inflate specified layout
        View view = LayoutInflater.from(context)
                .inflate(R.layout.meal_rv_item,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealPlanRVAdapter.ViewHolder holder, int position) {
        // set data to recycler view
        Meal meal = mealArrayList.get(position);
        holder.recipeNameTV.setText(meal.getRecipeName());
        holder.mealPlanDate.setText(meal.getDateLong());
        Picasso.get().load(meal.getRecipeImg()).into(holder.recipeTV);
        // add animation to recycler view item
        setAnimation(holder.itemView, position);
        holder.itemView.setOnClickListener(v -> mealPlanClickInterface.onMealPlanClick(position));
    }

    // set animation to RV item
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
        // create variables image and text views
        private final TextView recipeNameTV,
                mealPlanDate;
        private final ImageView recipeTV;
        // initialize variables
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameTV = itemView.findViewById(R.id.idTVRecipeName);
            mealPlanDate = itemView.findViewById(R.id.idTVDate);
            recipeTV = itemView.findViewById(R.id.idTVRecipe);
        }
    }

    // create click interface
    public interface MealPlanClickInterface{
        void onMealPlanClick(int position);
    }
}
