package com.myginee.customer.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.myginee.customer.HomeActivity;
import com.myginee.customer.R;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.CommonMethod;
import com.myginee.customer.utils.ConnectionDetector;
import com.myginee.customer.utils.GineePref;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SignUpSetPassActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog mProgressDialog;
    EditText etPass, etConfPass;
    TextView tvDone;
    String pass, confPass, access_token = "", mo_no = "";

    // Parse Network Response
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_pass);
        init();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(SignUpSetPassActivity.this);
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        access_token = getIntent().getStringExtra("access-token");
        mo_no = getIntent().getStringExtra("phone");

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();

        etConfPass = findViewById(R.id.etConfPass);
        etPass = findViewById(R.id.etPass);
        tvDone = findViewById(R.id.tvDone);

        tvDone.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvDone:
                if (localValidation()) {
                    if (ConnectionDetector.getInstance().isConnectingToInternet(this)) {
                        //Call Login Api
                        mProgressDialog.show();
                        if(!GineePref.getAccessToken(SignUpSetPassActivity.this).equals("")) {
                            setPassApiCall();
                        }

                    } else {
                        ConnectionDetector.getInstance().showNoInternetAlert(SignUpSetPassActivity.this);
                    }
                }
                break;

        }
    }

    private void setPassApiCall() {

        Call<ResponseBody> savePasswordApi = GineeAppApi.api().savePassword(access_token, pass);
        savePasswordApi.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            loginApiCall();
                            /*startActivity(new Intent(SignUpSetPassActivity.this, HomeActivity.class));
                            finish();*/
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(SignUpSetPassActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        mProgressDialog.dismiss();
                        e.printStackTrace();
                        Toast.makeText(SignUpSetPassActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(SignUpSetPassActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(SignUpSetPassActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loginApiCall() {

        Call<ResponseBody> userLogin = GineeAppApi.api().userLogin(mo_no, pass,
                CommonMethod.getDeviceUniqueId(SignUpSetPassActivity.this), GineePref.getFCMToken(SignUpSetPassActivity.this));
        userLogin.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
//                            Toast.makeText(SignUpSetPassActivity.this, "login Successfully!", Toast.LENGTH_LONG).show();
                            GineePref.getSharedPreferences(SignUpSetPassActivity.this);
                            GineePref.setStringValue(SignUpSetPassActivity.this, mo_no);
                            GineePref.setAccessToken(SignUpSetPassActivity.this, response.headers().get("Access-Token") + "");
                            GineePref.setRefreshToken(SignUpSetPassActivity.this, response.headers().get("refresh-token") + "");
                            Intent otpIntent = new Intent(SignUpSetPassActivity.this, HomeActivity.class);
                            otpIntent.putExtra("access-token", response.headers().get("Access-Token") + "");
                            startActivity(otpIntent);
                            finish();
                        } else {
                            Toast.makeText(SignUpSetPassActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(SignUpSetPassActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SignUpSetPassActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(SignUpSetPassActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean localValidation() {
        pass = etPass.getText().toString().trim();
        confPass = etConfPass.getText().toString().trim();

        if (pass.equals("")) {
            etPass.setError(getResources().getString(R.string.user_pass_error_message));
            etPass.requestFocus();
            return false;
        } else if (confPass.equals("")) {
            etConfPass.setError(getResources().getString(R.string.user_conf_pass_error_message));
            etConfPass.requestFocus();
            return false;
        } else if (!pass.equals(confPass)) {
            Toast.makeText(SignUpSetPassActivity.this, "" + getResources().getString(R.string.user_conf_pass_match_error_message), Toast.LENGTH_LONG).show();
            return false;
        } else {
            etPass.setError(null);
            etConfPass.setError(null);
            return true;
        }
    }

}