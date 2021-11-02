package wit.neliolucas.epicweather.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import wit.neliolucas.epicweather.R;
import wit.neliolucas.epicweather.fragments.CityWeatherDetails;
import wit.neliolucas.epicweather.models.CityWeather;
import wit.neliolucas.epicweather.util.WeatherHelper;

/**
 * Author Nelio Lucas
 * Date 10/30/2021
 */
public class CityRecyclerAdapter extends RecyclerView.Adapter<CityRecyclerAdapter.ViewHolder> {

    private List<CityWeather> cityWeathers;
    private final FragmentActivity activity;
    private View view;
    private List<CityWeather> temp;

    public CityRecyclerAdapter(FragmentActivity activity, List<CityWeather> cityWeathers) {
        this.cityWeathers = cityWeathers;
        this.temp = cityWeathers;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CityRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.other_city_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityRecyclerAdapter.ViewHolder holder, int position) {

        CityWeather cityWeather = cityWeathers.get(position);
        holder.temperatureTv.setText(String.format("%s°C", Math.round(cityWeather.getCurrent().getTemp_c())));
        holder.cityTv.setText(cityWeather.getLocation().getName());
        holder.countryTv.setText(cityWeather.getLocation().getCountry());
        holder.humidityTv.setText(String.format("%s%s", Math.round(cityWeather.getCurrent().getHumidity()), "%"));
        holder.windTv.setText(String.format("%skm/h", Math.round(cityWeather.getCurrent().getWind_kph())));
        holder.weatherIcon.setImageResource(WeatherHelper.getResourceId(view.getContext(), cityWeather.getCurrent().getCondition().getText()));
        holder.cardRoot.setOnClickListener(view -> {
            activity.getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container, CityWeatherDetails.newInstance(cityWeather))
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return cityWeathers.size();
    }

    public void update(String filter) {

        List<CityWeather> filteredList = new ArrayList<>();
        for (CityWeather cityWeather : temp) {
            if (cityWeather.getLocation().getName().toLowerCase().contains(filter.toLowerCase())) {
                filteredList.add(cityWeather);
            }
        }
        cityWeathers = filteredList;
        notifyDataSetChanged();
    }

    public void update(CityWeather cityWeather) {
        cityWeathers.add(cityWeather);
        notifyDataSetChanged();
    }

    public void clear()
    {
        cityWeathers.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView temperatureTv;
        TextView cityTv;
        TextView countryTv;
        TextView humidityTv;
        TextView windTv;
        ImageView weatherIcon;
        MaterialCardView cardRoot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot = itemView.findViewById(R.id.itemRoot);
            temperatureTv = itemView.findViewById(R.id.city_item_temp);
            cityTv = itemView.findViewById(R.id.city_item_city);
            countryTv = itemView.findViewById(R.id.city_item_country);
            humidityTv = itemView.findViewById(R.id.city_item_humidity);
            windTv = itemView.findViewById(R.id.city_item_wind);
            weatherIcon = itemView.findViewById(R.id.city_item_weather_icon);
        }
    }
}
