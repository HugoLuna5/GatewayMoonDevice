package lunainc.com.mx.gatewaymoon.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import lunainc.com.mx.gatewaymoon.Api.APIService;
import lunainc.com.mx.gatewaymoon.Api.ApiUtils;
import lunainc.com.mx.gatewaymoon.Model.MessageEvent;
import lunainc.com.mx.gatewaymoon.R;
import lunainc.com.mx.gatewaymoon.UI.MainActivity;

public class MessagingService extends FirebaseMessagingService {
    private SharedPreferences sharedPreferences;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        APIService apiService = ApiUtils.getAPIService();
        sharedPreferences = getSharedPreferences(
                "credentials", Context.MODE_PRIVATE);
        String device_id = sharedPreferences.getString(("device_id"), "noLogged");
       // new Constants().updateTokenFromNew(apiService, s, device_id, "Bearer "+getResources().getString(R.string.token_web_app));
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_check);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "gatewaymoonappnotification";
            CharSequence name = "gatewaymoonappnotification";
            String description = "Channel description";


            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setShowBadge(false);
            Objects.requireNonNull(notificationManager).createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "gatewaymoonappnotification")
                .setContentTitle(Objects.requireNonNull(remoteMessage.getNotification()).getTitle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_check)
                .setLargeIcon(bitmap)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentText(remoteMessage.getNotification().getBody())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(remoteMessage.getNotification().getBody()))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);



        Intent intent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);


        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setContentIntent(resultPendingIntent);

        EventBus.getDefault().post(new MessageEvent(remoteMessage.getData().get("phone"),
                remoteMessage.getData().get("message")));


        Objects.requireNonNull(notificationManager).notify(0, notification.build());

        assert notificationManager != null;
        notificationManager.notify(0, notification.build());

    }

}
