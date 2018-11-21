package org.affordablehousing.chi.chicagoaffordablehousingapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PropertyArrayList {
    @SerializedName("propertyArrayList")
    private ArrayList<Property> propertyArrayList;

    public ArrayList<Property> getPropertyArrayList() {
        return propertyArrayList;
    }

    public void setPropertyArrayList(ArrayList<Property> propertyArrayList) {
        this.propertyArrayList = propertyArrayList;
    }
}
