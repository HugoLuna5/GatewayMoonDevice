package lunainc.com.mx.gatewaymoon.Utils;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;

import lunainc.com.mx.gatewaymoon.Api.APIService;
import lunainc.com.mx.gatewaymoon.Model.ResponseDefault;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Constants {



    @SuppressLint("LogNotTimber")
    public void updateTokenFromNew(APIService mAPIService,String deviceToken, String device_id, String token){
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("device_token", deviceToken)
                .addFormDataPart("device_id", device_id)
                .build();
        mAPIService.updateToken("Accept", token, requestBody).enqueue(new Callback<ResponseDefault>() {

            @Override
            public void onResponse( Call<ResponseDefault> call,  Response<ResponseDefault> response) {

                if (response.isSuccessful()){

                    Log.e("ErrorRed", Objects.requireNonNull(response.body()).getMessage());
                }else{
                    Log.e("ErrorRed", "Error(500) en el servidor");
                    //Toast.makeText(MainActivity.this, "Error(500) en el servidor", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseDefault> call, Throwable t) {
                Log.e("ErrorRed", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    @SuppressLint("LogNotTimber")
    public void updateDeviceToken(APIService mAPIService, String device_id, String completeToken){


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String deviceToken = instanceIdResult.getToken();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("device_token", deviceToken)
                    .addFormDataPart("device_id", device_id)
                    .build();
            mAPIService.updateToken("Accept", completeToken, requestBody).enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {

                    if (response.isSuccessful()){

                        Log.e("ErrorRed", Objects.requireNonNull(response.body()).getMessage());
                    }else{
                        Log.e("ErrorRed", "Error(500) en el servidor");
                        //Toast.makeText(MainActivity.this, "Error(500) en el servidor", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    Log.e("ErrorRed", Objects.requireNonNull(t.getMessage()));
                }
            });

        });

    }

}
