package com.example.foodplannerapp.adapters;

/*
 * @Author: Ruby Lennon (x19128355)
 * 27th February 2023
 * RecipeRVAdapter.java
 * Description - Recipe RecyclerView Adapter class
 */

// @REF 1 - GeeksForGeeks Tutorial - https://www.youtube.com/watch?v=-Gvpf8tXpbc
// @REF 2 - https://stackoverflow.com/questions/9891360/getting-activity-from-context-in-android

// imports
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplannerapp.R;
import com.example.foodplannerapp.models.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeRVAdapter extends RecyclerView.Adapter<RecipeRVAdapter.ViewHolder> {
    int lastPos = -1;
    private final ArrayList<Recipe> recipeArrayList;
    private final Context context;
    private final RecipeClickInterface recipeClickInterface;

    public RecipeRVAdapter(ArrayList<Recipe> recipeArrayList, Context context,
                           RecipeClickInterface recipeClickInterface) {
        this.recipeArrayList = recipeArrayList;
        this.context = context;
        this.recipeClickInterface = recipeClickInterface;
    }

    @NonNull
    @Override
    public RecipeRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        String currentActivity = String.valueOf(getActivity(context));
        View view;

        if(currentActivity.contains("MainActivity")){
            view = LayoutInflater.from(context).inflate(R.layout.recipe_user_rv_item, parent,
                    false);
        } else if(currentActivity.contains("PublicRecipesActivity")){
            view = LayoutInflater.from(context).inflate(R.layout.recipe_public_rv_item, parent,
                    false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.recipe_search_rv_item, parent,
                    false);
        }
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecipeRVAdapter.ViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        Recipe recipe = recipeArrayList.get(position);
        holder.recipeNameTV.setText(recipe.getRecipeName());
        holder.recipeCookingTimeTV.setText("Cooking Time: " + recipe.getRecipeCookingTime() + "m");
        Picasso.get().load(recipe.getRecipeImg()).into(holder.recipeTV);
        setAnimation(holder.itemView, position);

        holder.itemView.setOnClickListener(v -> recipeClickInterface.onRecipeClick(position));
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
        return recipeArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView recipeNameTV,
                recipeCookingTimeTV;
        private final ImageView recipeTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameTV = itemView.findViewById(R.id.idTVRecipeName);
            recipeCookingTimeTV = itemView.findViewById(R.id.idTVCookingTime);
            recipeTV = itemView.findViewById(R.id.idTVRecipe);
        }
    }

    public interface RecipeClickInterface{
        void onRecipeClick(int position);
    }

    public Activity getActivity(Context context) {
        if (context == null) {
            return null;
        } else if (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            else {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }

        return null;
    }
}
