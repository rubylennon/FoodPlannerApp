package com.example.firebasecrudapplication;

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

public class MealPlanRVAdapter extends RecyclerView.Adapter<MealPlanRVAdapter.ViewHolder> {
    int lastPos = -1;
    private ArrayList<MealPlanRVModal> mealPlanRVModalArrayList;
    private Context context;
    private MealPlanClickInterface mealPlanClickInterface;

    public MealPlanRVAdapter(ArrayList<MealPlanRVModal> mealPlanRVModalArrayList, Context context, MealPlanClickInterface mealPlanClickInterface) {
        this.mealPlanRVModalArrayList = mealPlanRVModalArrayList;
        this.context = context;
        this.mealPlanClickInterface = mealPlanClickInterface;
    }

    @NonNull
    @Override
    public MealPlanRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meal_plan_rv_item,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealPlanRVAdapter.ViewHolder holder, int position) {
        MealPlanRVModal mealPlanRVModal = mealPlanRVModalArrayList.get(position);
        holder.recipeNameTV.setText("Description: " + mealPlanRVModal.getRecipeName());
        holder.recipeCookingTimeTV.setText("Cooking Time: " + mealPlanRVModal.getRecipeCookingTime());
        Picasso.get().load(mealPlanRVModal.getRecipeImg()).into(holder.recipeTV);
        setAnimation(holder.itemView, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealPlanClickInterface.onMealPlanClick(position);
            }
        });
    }

    private void setAnimation(View itemView, int position){
        if(position > lastPos){
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPos = position;
        }
    }

    @Override
    public int getItemCount() {
        return mealPlanRVModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView recipeNameTV, recipeServesTV,recipeCookingTimeTV;
        private ImageView recipeTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameTV = itemView.findViewById(R.id.idTVRecipeName);
            recipeCookingTimeTV = itemView.findViewById(R.id.idTVCookingTime);
            recipeTV = itemView.findViewById(R.id.idTVRecipe);
        }
    }

    public interface MealPlanClickInterface{
        void onMealPlanClick(int position);
    }
}
