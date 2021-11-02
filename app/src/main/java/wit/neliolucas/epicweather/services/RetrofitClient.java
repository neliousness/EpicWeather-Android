package wit.neliolucas.epicweather.services;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Author Nelio Lucas
 * Date 10/31/2021
 */
public class RetrofitClient {

    private static Retrofit retrofit;
    private static String BASE_URL = "https://ipapi.co/197.249.38.222/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().
                    addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).
                    baseUrl(BASE_URL).build();
        }
        return retrofit;
    }
}
