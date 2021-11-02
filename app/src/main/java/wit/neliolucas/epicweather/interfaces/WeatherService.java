package wit.neliolucas.epicweather.interfaces;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;
import wit.neliolucas.epicweather.models.CityWeather;

/**
 * Author Nelio Lucas
 * Date 10/31/2021
 */
public interface WeatherService {
    @GET
    Observable<CityWeather> getWeather(@Url String url);
}
