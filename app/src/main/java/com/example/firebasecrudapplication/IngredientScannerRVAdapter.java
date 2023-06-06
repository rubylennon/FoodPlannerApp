package com.example.firebasecrudapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class IngredientScannerRVAdapter extends RecyclerView.Adapter<IngredientScannerRVAdapter.MyViewHolder> {

    ArrayList<Ingredient> list;
    public IngredientScannerRVAdapter(ArrayList<Ingredient> list){
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_holder_test,viewGroup,false);
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

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, description;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.SvIngredientNameTwo);
            description = itemView.findViewById(R.id.SvIngredientDescriptionTwo);
        }
    }
}
