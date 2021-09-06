package com.myginee.customer.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.myginee.customer.HomeActivity;
import com.myginee.customer.R;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.CommonMethod;
import com.myginee.customer.utils.ConnectionDetector;
import com.myginee.customer.utils.GineePref;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ResetPassActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog mProgressDialog;
    EditText etPass, etOTP;
    TextView tvResetPass;
    String otp, pass, mo_no;
    Toolbar toolbar;

    // Parse Network Response
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        init();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(ResetPassActivity.this);
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        mo_no = getIntent().getStringExtra("phone");

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();

        GineePref.getSharedPreferences(ResetPassActivity.this);

        etPass = findViewById(R.id.etPass);
        etOTP = findViewById(R.id.etOTP);
        tvResetPass = findViewById(R.id.tvResetPass);


        tvResetPass.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvResetPass:
                if (localValidation()) {
//                    startActivity(new Intent(SignUpActivity.this, SignUpOTPActivity.class));
                    if (ConnectionDetector.getInstance().isConnectingToInternet(this)) {
                        //Call Login Api
                        mProgressDialog.show();
                        callAPIForGetOTP();
                    } else {
                        ConnectionDetector.getInstance().showNoInternetAlert(ResetPassActivity.this);
                    }
                }
                break;

        }
    }

    private void callAPIForGetOTP() {

        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("phone", mo_no);
            paramObject.put("password", pass);
            paramObject.put("otp", otp);

            RequestBody body =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramObject.toString());

            Call<ResponseBody> resetPassAPI = GineeAppApi.api().resetPassword(body);
            resetPassAPI.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mProgressDialog.dismiss();
                    if (response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getBoolean("success") == true) {
                                loginApiCall();
                            } else {
                                Toast.makeText(ResetPassActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ResetPassActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(ResetPassActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(ResetPassActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean localValidation() {
        pass = etPass.getText().toString().trim();
        otp = etOTP.getText().toString().trim();
        if (pass.equals("")) {
            etOTP.setError(null);
            etPass.setError(getResources().getString(R.string.user_pass_error_message));
            etPass.requestFocus();
            return false;
        } if (otp.equals("") || otp.length() < 6) {
            etPass.setError(null);
            etOTP.setError(getResources().getString(R.string.user_otp_error_message));
            etOTP.requestFocus();
            return false;
        } else {
            etOTP.setError(null);
            etPass.setError(null);
            return true;
        }
    }

    private void loginApiCall() {

        Call<ResponseBody> userLogin = GineeAppApi.api().userLogin(mo_no, pass,
                CommonMethod.getDeviceUniqueId(ResetPassActivity.this), GineePref.getFCMToken(ResetPassActivity.this));
        userLogin.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            GineePref.getSharedPreferences(ResetPassActivity.this);
                            GineePref.setStringValue(ResetPassActivity.this, mo_no);
                            GineePref.setAccessToken(ResetPassActivity.this, response.headers().get("Access-Token") + "");
                            GineePref.setRefreshToken(ResetPassActivity.this, response.headers().get("refresh-token") + "");
                            Intent otpIntent = new Intent(ResetPassActivity.this, HomeActivity.class);
                            otpIntent.putExtra("access-token", response.headers().get("Access-Token") + "");
                            startActivity(otpIntent);
                            finish();
                        } else {
                            Toast.makeText(ResetPassActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ResetPassActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ResetPassActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(ResetPassActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }


}