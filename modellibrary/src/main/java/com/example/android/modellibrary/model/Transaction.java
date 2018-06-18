package com.example.android.modellibrary.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HARUN on 12/24/2016.
 */

public class Transaction implements Parcelable{
    public static final String TRANSACTION_KEY = "transaction_key";

    @SerializedName("id")
    private String id;

    @SerializedName("company_id")
    private String company_id;

    @SerializedName("vehicle_id")
    private int vehicle_id;

    @SerializedName("amount")
    private double amount;

    @SerializedName("type")
    private String type;

    @SerializedName("description")
    private String description;

    @SerializedName("timestamp")
    private long timestamp;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("updated_at")
    private String updated_at;

    private String sync;

    public String getId() {
        return id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public int getVehicle_id() {
        return vehicle_id;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getSync() {
        return sync;
    }

    public Transaction(int vehicle_id, double amount, String type, String description, long timestamp, String created_at, String sync) {
        this.vehicle_id = vehicle_id;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.created_at = created_at;
        this.timestamp = timestamp;
        this.sync = sync;
    }

    public Transaction(String id, int vehicle_id, double amount, String type, String description, long timestamp, String created_at, String updated_at, String sync) {
        this.id = id;
        this.vehicle_id = vehicle_id;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.timestamp = timestamp;
        this.sync = sync;
    }

    public Transaction(double amount, String type, String description , String updatedDate) {
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.updated_at = updatedDate;
    }

    protected Transaction(Parcel in) {
        id = in.readString();
        company_id = in.readString();
        vehicle_id = in.readInt();
        amount = in.readDouble();
        type = in.readString();
        description = in.readString();
        timestamp = in.readLong();
        created_at = in.readString();
        updated_at = in.readString();
        sync = in.readString();
    }


    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(company_id);
        dest.writeInt(vehicle_id);
        dest.writeDouble(amount);
        dest.writeString(type);
        dest.writeString(description);
        dest.writeLong(timestamp);
        dest.writeString(created_at);
        dest.writeString(updated_at);
        dest.writeString(sync);
    }
}
