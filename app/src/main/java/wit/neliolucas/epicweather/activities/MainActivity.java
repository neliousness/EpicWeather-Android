package wit.neliolucas.epicweather.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import wit.neliolucas.epicweather.R;
import wit.neliolucas.epicweather.fragments.CityWeatherSummary;
import wit.neliolucas.epicweather.models.CityWeather;

import static wit.neliolucas.epicweather.util.Constants.WEATHER_DATA;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            HashMap<String, CityWeather> stringCityWeatherHashMap = (HashMap<String, CityWeather>) getIntent().getSerializableExtra(WEATHER_DATA);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, CityWeatherSummary.newInstance(stringCityWeatherHashMap)).commit();
        }
    }
}