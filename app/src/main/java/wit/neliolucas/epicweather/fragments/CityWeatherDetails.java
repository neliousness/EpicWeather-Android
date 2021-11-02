package wit.neliolucas.epicweather.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wit.neliolucas.epicweather.R;
import wit.neliolucas.epicweather.interfaces.WeatherService;
import wit.neliolucas.epicweather.models.CityWeather;
import wit.neliolucas.epicweather.models.ConditionData;
import wit.neliolucas.epicweather.models.Hour;
import wit.neliolucas.epicweather.models.HourData;
import wit.neliolucas.epicweather.adapters.ConditionRecyclerAdapter;
import wit.neliolucas.epicweather.adapters.HourlyRecyclerAdapter;
import wit.neliolucas.epicweather.services.RetrofitClient;
import wit.neliolucas.epicweather.util.Constants;
import wit.neliolucas.epicweather.util.WeatherHelper;

import static wit.neliolucas.epicweather.util.Constants.DATE_PATTERN;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CityWeatherDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CityWeatherDetails extends Fragment {

    private static final String CITY_WEATHER_DATA = "city_weather_data";

    private TextView city;
    private TextView country;
    private TextView temp;
    private TextView conditionDescription;
    private TextView minMax;
    private TextView sunrise;
    private TextView sunset;
    private ImageView weatherIcon;
    private LinearLayout loader;
    private ImageView loaderCloud;
    private View view;

    private ConditionRecyclerAdapter conditionRecyclerAdapter;
    private HourlyRecyclerAdapter hourlyRecyclerAdapter;
    private CityWeather cityWeather;
    private final List<ConditionData> conditionData = new ArrayList<>();
    private final List<HourData> hourData = new ArrayList<>();
    private final AnimationSet animationSet = new AnimationSet(true);

    public CityWeatherDetails() {
    }

    public static CityWeatherDetails newInstance(CityWeather cityWeather) {
        CityWeatherDetails fragment = new CityWeatherDetails();
        Bundle args = new Bundle();
        args.putSerializable(CITY_WEATHER_DATA, cityWeather);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cityWeather = (CityWeather) getArguments().getSerializable(CITY_WEATHER_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_city_weather_details, container, false);

        initAnimation();
        initConditionData();
        initHourData();
        initDetailedCityView();
        initConditionRecyclerView();
        initHourRecyclerView();

        return view;
    }

    private void initDetailedCityView() {
        city = view.findViewById(R.id.details_city);
        country = view.findViewById(R.id.details_country);
        temp = view.findViewById(R.id.details_temp);
        conditionDescription = view.findViewById(R.id.details_condition_description);
        minMax = view.findViewById(R.id.details_min_max);
        sunrise = view.findViewById(R.id.details_sunrise);
        sunset = view.findViewById(R.id.details_sunset);
        weatherIcon = view.findViewById(R.id.details_weather_icon);
        LinearLayout backContainer = view.findViewById(R.id.back_container);
        loader = view.findViewById(R.id.details_loader);
        loaderCloud = view.findViewById(R.id.loader_cloud);

        city.setText(cityWeather.getLocation().getName());
        country.setText(cityWeather.getLocation().getCountry());
        temp.setText(String.format("%s°C", Math.round(cityWeather.getCurrent().getTemp_c())));
        conditionDescription.setText(cityWeather.getCurrent().getCondition().getText());
        minMax.setText(String.format("Min %s - %s Max", roundMaxMin(cityWeather, false), roundMaxMin(cityWeather, true)));
        sunrise.setText(cityWeather.getForecast().getForecastday().get(0).getAstro().getSunrise());
        sunset.setText(cityWeather.getForecast().getForecastday().get(0).getAstro().getSunset());
        weatherIcon.setImageResource(WeatherHelper.getResourceId(view.getContext(), cityWeather.getCurrent().getCondition().getText()));
        backContainer.setOnClickListener(view1 -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void initConditionRecyclerView() {
        RecyclerView conditionRecyclerView = view.findViewById(R.id.condition_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        conditionRecyclerView.setLayoutManager(gridLayoutManager);
        conditionRecyclerAdapter = new ConditionRecyclerAdapter(getActivity(), conditionData);
        conditionRecyclerView.setAdapter(conditionRecyclerAdapter);
    }

    private void initHourRecyclerView() {
        RecyclerView hourlyRecyclerView = view.findViewById(R.id.hourly_recycler_view);
        hourlyRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        hourlyRecyclerAdapter = new HourlyRecyclerAdapter(getActivity(), hourData);
        hourlyRecyclerView.setAdapter(hourlyRecyclerAdapter);
    }

    private void initConditionData() {
        ConditionData pressure = new ConditionData();
        pressure.setIcon("pressure");
        pressure.setLabel("Pressure");
        pressure.setValue(String.format("%sin", Math.round(cityWeather.getCurrent().getPressure_in())));

        ConditionData realFeel = new ConditionData();
        realFeel.setIcon("rf");
        realFeel.setLabel("Real Feel");
        realFeel.setValue(String.format("%s°", Math.round(cityWeather.getCurrent().getFeelslike_c())));

        ConditionData humidity = new ConditionData();
        humidity.setIcon("water_drop");
        humidity.setLabel("Humidity");
        humidity.setValue(String.format("%s%s", cityWeather.getCurrent().getHumidity(), "%"));

        ConditionData visibility = new ConditionData();
        visibility.setIcon("visibility");
        visibility.setLabel("Visibility");
        visibility.setValue(String.format("%skm", Math.round(cityWeather.getCurrent().getVis_km())));

        ConditionData uv = new ConditionData();
        uv.setIcon("sun");
        uv.setLabel("UV");
        uv.setValue(String.valueOf(Math.round(cityWeather.getCurrent().getUv())));

        ConditionData wind = new ConditionData();
        wind.setIcon("wind");
        wind.setLabel("Wind");
        wind.setValue(String.format("%skm/h", Math.round(cityWeather.getCurrent().getWind_kph())));

        conditionData.add(pressure);
        conditionData.add(realFeel);
        conditionData.add(humidity);
        conditionData.add(visibility);
        conditionData.add(uv);
        conditionData.add(wind);

    }

    private void initHourData() {
        List<Hour> hours = cityWeather.getForecast().getForecastday().get(0).getHour();

        for (Hour hour : hours) {
            hourData.add(new HourData(WeatherHelper.formatHours(hour.getTime()), hour.getCondition().getText(), String.format("%s°", Math.round(hour.getTemp_c()))));
        }
    }

    private String roundMaxMin(CityWeather cityWeather, boolean max) {
        if (max) {
            return String.valueOf(Math.round(cityWeather.getForecast().getForecastday().get(0).getDay().getMaxtemp_c()));
        }
        return String.valueOf(Math.round(cityWeather.getForecast().getForecastday().get(0).getDay().getMintemp_c()));
    }

    @SuppressLint("CheckResult")
    @Override
    public void onResume() {
        super.onResume();

        WeatherHelper.hasInternetConnection().subscribe((hasInternet) -> {
            if(hasInternet) {
                String currentTime = new SimpleDateFormat(DATE_PATTERN).format(new Date());
                if (!cityWeather.getLocation().getLocaltime().equals(currentTime)) {
                    loaderCloud.startAnimation(animationSet);
                    loader.setVisibility(View.VISIBLE);
                    reloadWeatherData();
                }
            }else {
                Toast toast = Toast.
                        makeText(getContext(),"No internet connection",Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }

    @SuppressLint("CheckResult")
    private void fetchCityWeather(String city) {
        WeatherService api = RetrofitClient.getRetrofitInstance().create(WeatherService.class);
        Observable<CityWeather> weatherObservable = api.getWeather(String.format(Constants.WEATHER_BASE_URL, city));
        weatherObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onComplete, this::onError);
    }

    private void onComplete(CityWeather cityWeather) {
        this.cityWeather = cityWeather;
        city.setText(cityWeather.getLocation().getName());
        country.setText(cityWeather.getLocation().getCountry());
        temp.setText(String.format("%s°C", Math.round(cityWeather.getCurrent().getTemp_c())));
        conditionDescription.setText(cityWeather.getCurrent().getCondition().getText());
        minMax.setText(String.format("Min %s - %s Max", roundMaxMin(cityWeather, false), roundMaxMin(cityWeather, true)));
        sunrise.setText(cityWeather.getForecast().getForecastday().get(0).getAstro().getSunrise());
        sunset.setText(cityWeather.getForecast().getForecastday().get(0).getAstro().getSunset());
        weatherIcon.setImageResource(WeatherHelper.getResourceId(view.getContext(), cityWeather.getCurrent().getCondition().getText()));

        hourlyRecyclerAdapter.clear();
        conditionRecyclerAdapter.clear();
        initConditionData();
        initHourData();
        hourlyRecyclerAdapter.updateAll(hourData);
        conditionRecyclerAdapter.updateAll(conditionData);
        loader.setVisibility(View.GONE);
        animationSet.cancel();
    }

    private void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    private void initAnimation(){

        AlphaAnimation fadeOut = new AlphaAnimation(1.0f , 0.0f);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f);
        animationSet.addAnimation(fadeOut);
        animationSet.addAnimation(fadeIn);
        animationSet.setDuration(1500);
        animationSet.setRepeatMode(Animation.INFINITE);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                loaderCloud.startAnimation(animationSet);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void reloadWeatherData() {
        fetchCityWeather(cityWeather.getLocation().getName());
    }
}