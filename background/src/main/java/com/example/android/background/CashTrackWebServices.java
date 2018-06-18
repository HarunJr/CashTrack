package com.example.android.background;

import com.example.android.modellibrary.model.ParentModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CashTrackWebServices {

    @FormUrlEncoded
    @POST("authenticate/{parameter}")
    Call<ParentModel> registerUser(@Path("parameter") String requestType,
                                   @Field("name") String name,
                                   @Field("email") String email,
                                   @Field("phone") String phone,
                                   @Field("company") String company,
                                   @Field("password") String pin);

    @FormUrlEncoded
    @POST("authenticate")
    Call<ParentModel> loginUser(@Field("phone") String phone,
                                @Field("password") String pin);

    @FormUrlEncoded
    @POST("vehicles")
    Call<ParentModel> createVehicle(@Query("token") String token,
                                    @Field("registration") String registration,
                                    @Field("vehicle_model") String model,
                                    @Field("manufacture_year ") String manufacture_year,
                                    @Field("seating_capacity") String seating_capacity);

//    @GET("authenticate/{parameter}")
//    Call<ParentModel> getUser(@Path("parameter") String requestType,
//                              @Query("token") String token);

    @GET("vehicles")
    Call<ParentModel> getVehicles(@Query("token") String requestType);

    @FormUrlEncoded
    @POST("transactions")
    Call<ParentModel> postTransaction(@Query("token") String query,
                                      @Field("vehicle_id") int vehicleKey,
                                      @Field("amount") double amount,
                                      @Field("description") String description);

    @GET("transactions")
    Call<ParentModel> getTransactions(@Query("token") String requestType);


}
