package io.codelabs.digitutor.core.notification;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

import io.codelabs.digitutor.core.datasource.local.UserSharedPreferences;
import io.codelabs.digitutor.core.util.Constants;
import io.codelabs.digitutor.data.BaseUser;
import io.codelabs.sdk.util.ExtensionUtils;

/**
 * Send and receive push notifications from the firebase messaging service
 */
public class AppMessagingService extends FirebaseMessagingService {

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


    }


}
