package com.example.dunzoserver.Remote;

import com.example.dunzoserver.Model.DataMessage;
import com.example.dunzoserver.Model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAKnGnKnQ:APA91bEloDOSSUyUNRqfomXhT4xXk6cm45e7GgTpm1V4eVt8uAbYtU_yoVDiDMsclebMIrDSMEQ1Pmgd-lQ9RVFNaJ9j2jEwKsNg5VBLQOIky_73w7nd8QA11EJ3UYGU7H2mAPEPRunM"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);
}
