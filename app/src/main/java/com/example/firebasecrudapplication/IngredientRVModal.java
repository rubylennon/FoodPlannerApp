package com.example.firebasecrudapplication;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class IngredientRVModal implements Parcelable {
    // variables
    private String ingredientName,
            ingredientDescription;

    public IngredientRVModal(){
    }

    public IngredientRVModal(String ingredientName, String ingredientDescription) {
        this.ingredientName = ingredientName;
        this.ingredientDescription = ingredientDescription;
    }

    protected IngredientRVModal(Parcel in) {
        ingredientName = in.readString();
        ingredientDescription = in.readString();
    }

    public static final Creator<IngredientRVModal> CREATOR = new Creator<IngredientRVModal>() {
        @Override
        public IngredientRVModal createFromParcel(Parcel in) {
            return new IngredientRVModal(in);
        }

        @Override
        public IngredientRVModal[] newArray(int size) {
            return new IngredientRVModal[size];
        }
    };

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getIngredientDescription() {
        return ingredientDescription;
    }

    public void setIngredientDescription(String ingredientDescription) {
        this.ingredientDescription = ingredientDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(ingredientName);
        dest.writeString(ingredientDescription);
    }
}
