package com.example.firebasecrudapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterItemIngredient extends RecyclerView.Adapter<AdapterItemIngredient.ItemViewHolder> {
    AppCompatActivity activity;
    ArrayList<Ingredient> ingredientArrayList;
    Button submit;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    public AdapterItemIngredient(AppCompatActivity activity, ArrayList<Ingredient> ingredientArrayList, Button submit) {
        this.activity = activity;
        this.ingredientArrayList = ingredientArrayList;
        this.submit = submit;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_list_item,parent,false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterItemIngredient.ItemViewHolder holder, int position) {
        Ingredient ingredient = ingredientArrayList.get(position);
        holder.name.setText("Name: " + ingredient.getIngredientName());
    }

    @Override
    public int getItemCount() {
        return ingredientArrayList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private CheckBox checkBox;
        public ItemViewHolder(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            checkBox = itemView.findViewById(R.id.checkbox);
        }

        public void bindView(Ingredient ingredient){
            databaseReference.child("Ingredients").child(ingredient.getIngredientName()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
