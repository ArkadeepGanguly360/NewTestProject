package com.example.testproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.testproject.Base.Url;
import com.example.testproject.Service.GPSTracker;
import com.example.testproject.Util.CheckConnectivity;
import com.example.testproject.Util.DialogView;
import com.example.testproject.Util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Context mContext;
    EditText et_emailid, et_password;
    Button btn_login;
    SessionManager mSessionManager;
    DialogView dialogView;
    String EmailId, Password;
    RequestQueue mQueue;
    private String TAG="LoginActivity";
    private GPSTracker gps;
    CheckConnectivity checkConnectivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialization();

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_login:

                if (checkConnectivity.isOnline(mContext)) {

                    if (validate()) {

                        login(EmailId, Password);
                    }
                }
                else {
                    Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initialization() {

        et_emailid = (EditText) findViewById(R.id.et_emailid);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        mContext = this;
        mQueue = Volley.newRequestQueue(this);
        mSessionManager = new SessionManager(mContext);
        dialogView = new DialogView();
        checkConnectivity = new CheckConnectivity();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            gps = new GPSTracker(mContext, LoginActivity.this);
        }

        getSupportActionBar().hide();
    }

    private void login(final String emailId, final String password) {

        dialogView.showCustomSpinProgress(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.urlLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);

                            mSessionManager.setEmailId(json.getString("email"));
                            mSessionManager.setUserId(json.getString("id"));
                            mSessionManager.setPassword(json.getString("password"));

                            Toast.makeText(mContext, "Login Successful", Toast.LENGTH_SHORT).show();
                            mSessionManager.setLogin(true);

                            Intent intent = new Intent(mContext,DashboardActivity.class);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        dialogView.dismissCustomSpinProgress();
                    }
                    }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialogView.dismissCustomSpinProgress();
                System.out.println("Something went wrong!");
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", emailId);
                params.put("password", password);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(60), 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        stringRequest.setTag(TAG);
        mQueue.add(stringRequest);
    }

    public boolean validate() {
        boolean valid = true;

        EmailId = et_emailid.getText().toString();
        Password = et_password.getText().toString();

        if (EmailId.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(EmailId).matches()) {
            et_emailid.setError("Enter a valid email address");
            valid = false;
        } else {
            et_emailid.setError(null);
        }

        if (Password.isEmpty()) {
            et_password.setError("Enter a valid password");
            valid = false;
        } else {
            et_password.setError(null);
        }
        return valid;
    }
}

