package wit.neliolucas.epicweather.models;

/**
 * Author Nelio Lucas
 * Date 11/1/2021
 */
public class HourData {
    private String hour;
    private String weatherIcon;
    private String temp;

    public HourData(String hour, String weatherIcon, String temp) {
        this.hour = hour;
        this.weatherIcon = weatherIcon;
        this.temp = temp;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }


}
