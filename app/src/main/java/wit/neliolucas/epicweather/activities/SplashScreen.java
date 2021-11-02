package wit.neliolucas.epicweather.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wit.neliolucas.epicweather.R;
import wit.neliolucas.epicweather.interfaces.CityService;
import wit.neliolucas.epicweather.interfaces.IpService;
import wit.neliolucas.epicweather.interfaces.WeatherService;
import wit.neliolucas.epicweather.models.CityWeather;
import wit.neliolucas.epicweather.models.IpData;
import wit.neliolucas.epicweather.services.RetrofitClient;
import wit.neliolucas.epicweather.util.Constants;
import wit.neliolucas.epicweather.util.WeatherHelper;

import static wit.neliolucas.epicweather.util.Constants.WEATHER_DATA;

public class SplashScreen extends AppCompatActivity {

    private HashMap<String, CityWeather> weatherMap = new HashMap<>();
    private boolean currentCityLoaded = false;
    private Observable<CityWeather> weatherObservable;
    private AnimationSet animationSet = new AnimationSet(true);

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        initAnimation();
        WeatherHelper.hasInternetConnection().subscribe((hasInternet) -> {
            if (hasInternet) {
                fetchWeatherData();
            } else {
                Toast toast = Toast.
                        makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        animationSet.cancel();
    }

    private void initAnimation() {
        ImageView imageView = findViewById(R.id.cloud_load);
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        animationSet.addAnimation(fadeOut);
        animationSet.addAnimation(fadeIn);
        animationSet.setDuration(1500);
        animationSet.setRepeatMode(Animation.INFINITE);
        imageView.startAnimation(animationSet);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.startAnimation(animationSet);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void fetchCurrentLocationWeather() {
        IpService api = RetrofitClient.getRetrofitInstance().create(IpService.class);
        Call<ResponseBody> call = api.getIp(Constants.IP_URL);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String ip = response.body().string();
                    fetchCityName(ip);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("error", t.getLocalizedMessage());
            }
        });
    }

    private void fetchCityName(String ip) {
        CityService service = RetrofitClient.getRetrofitInstance().create(CityService.class);
        Call<IpData> call = service.getCity(String.format(Constants.CITY_BASE_URL, ip));
        call.enqueue(new Callback<IpData>() {
            @Override
            public void onResponse(Call<IpData> call, Response<IpData> response) {
                try {
                    String city = response.body().getCity();
                    fetchCityWeather(city);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<IpData> call, Throwable t) {
                Log.e("error", t.getLocalizedMessage());
            }
        });
    }

    @SuppressLint("CheckResult")
    private void fetchCityWeather(String city) {
        WeatherService api = RetrofitClient.getRetrofitInstance().create(WeatherService.class);
        weatherObservable = api.getWeather(String.format(Constants.WEATHER_BASE_URL, city));
        weatherObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onComplete, this::onError);
    }

    private void onComplete(CityWeather cityWeather) {
        if (!currentCityLoaded) {
            weatherMap.put(cityWeather.getLocation().getName(), cityWeather);
        } else {
            weatherMap.put(Constants.CURRENT_LOCATION, cityWeather);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(WEATHER_DATA, weatherMap);
            startActivity(intent);
            weatherObservable.unsubscribeOn(Schedulers.io());
            finish();
            return;
        }

        if (weatherMap.size() == Constants.cities.length && !currentCityLoaded) {
            currentCityLoaded = true;
            fetchCurrentLocationWeather();
        }
    }

    private void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    private void fetchWeatherData() {
        for (String city : Constants.cities) {
            fetchCityWeather(city);
        }
    }

}