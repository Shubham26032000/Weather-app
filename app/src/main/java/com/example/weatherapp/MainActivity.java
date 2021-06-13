package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    final String url = "http://api.openweathermap.org/data/2.5/weather";
    final String apiKey = "6167254a872b3097a04b6ee4e49a833c";

    String location_provider = LocationManager.GPS_PROVIDER;
    LocationManager locationManager;
    LocationListener locationListener;
    final int MIN_TIME = 5000;
    final int MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.mainContainer.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        getWeatherData("Malvan");

        binding.seachView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                binding.mainContainer.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
                getWeatherData(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        binding.searchViewInError.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                binding.mainContainer.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
                getWeatherData(s);
                binding.searchViewErrorLayout.setVisibility(View.GONE);
                binding.tvErrorMessage.setVisibility(View.GONE);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }

    private void getWeatherData(String location) {

        String tempUrl = url + "?q=" + location + "&appid=" + apiKey;
        StringRequest request = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    setFetchedData(response);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error occur!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                binding.progressBar.setVisibility(View.GONE);
                binding.mainContainer.setVisibility(View.GONE);
                binding.tvErrorMessage.setVisibility(View.VISIBLE);
                binding.searchViewErrorLayout.setVisibility(View.VISIBLE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

    }

    private static String getTimeString(String timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Integer.parseInt(timeStamp) * 1000L);
        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
        return simpleDateFormat.format(date);
    }

    private String getUpdateDate(String timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Integer.parseInt(timeStamp) * 1000L);
        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a");
        return simpleDateFormat.format(date);
    }


    private void setFetchedData(String response) throws JSONException {
            JSONObject jsonObject = new JSONObject(response);

            JSONArray jsonArray = jsonObject.getJSONArray("weather");
            JSONObject weather = jsonArray.getJSONObject(0);

            binding.tvStatus.setText(weather.getString("description"));
            JSONObject main = jsonObject.getJSONObject("main");

            int temperature = (int)(main.getDouble("temp") - 273.15);
            int tempMin = (int) (main.getDouble("temp_min") - 273.15);
            int tempMax = (int) (main.getDouble("temp_max") - 273.15);

            binding.tvTempMax.setText("Max :"+tempMax + "°C");
            binding.tvTempMin.setText("Min :"+tempMin + "°C");
            binding.tvTemperature.setText(temperature + "°C");

            long sunriseTime = jsonObject.getJSONObject("sys").getLong("sunrise");
            long sunsetTime = jsonObject.getJSONObject("sys").getLong("sunset");
            long updatedTime = jsonObject.getLong("dt");

            binding.tvSunrise.setText(getTimeString(String.valueOf(sunriseTime)));
            binding.tvSunset.setText(getTimeString(String.valueOf(sunsetTime)));

            binding.tvUpdateat.setText("Updated at "+getUpdateDate(updatedTime+""));

            String windSpeed = jsonObject.getJSONObject("wind").getString("speed");
            binding.tvWind.setText(windSpeed+" m/s");

            String humidity = main.getString("humidity");
            binding.tvHumidity.setText(humidity + " %");

            String pressure = main.getString("pressure");
            binding.tvPressure.setText(pressure +" hPa");


            binding.tvLocation.setText(jsonObject.getString("name"));

            setWeatherIcon(weather.getString("icon"));
            binding.progressBar.setVisibility(View.GONE);
            binding.mainContainer.setVisibility(View.VISIBLE);
        }

        private void setWeatherIcon(String icon) {
        switch (icon)
        {
            case "01d":
                binding.ivWeatherIcon.setImageResource(R.drawable.d01);
                break;
            case "01n":
                binding.ivWeatherIcon.setImageResource(R.drawable.n01);
                break;
            case "02d":
                binding.ivWeatherIcon.setImageResource(R.drawable.d02);
                break;
            case "02n":
                binding.ivWeatherIcon.setImageResource(R.drawable.n02);
                break;
            case "03d":
                binding.ivWeatherIcon.setImageResource(R.drawable.d03);
                break;
            case "03n":
                binding.ivWeatherIcon.setImageResource(R.drawable.n03);
                break;
            case "04d":
                binding.ivWeatherIcon.setImageResource(R.drawable.d04);
                break;
            case "04n":
                binding.ivWeatherIcon.setImageResource(R.drawable.n04);
                break;
            case "09d":
                binding.ivWeatherIcon.setImageResource(R.drawable.d09);
                break;
            case "09n":
                binding.ivWeatherIcon.setImageResource(R.drawable.n09);
                break;
            case "10d":
                binding.ivWeatherIcon.setImageResource(R.drawable.d10);
                break;
            case "10n":
                binding.ivWeatherIcon.setImageResource(R.drawable.n10);
                break;
            case "11d":
                binding.ivWeatherIcon.setImageResource(R.drawable.d11);
                break;
            case "11n":
                binding.ivWeatherIcon.setImageResource(R.drawable.n11);
                break;
            case "13d":
                binding.ivWeatherIcon.setImageResource(R.drawable.d13);
                break;
            case "13n":
                binding.ivWeatherIcon.setImageResource(R.drawable.n13);
                break;
            case "50d":
                binding.ivWeatherIcon.setImageResource(R.drawable.d50);
                break;
            case "50n":
                binding.ivWeatherIcon.setImageResource(R.drawable.n50);
                break;
            default:

        }
    }


}
