package io.codelabs.digitutor.core.datasource.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import io.codelabs.digitutor.core.util.Constants;
import io.codelabs.digitutor.data.BaseUser;
import kotlin.jvm.Synchronized;
import kotlin.jvm.Volatile;

/**
 * Stores user key and type
 */
public class UserSharedPreferences {
    @Volatile
    private static UserSharedPreferences instance = null;
    private final SharedPreferences prefs;
    private boolean isLoggedIn = false;
    private String key, type;

    @Synchronized
    public static UserSharedPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new UserSharedPreferences(context);
        }
        return instance;
    }

    private UserSharedPreferences(@NotNull Context context) {
        this.prefs = context.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);

        // Get fields
        key = prefs.getString(Constants.USER_KEY, null);
        type = prefs.getString(Constants.USER_TYPE, BaseUser.Type.PARENT);

        isLoggedIn = key != null && !TextUtils.isEmpty(key);

        if (isLoggedIn) {
            key = prefs.getString(Constants.USER_KEY, null);
            type = prefs.getString(Constants.USER_TYPE, BaseUser.Type.PARENT);
        }
    }

    /**
     * Handles login locally
     */
    public void login(String key, String type) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.USER_KEY, key);
        editor.putString(Constants.USER_TYPE, type);
        editor.apply();
        isLoggedIn = key != null && !TextUtils.isEmpty(key);
    }

    /**
     * Handles logout locally
     */
    public void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.USER_KEY, null);
        editor.putString(Constants.USER_TYPE, null);
        editor.apply();
        isLoggedIn = false;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }
}
