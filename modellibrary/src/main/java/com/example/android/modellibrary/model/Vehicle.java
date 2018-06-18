package com.example.android.modellibrary.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Vehicle implements Parcelable{
    public static final String VEHICLE_KEY = "vehicle_key";

    @SerializedName("id")
    private int _id;

    @SerializedName("registration")
    private String registration;

    @SerializedName("vehicle_make")
    private String vehicle_make;

    @SerializedName("vehicle_model")
    private String vehicle_model;

    @SerializedName("seating_capacity")
    private String seating_capacity;

    @SerializedName("manufacture_year")
    private String manufacture_year;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("updated_at")
    private String updated_at;

    private double collection = 0.0;
    private double expense = 0.0;

    public Vehicle(String vehicleRegistration, String vehicleMake, String vehicleModel, String yearOfManufacture, String passengerCapacity, String createdAt) {
        this.registration = vehicleRegistration;
        this.vehicle_make = vehicleMake;
        this.vehicle_model = vehicleModel;
        this.manufacture_year = yearOfManufacture;
        this.seating_capacity = passengerCapacity;
        this.created_at = createdAt;
    }

    public Vehicle(int vehicleId, String vehicleReg) {
        this._id = vehicleId;
        this.registration = vehicleReg;
    }

    public Vehicle(int vehicleId, String vehicleReg, double collection, double expense, String updatedDate) {
        this._id = vehicleId;
        this.registration = vehicleReg;
        this.collection = collection;
        this.expense = expense;
        this.updated_at = updatedDate;
    }

    public Vehicle(int vehicleId, double collection, double expense, String updatedDate) {
        this._id = vehicleId;
        this.collection = collection;
        this.expense = expense;
        this.updated_at = updatedDate;
    }

    protected Vehicle(Parcel in) {
        _id = in.readInt();
        registration = in.readString();
        vehicle_make = in.readString();
        vehicle_model = in.readString();
        seating_capacity = in.readString();
        manufacture_year = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
        collection = in.readDouble();
        expense = in.readDouble();
    }

    public static final Creator<Vehicle> CREATOR = new Creator<Vehicle>() {
        @Override
        public Vehicle createFromParcel(Parcel in) {
            return new Vehicle(in);
        }

        @Override
        public Vehicle[] newArray(int size) {
            return new Vehicle[size];
        }
    };

    public int get_id() {
        return _id;
    }

    public String getRegistration() {
        return registration;
    }

    public String getVehicle_make() {
        return vehicle_make;
    }

    public String getVehicle_model() {
        return vehicle_model;
    }

    public String getSeating_capacity() {
        return seating_capacity;
    }

    public String getManufacture_year() {
        return manufacture_year;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public double getCollection() {
        return collection;
    }

    public double getExpense() {
        return expense;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(registration);
        dest.writeString(vehicle_make);
        dest.writeString(vehicle_model);
        dest.writeString(seating_capacity);
        dest.writeString(manufacture_year);
        dest.writeString(created_at);
        dest.writeString(updated_at);
        dest.writeDouble(collection);
        dest.writeDouble(expense);
    }
}

