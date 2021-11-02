package wit.neliolucas.epicweather.models;

import java.io.Serializable;
import java.util.List;

/**
 * Author Nelio Lucas
 * Date 10/31/2021
 */
public class Forecast implements Serializable {
    private List<ForecastDay> forecastday;

    public List<ForecastDay> getForecastday() {
        return forecastday;
    }

    public void setForecastday(List<ForecastDay> forecastday) {
        this.forecastday = forecastday;
    }

    @Override
    public String toString() {
        return "Forecast{" +
                "forecastday=" + forecastday +
                '}';
    }
}
