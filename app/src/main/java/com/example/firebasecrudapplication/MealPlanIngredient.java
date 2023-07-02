package com.example.firebasecrudapplication;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class MealPlanIngredient implements Parcelable {
    // variables
    private String ingredient;

    public MealPlanIngredient(){

    }

    public MealPlanIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    protected MealPlanIngredient(Parcel in) {
        ingredient = in.readString();
    }

    public static final Creator<MealPlanIngredient> CREATOR = new Creator<MealPlanIngredient>() {
        @Override
        public MealPlanIngredient createFromParcel(Parcel in) {
            return new MealPlanIngredient(in);
        }

        @Override
        public MealPlanIngredient[] newArray(int size) {
            return new MealPlanIngredient[size];
        }
    };

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(ingredient);
    }
}
