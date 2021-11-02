package wit.neliolucas.epicweather.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import wit.neliolucas.epicweather.R;
import wit.neliolucas.epicweather.models.HourData;
import wit.neliolucas.epicweather.util.WeatherHelper;

/**
 * Author Nelio Lucas
 * Date 10/30/2021
 */
public class HourlyRecyclerAdapter extends RecyclerView.Adapter<HourlyRecyclerAdapter.ViewHolder> {

    private List<HourData> hourData;
    private final FragmentActivity activity;

    public HourlyRecyclerAdapter(FragmentActivity activity, List<HourData> hourData) {
        this.hourData = hourData;
        this.activity = activity;
    }

    @NonNull
    @Override
    public HourlyRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.hourly_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyRecyclerAdapter.ViewHolder holder, int position) {

        HourData data = hourData.get(position);
        holder.forecastHour.setText(WeatherHelper.formatTime(data.getHour()));
        holder.forecastIcon.setImageResource(WeatherHelper.getResourceId(activity, data.getWeatherIcon()));
        holder.forecastTemp.setText(data.getTemp());
    }

    @Override
    public int getItemCount() {
        return hourData.size();
    }

    public void updateAll(List<HourData> hourData) {
        this.hourData = hourData;
        notifyDataSetChanged();
    }

    public void clear() {
        hourData.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView forecastHour;
        TextView forecastTemp;
        ImageView forecastIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            forecastHour = itemView.findViewById(R.id.forecast_hour);
            forecastIcon = itemView.findViewById(R.id.forecast_icon);
            forecastTemp = itemView.findViewById(R.id.forecast_temp);

        }
    }
}
