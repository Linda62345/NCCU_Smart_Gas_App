package glotech.smartgasapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences("login_data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setSessionTimeout(long timeoutMillis) {
        long currentTimeMillis = System.currentTimeMillis();
        long timeoutTimestamp = currentTimeMillis + timeoutMillis;
        editor.putLong("session_timeout", timeoutTimestamp);
        editor.apply();
    }


    public void clearSessionData() {
        editor.remove("session_timeout");
        editor.apply();
    }
    public boolean isSessionTimedOut() {
        long currentTimeMillis = System.currentTimeMillis();
        long timeoutTimestamp = sharedPreferences.getLong("session_timeout", 0); // 0 means no timestamp found
        return currentTimeMillis >= timeoutTimestamp;
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    public void setLoggedInStatus(boolean isLoggedIn) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }
}

