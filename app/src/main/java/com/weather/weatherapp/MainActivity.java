package com.weather.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private static final int locationRequest = 1;
    private static String AppID = "059950627dc075ed4215d9a4be8ebbd5";
    private double lat;
    private double lon;
//    private  LocationManager locationManager;

    @BindView(R.id.area_name)
    TextView areaName;
    @BindView(R.id.timezone)
    TextView areaTime;
    @BindView(R.id.feelslike)
    TextView areaFeels;
    @BindView(R.id.deg)
    TextView areaDeg;
    @BindView(R.id.wind)
    TextView areaWind;
    @BindView(R.id.humidity)
    TextView areaHumidity;
    @BindView(R.id.pressure)
    TextView areaPressure;
    @BindView(R.id.visibility)
    TextView areaVisbility;
    @BindView(R.id.sunrise)
    TextView areaSunrise;
    @BindView(R.id.sunset)
    TextView areaSunset;
    @BindView(R.id.weather_image)
    ImageView weatherImage;
    @BindView(R.id.where)
    TextView weatherArea;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getLocation();
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, locationRequest);
        } else {
            getCurrentLocation();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == locationRequest && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(MainActivity.this).removeLocationUpdates(this);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    int latestlocationindex = locationResult.getLocations().size() - 1;
                    lat = locationResult.getLocations().get(latestlocationindex).getLatitude();
                    lon = locationResult.getLocations().get(latestlocationindex).getLongitude();



                    progressBar.setVisibility(View.GONE);

                    getWeather(lat, lon);

                    Log.d("latlon", "lat: " + lat + " \n" + "lon: " + lon + " ");
                }
            }
        }, Looper.getMainLooper());

    }


    private void getWeather(double lat, double lon) {

        WeatherService weatherService = ApiClient.getClient().create(WeatherService.class);

        Call<WeatherResponse> getdata = weatherService.getData(lat, lon, AppID);

        Toast.makeText(this, "" + lat + " " + lon, Toast.LENGTH_SHORT).show();

        getdata.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {


                WeatherResponse weatherResponse = response.body();

                Log.d("weather",
                        "name: " + weatherResponse.name + " " +
                                "humidity: " + weatherResponse.main.humidity + " " +
                                "time: " + weatherResponse.timezone

                );

                Log.d("weather",
                        "name: " + weatherResponse.name + " " +
                                "humidity: " + weatherResponse.main.humidity + " " +
                                "time: " + weatherResponse.timezone + " " +
                                "deg: " + weatherResponse.main.temp + weatherResponse.weather.get(0).description

                );




                areaName.setText(weatherResponse.name);
                areaFeels.setText(weatherResponse.weather.get(0).description);
                areaDeg.setText(Math.round(weatherResponse.main.temp) + "°");
                areaWind.setText(String.valueOf(weatherResponse.wind.speed));
                areaHumidity.setText(String.valueOf(weatherResponse.main.humidity));
                areaPressure.setText(String.valueOf(weatherResponse.main.pressure));
                areaVisbility.setText(String.valueOf(weatherResponse.visibility));


                weatherArea.setText("Weather Today in " + weatherResponse.name + ", " + weatherResponse.sys.country);


                try {
                    String icon = weatherResponse.weather.get(0).icon;
                    Picasso.get().load("https://openweathermap.org/img/wn/" + icon + "@2x.png").into(weatherImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {

            }
        });
    }


}