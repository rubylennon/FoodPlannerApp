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

public class IngredientRVAdapter extends RecyclerView.Adapter<IngredientRVAdapter.ViewHolder> {
    int lastPos = -1;
    private ArrayList<IngredientRVModal> ingredientRVModalArrayList;
    private Context context;
    private IngredientClickInterface ingredientClickInterface;

    public IngredientRVAdapter(ArrayList<IngredientRVModal> ingredientRVModalArrayList, Context context, IngredientClickInterface ingredientClickInterface) {
        this.ingredientRVModalArrayList = ingredientRVModalArrayList;
        this.context = context;
        this.ingredientClickInterface = ingredientClickInterface;
    }

    @NonNull
    @Override
    public IngredientRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ingredient_rv_item,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientRVAdapter.ViewHolder holder, int position) {
        IngredientRVModal ingredientRVModal = ingredientRVModalArrayList.get(position);
        holder.ingredientNameTV.setText("Name: " + ingredientRVModal.getIngredientName());
        holder.ingredientDescriptionTV.setText("Description: " + ingredientRVModal.getIngredientDescription());
        setAnimation(holder.itemView, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingredientClickInterface.onIngredientClick(position);
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
        return ingredientRVModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView ingredientNameTV, ingredientDescriptionTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientNameTV = itemView.findViewById(R.id.idTVIngredientName);
            ingredientDescriptionTV = itemView.findViewById(R.id.idTVIngredientDescription);
        }
    }

    public interface IngredientClickInterface{
        void onIngredientClick(int position);
    }
}
