package lunainc.com.mx.gatewaymoon.Api;

import lunainc.com.mx.gatewaymoon.Model.CreatedDeviceResponse;
import lunainc.com.mx.gatewaymoon.Model.ResponseDefault;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface APIService {


    @POST("api/create/device")
    Call<CreatedDeviceResponse> createDevice(@Header("Accept") String accept, @Header("Authorization") String auth, @Body RequestBody body);


    @POST("api/update/token")
    Call<ResponseDefault> updateToken(@Header("Accept") String accept, @Header("Authorization") String auth, @Body RequestBody body);

}
