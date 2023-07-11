package com.example.firebasecrudapplication;

import android.annotation.SuppressLint;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeRVAdapter extends RecyclerView.Adapter<RecipeRVAdapter.ViewHolder> {
    int lastPos = -1;
    private final ArrayList<RecipeRVModal> recipeRVModalArrayList;
    private final Context context;
    private final RecipeClickInterface recipeClickInterface;

    public RecipeRVAdapter(ArrayList<RecipeRVModal> recipeRVModalArrayList, Context context,
                           RecipeClickInterface recipeClickInterface) {
        this.recipeRVModalArrayList = recipeRVModalArrayList;
        this.context = context;
        this.recipeClickInterface = recipeClickInterface;
    }

    @NonNull
    @Override
    public RecipeRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_rv_item, parent,
                false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecipeRVAdapter.ViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        RecipeRVModal recipeRVModal = recipeRVModalArrayList.get(position);
        holder.recipeNameTV.setText(recipeRVModal.getRecipeName());
        holder.recipeCookingTimeTV.setText("Cooking Time: " + recipeRVModal.getRecipeCookingTime());
        Picasso.get().load(recipeRVModal.getRecipeImg()).into(holder.recipeTV);
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
        return recipeRVModalArrayList.size();
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
}
