package com.example.testproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.example.testproject.Util.SessionManager;

public class SplashActivity extends AppCompatActivity {

    Context mContext;
    TextView textView;
    SessionManager mSessionManager;
    private static int SPLASH_TIME_OUT = 2000;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initialization();
        splashTime();
    }

    private void initialization() {

        textView=(TextView)findViewById(R.id.textView);
        mContext = this;
        mSessionManager = new SessionManager(mContext);

        getSupportActionBar().hide();
    }

    private void splashTime() {

        try{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (mSessionManager.isLoggedIn()) {
                        Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }

            }, SPLASH_TIME_OUT);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
