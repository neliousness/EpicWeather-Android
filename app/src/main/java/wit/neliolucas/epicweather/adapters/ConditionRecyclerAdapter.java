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
import wit.neliolucas.epicweather.models.ConditionData;
import wit.neliolucas.epicweather.util.WeatherHelper;

/**
 * Author Nelio Lucas
 * Date 10/30/2021
 */
public class ConditionRecyclerAdapter extends RecyclerView.Adapter<ConditionRecyclerAdapter.ViewHolder> {

    private List<ConditionData> conditionData;
    private final FragmentActivity activity;

    public ConditionRecyclerAdapter(FragmentActivity activity, List<ConditionData> conditions) {
        this.conditionData = conditions;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ConditionRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.condition_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConditionRecyclerAdapter.ViewHolder holder, int position) {

        ConditionData condition = conditionData.get(position);
        holder.conditionIcon.setImageResource(WeatherHelper.getConditionResourceId(activity, condition.getIcon()));
        holder.conditionValue.setText(condition.getValue());
        holder.conditionLabel.setText(condition.getLabel());
    }

    @Override
    public int getItemCount() {
        return conditionData.size();
    }

    public void updateAll(List<ConditionData> conditionData) {
        this.conditionData = conditionData;
        notifyDataSetChanged();
    }

    public void clear() {
        conditionData.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView conditionIcon;
        TextView conditionValue;
        TextView conditionLabel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            conditionIcon = itemView.findViewById(R.id.condition_icon);
            conditionValue = itemView.findViewById(R.id.condition_value);
            conditionLabel = itemView.findViewById(R.id.condition_label);

        }
    }
}
