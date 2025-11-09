package com.group3.application.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class PreferenceManager {
    private static final String PREF_NAME = "cafe_shop_prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";

    private static SharedPreferences getEncryptedPreferences(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            return EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            // Fallback to standard SharedPreferences
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = getEncryptedPreferences(context);
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = getEncryptedPreferences(context);
        return prefs.getString(KEY_TOKEN, null);
    }

    public static void saveUserRole(Context context, String role) {
        SharedPreferences prefs = getEncryptedPreferences(context);
        prefs.edit().putString(KEY_USER_ROLE, role).apply();
    }

    public static String getUserRole(Context context) {
        SharedPreferences prefs = getEncryptedPreferences(context);
        return prefs.getString(KEY_USER_ROLE, null);
    }

    public static void saveUserId(Context context, String userId) {
        SharedPreferences prefs = getEncryptedPreferences(context);
        prefs.edit().putString(KEY_USER_ID, userId).apply();
    }

    public static String getUserId(Context context) {
        SharedPreferences prefs = getEncryptedPreferences(context);
        return prefs.getString(KEY_USER_ID, null);
    }

    public static void saveUserName(Context context, String name) {
        SharedPreferences prefs = getEncryptedPreferences(context);
        prefs.edit().putString(KEY_USER_NAME, name).apply();
    }

    public static String getUserName(Context context) {
        SharedPreferences prefs = getEncryptedPreferences(context);
        return prefs.getString(KEY_USER_NAME, null);
    }

    public static void saveUserEmail(Context context, String email) {
        SharedPreferences prefs = getEncryptedPreferences(context);
        prefs.edit().putString(KEY_USER_EMAIL, email).apply();
    }

    public static String getUserEmail(Context context) {
        SharedPreferences prefs = getEncryptedPreferences(context);
        return prefs.getString(KEY_USER_EMAIL, null);
    }

    public static void clearSession(Context context) {
        SharedPreferences prefs = getEncryptedPreferences(context);
        prefs.edit().clear().apply();
    }

    public static boolean isLoggedIn(Context context) {
        return getToken(context) != null;
    }
}
