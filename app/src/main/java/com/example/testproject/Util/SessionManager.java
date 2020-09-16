package com.example.testproject.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class SessionManager {

    private static SessionManager sessionManager;

    //TODO: Shared Preferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    //TODO: Shared pref mode
    int PRIVATE_MODE = 0;

    //TODO: Shared preferences file name
    private static final String PREF_NAME = "fgm";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    private static final String EMAILID = "emailid" ;
    private static final String USERID = "userid" ;
    private static final String PASSWORD = "password" ;


    private static String TAG = SessionManager.class.getSimpleName();

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static SessionManager getInstance(Context context) {
        if (sessionManager == null) {
            sessionManager = new SessionManager(context);
        }
        return sessionManager;
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn() {

        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void setEmailId(String emailId) {
        editor.putString(EMAILID, emailId);
        editor.commit();
    }

    public String getEmailId() {

        return pref.getString(EMAILID, "");
    }

    public void setUserId(String userId) {
        editor.putString(USERID, userId);
        editor.commit();
    }

    public String getUserId() {

        return pref.getString(USERID, "");
    }

    public void setPassword(String userId) {
        editor.putString(PASSWORD, userId);
        editor.commit();
    }

    public String getPassword() {

        return pref.getString(PASSWORD, "");
    }

}