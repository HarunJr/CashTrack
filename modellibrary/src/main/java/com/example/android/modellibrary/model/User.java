package com.example.android.modellibrary.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HARUN on 4/18/2018.
 */

public class User implements Parcelable {
    private static final String LOG_TAG = User.class.getSimpleName();
    public static final String USER_KEY = "user_key";
    public static final String TOKEN_KEY = "token_key";

    @SerializedName("name")
    private String name;

    @SerializedName("phoneNo")
    private String phoneNo;

    @SerializedName("email")
    private String email;

    @SerializedName("pin")
    private String pin;

    @SerializedName("company")
    private String company;

    @SerializedName("role")
    private String role;

    private String auth_token;

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public String getCompany() {
        return company;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getPin() {
        return pin;
    }

    public String getRole() {
        return role;
    }

    public User(String name, String email, String phoneNo, String pin, String company, String role) {
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.pin = pin;
        this.company = company;
        this.role = role;
    }

    public User(String name, String email, String phoneNo){
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
    }

    private User(Parcel in) {
        name = in.readString();
        phoneNo = in.readString();
        email = in.readString();
        pin = in.readString();
        company = in.readString();
        role = in.readString();
        auth_token = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phoneNo);
        dest.writeString(email);
        dest.writeString(pin);
        dest.writeString(company);
        dest.writeString(role);
        dest.writeString(auth_token);
    }

//    public User(String phoneNo, String pin, String role){
//        this.phoneNo = phoneNo;
//        this.pin = pin;
//        this.role = role;
//    }
}
