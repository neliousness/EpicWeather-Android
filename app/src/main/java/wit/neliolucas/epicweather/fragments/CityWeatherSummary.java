package wit.neliolucas.epicweather.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import wit.neliolucas.epicweather.adapters.CityRecyclerAdapter;
import wit.neliolucas.epicweather.services.RetrofitClient;
import wit.neliolucas.epicweather.util.Constants;
import wit.neliolucas.epicweather.util.WeatherHelper;

import static wit.neliolucas.epicweather.util.Constants.DATE_PATTERN;
import static wit.neliolucas.epicweather.util.Constants.WEATHER_DATA;


public class CityWeatherSummary extends Fragment {

    private View view;
    private TextView currentCity;
    private TextView currentCountry;
    private TextView currentTemp;
    private TextView currentHumidity;
    private TextView currentRealFeel;
    private TextView currentWind;
    private ImageView currentWeather;
    private ImageView loaderCloud;
    private MaterialCardView cardView;
    private LinearLayout loader;

    private final AnimationSet animationSet = new AnimationSet(true);
    private CityWeather currentCityWeather;
    private List<CityWeather> cityWeathers = new ArrayList<>();
    private CityRecyclerAdapter adapter;
    private HashMap<String, CityWeather> weatherHashMap;

    public CityWeatherSummary() {
        // Required empty public constructor
    }

    public static CityWeatherSummary newInstance(HashMap<String, CityWeather> cityWeatherHashMap) {
        CityWeatherSummary fragment = new CityWeatherSummary();
        Bundle args = new Bundle();
        args.putSerializable(WEATHER_DATA, cityWeatherHashMap);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            weatherHashMap = (HashMap<String, CityWeather>) getArguments().getSerializable(WEATHER_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_city_weather_summary, container, false);
        initAnimation();
        initCurrentWeatherData();
        initCurrentWeatherView();
        initWeatherList();
        initCityRecyclerView();
        return view;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onResume() {
        super.onResume();
        WeatherHelper.hasInternetConnection().subscribe((hasInternet) -> {
            if (hasInternet) {
                String currentTime = new SimpleDateFormat(DATE_PATTERN).format(new Date());
                if (!currentCityWeather.getLocation().getLocaltime().equals(currentTime)) {
                    loaderCloud.startAnimation(animationSet);
                    loader.setVisibility(View.VISIBLE);
                    reloadWeatherData();
                }
            } else {
                Toast toast = Toast.
                        makeText(getContext(), "No internet connection", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private void initCurrentWeatherData() {
        currentCityWeather = weatherHashMap.get(Constants.CURRENT_LOCATION);
    }

    private void initCurrentWeatherView() {
        loader = view.findViewById(R.id.loader);
        loaderCloud = view.findViewById(R.id.loader_cloud);
        currentCity = view.findViewById(R.id.current_city);
        currentCountry = view.findViewById(R.id.current_country);
        currentTemp = view.findViewById(R.id.current_temp);
        currentHumidity = view.findViewById(R.id.current_humidity);
        currentRealFeel = view.findViewById(R.id.current_real_feel);
        currentWind = view.findViewById(R.id.current_wind);
        currentWeather = view.findViewById(R.id.current_weather_icon);
        cardView = view.findViewById(R.id.current_weather_card);
        EditText search = view.findViewById(R.id.searchEt);

        currentCity.setText(String.format("%s, ", currentCityWeather.getLocation().getName()));
        currentCountry.setText(currentCityWeather.getLocation().getCountry());
        currentTemp.setText(String.format("%s°C", Math.round(currentCityWeather.getCurrent().getTemp_c())));
        currentHumidity.setText(String.format("%s%s", currentCityWeather.getCurrent().getHumidity(), "%"));
        currentRealFeel.setText(String.format("%s°", Math.round(currentCityWeather.getCurrent().getFeelslike_c())));
        currentWind.setText(String.format("%skm/h", Math.round(currentCityWeather.getCurrent().getWind_kph())));
        currentWeather.setImageResource(WeatherHelper.getResourceId(view.getContext(), currentCityWeather.getCurrent().getCondition().getText()));
        cardView.setOnClickListener(view1 -> getActivity().getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container, CityWeatherDetails.newInstance(currentCityWeather))
                .addToBackStack(null)
                .commit());

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterCities(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void filterCities(String filter) {
        adapter.update(filter);
    }

    private void initWeatherList() {
        cityWeathers = new ArrayList<>(weatherHashMap.values());
        cityWeathers.remove(currentCityWeather);
    }

    private void initCityRecyclerView() {
        RecyclerView cityRecyclerView = view.findViewById(R.id.otherCitiesRycV);
        cityRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        adapter = new CityRecyclerAdapter(getActivity(), cityWeathers);
        cityRecyclerView.setAdapter(adapter);
    }

    private void initAnimation() {

        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
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
                onError(t);
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
        boolean currentCityLoaded = currentCityWeather.getLocation().getName().equals(cityWeather.getLocation().getName());
        if (!currentCityLoaded) {
            adapter.update(cityWeather);
        } else {
            animationSet.cancel();
            loader.setVisibility(View.GONE);
            currentCity.setText(String.format("%s, ", cityWeather.getLocation().getName()));
            currentCountry.setText(cityWeather.getLocation().getCountry());
            currentTemp.setText(String.format("%s°C", Math.round(cityWeather.getCurrent().getTemp_c())));
            currentHumidity.setText(String.format("%s%s", cityWeather.getCurrent().getHumidity(), "%"));
            currentRealFeel.setText(String.format("%s°", Math.round(cityWeather.getCurrent().getFeelslike_c())));
            currentWind.setText(String.format("%skm/h", Math.round(cityWeather.getCurrent().getWind_kph())));
            currentWeather.setImageResource(WeatherHelper.getResourceId(view.getContext(), cityWeather.getCurrent().getCondition().getText()));
            cardView.setOnClickListener(view1 -> getActivity().getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container, CityWeatherDetails.newInstance(cityWeather))
                    .addToBackStack(null)
                    .commit());
            return;
        }

        if (adapter.getItemCount() == Constants.cities.length && !currentCityLoaded) {
            fetchCurrentLocationWeather();
        }
    }

    private void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    private void reloadWeatherData() {
        adapter.clear();
        for (String city : Constants.cities) {
            fetchCityWeather(city);
        }
    }
}