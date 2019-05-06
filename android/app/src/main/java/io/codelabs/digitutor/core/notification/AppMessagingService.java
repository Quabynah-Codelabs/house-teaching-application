package io.codelabs.digitutor.core.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.datasource.local.UserSharedPreferences;
import io.codelabs.digitutor.core.util.Constants;
import io.codelabs.digitutor.data.BaseUser;
import io.codelabs.digitutor.view.RequestDetailsActivity;
import io.codelabs.sdk.util.ExtensionUtils;

/**
 * Send and receive push notifications from the firebase messaging service
 */
public class AppMessagingService extends FirebaseMessagingService {

    public static final String CHANNEL_ID = "home_tutor_default_channel_id";
    public static final int RC_NOTIFICATION = 133;
    public static final int NOTIFICATION_ICON = R.drawable.shr_logo;
    public static final int NOTIFICATION_ID = (int) System.currentTimeMillis();
    public static final String TYPE_REQUEST = "tutor-request";


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sendRegistrationToServer(s);

        try {
            // Now compare the old token with the new one and send information to the database server
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new Instance ID token
                        if (task.getResult() != null) {
                            String token = task.getResult().getToken();
                            ExtensionUtils.debugLog(getApplicationContext(), String.format("Old token: %s. \nNew Token: %s", s, token));
                            if (!s.equals(token)) {
                                sendRegistrationToServer(token);
                            }
                        }
                    });
        } catch (Exception e) {
            ExtensionUtils.debugLog(getApplicationContext(), e.getLocalizedMessage());
        }
    }

    private void sendRegistrationToServer(String token) {
        UserSharedPreferences instance = UserSharedPreferences.getInstance(this);
        if (instance.isLoggedIn()) {
            String type = instance.getType();

            // Create a map of the new token and time updated
            HashMap<String, Object> hashMap = new HashMap<>(0);
            hashMap.put("token", token);
            hashMap.put("updatedAt", System.currentTimeMillis());

            // Send data to the database server
            FirebaseFirestore.getInstance().collection(type.equals(BaseUser.Type.PARENT) ? Constants.PARENTS : Constants.TUTORS)
                    .document(instance.getKey())
                    .update(hashMap)
                    .addOnCompleteListener(task -> {
                        ExtensionUtils.debugLog(getApplicationContext(), "Token updated");
                    }).addOnFailureListener(e -> {
                ExtensionUtils.debugLog(getApplicationContext(), e.getLocalizedMessage());
            });
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage == null) return;
        Map<String, String> data = remoteMessage.getData();
        createNotificationChannel(getString(R.string.default_notification_channel_id));
        if (data.containsKey("type")) {

            switch (Objects.requireNonNull(data.get("type"))) {
                case TYPE_REQUEST:
                    // Add data to intent
                    Intent i = new Intent(getApplicationContext(), RequestDetailsActivity.class);
                    i.putExtra(RequestDetailsActivity.EXTRA_REQUEST_ID, data.get("requestId"));
                    i.putExtra(RequestDetailsActivity.EXTRA_REQUEST_PARENT, data.get("parent"));

                    // Send notification to device
                    pushNotification(data.get("title"), data.get("message"), i);
                    break;
            }

        }
    }

    private void pushNotification(String title, String content, Intent intent) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), RC_NOTIFICATION, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Customize the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(NOTIFICATION_ICON)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVibrate(new long[]{0, 200, 0, 300})
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Push notification
        if (manager != null) manager.notify(NOTIFICATION_ID, builder.build());

    }

    private void createNotificationChannel(String channelName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String descriptionText = getString(R.string.channel_description);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(descriptionText);

            // Register the channel with the system
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
