package com.example.testproject.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckConnectivity {

    ConnectivityManager cm ;
    NetworkInfo netInfo ;

    public boolean isOnline(Context mContext) {
         cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
         netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

}
