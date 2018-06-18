package com.example.android.modellibrary.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ParentModel {
    @SerializedName("status")
    private int status;

    @SerializedName("user")
    private User userInfo;

    @SerializedName("token")
    private String token;

    @SerializedName("vehicle")
    private Vehicle vehicleRe;

    @SerializedName("vehicles")
    private List<Vehicle> vehicles;

    @SerializedName("transaction")
    private Transaction transactionObject;

    @SerializedName("transactions")
    private List<Transaction> transactionList;

    public Transaction getTransactionObject() {
        return transactionObject;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public String getToken() {
        return token;
    }

    public User getUserInfo() {
        return userInfo;
    }

    public Vehicle getVehicleRe() {
        return vehicleRe;
    }

    public int getStatus() {
        return status;
    }

}
