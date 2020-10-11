package lunainc.com.mx.gatewaymoon.UI;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.iid.FirebaseInstanceId;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import lunainc.com.mx.gatewaymoon.Api.APIService;
import lunainc.com.mx.gatewaymoon.Api.ApiUtils;
import lunainc.com.mx.gatewaymoon.Model.CreatedDeviceResponse;
import lunainc.com.mx.gatewaymoon.Model.MessageEvent;
import lunainc.com.mx.gatewaymoon.Model.ResponseDefault;
import lunainc.com.mx.gatewaymoon.R;
import lunainc.com.mx.gatewaymoon.Utils.Constants;
import lunainc.com.mx.gatewaymoon.Utils.SmsListener;
import lunainc.com.mx.gatewaymoon.Utils.SmsReceiver;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_MMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.RECEIVE_WAP_PUSH;
import static android.Manifest.permission.SEND_SMS;

public class MainActivity extends AppCompatActivity implements SmsListener {

    @BindView(R.id.btnAction)
    ExtendedFloatingActionButton actionRegisterDevice;

    private APIService apiService;
    private String tokenApp;
    private SharedPreferences sharedPreferences;
    private String status = "";
    private String device_id = "";
    private String phoneNumber;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkPermissions();
        initVars();
        SmsReceiver.bindListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) ==
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, RECEIVE_MMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, RECEIVE_WAP_PUSH) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tMgr = (TelephonyManager)   this.getSystemService(Context.TELEPHONY_SERVICE);
            phoneNumber = tMgr.getLine1Number();
        } else {
            requestPermission();
        }
    }

    private void initVars() {
        apiService = ApiUtils.getAPIService();
        tokenApp = "Bearer "+getResources().getString(R.string.token_web_app);
        sharedPreferences = getSharedPreferences(
                "credentials", Context.MODE_PRIVATE);

        status = sharedPreferences.getString(("status"), "noLogged");
        device_id = sharedPreferences.getString(("device_id"), "noLogged");
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkSession();
        events();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {


        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(event.getPhone(), null, event.getMessage(), null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();

    };

    private void checkSession() {
        if (!status.equals("noLogged") &&  !status.isEmpty()){
            actionRegisterDevice.setVisibility(View.GONE);
            Toast.makeText(this, "En espera ha enviar SMS'S", Toast.LENGTH_SHORT).show();
            new Constants().updateDeviceToken(apiService, device_id, tokenApp );
        }
    }

    private void events() {
        actionRegisterDevice.setOnClickListener( v -> registerDevice());
    }


    public void registerDevice(){

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String deviceToken = instanceIdResult.getToken();

            @SuppressLint("HardwareIds") RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("device_token", deviceToken)
                    .addFormDataPart("device_name", Build.MODEL+"_"+ Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID))
                    .addFormDataPart("phone_number", phoneNumber)
                    .addFormDataPart("region", "MX")
                    .build();


            apiService.createDevice("Accept", tokenApp, body)
                    .enqueue(new Callback<CreatedDeviceResponse>() {
                        @Override
                        public void onResponse(Call<CreatedDeviceResponse> call, Response<CreatedDeviceResponse> response) {

                            if (response.isSuccessful()){
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("status", "activated");
                                editor.putString("device_id", response.body().getDevice_id());
                                editor.commit();
                                editor.apply();
                                checkSession();
                                Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MainActivity.this, "Ocurrio un error al registrar el dispositivo", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(Call<CreatedDeviceResponse> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "Ocurrio un error al registrar el dispositivo: Error en la petición: "+t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });


        });
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE, SEND_SMS, RECEIVE_SMS, RECEIVE_SMS, RECEIVE_WAP_PUSH}, 100);
        }
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                TelephonyManager tMgr = (TelephonyManager)  this.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED  &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=      PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) !=      PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) !=      PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) !=      PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_WAP_PUSH) !=      PackageManager.PERMISSION_GRANTED ) {
                    return;
                }
                phoneNumber = tMgr.getLine1Number();

                break;
        }
    }


    @Override
    public void messageReceived(String messageText, String phone) {
Log.e("MessageReceived", messageText);
    }
}