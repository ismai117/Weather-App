package com.weather.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    @GET("data/2.5/weather?&units=metric")
    Call<WeatherResponse> getData(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String appid
    );
}
