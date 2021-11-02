package wit.neliolucas.epicweather.interfaces;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Author Nelio Lucas
 * Date 10/31/2021
 */
public interface IpService {
    @GET
    Call<ResponseBody> getIp(@Url String ip);
}
