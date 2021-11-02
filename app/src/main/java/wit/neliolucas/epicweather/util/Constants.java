package wit.neliolucas.epicweather.util;

/**
 * Author Nelio Lucas
 * Date 10/31/2021
 */
public class Constants {
    private static final String WEATHER_API_KEY = "666a79686d1646deae1184936212110";
    private static final String IP_API_KEY = "94b7eacf04ed4e199c1b64f3dc5f6ce3";

    public static final String IP_URL = "https://api.ipify.org/?format=text";
    public static final String CITY_BASE_URL = "https://api.ipgeolocation.io/ipgeo?apiKey="+IP_API_KEY+"&ip=%s";
    public static final String WEATHER_BASE_URL = "http://api.weatherapi.com/v1/forecast.json?key=" + WEATHER_API_KEY + "&q=%s&aqi=no";
    public static final String CURRENT_LOCATION = "current_location";
    public static final String WEATHER_DATA = "weatherData";
    public static final String DATE_PATTERN = "yyyy-MM-dd H:mm";
    public static final String[] cities =
            {"Lisbon",
                    "Madrid",
                    "Paris",
                    "Berlin",
                    "Copenhagen",
                    "Rome",
                    "Dublin",
                    "London",
                    "Vienna",
                    "Prague"};
}
