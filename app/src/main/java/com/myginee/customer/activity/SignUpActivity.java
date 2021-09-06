package com.myginee.customer.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog mProgressDialog;
    EditText etMoNo, etName;
    TextView tvNext;
    String moNo, name;
    Toolbar toolbar;

    // Parse Network Response
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(SignUpActivity.this);
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();

        etName = findViewById(R.id.etName);
        etMoNo = findViewById(R.id.etMoNo);
        tvNext = findViewById(R.id.tvNext);


        tvNext.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvNext:
                if (localValidation()) {
//                    startActivity(new Intent(SignUpActivity.this, SignUpOTPActivity.class));
                    if (ConnectionDetector.getInstance().isConnectingToInternet(this)) {
                        //Call Login Api
                        mProgressDialog.show();
                        registerApiCall();
                    } else {
                        ConnectionDetector.getInstance().showNoInternetAlert(SignUpActivity.this);
                    }
                }
                break;

        }
    }

    private void registerApiCall() {

        Call<ResponseBody> userRegister = GineeAppApi.api().userRegister(name, "+91"+moNo,
                CommonMethod.getDeviceUniqueId(SignUpActivity.this), GineePref.getFCMToken(SignUpActivity.this));
        userRegister.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getBoolean("success") == true) {
                                    Log.d("header", response.headers().get("Access-Token")+"");
                                    GineePref.getSharedPreferences(SignUpActivity.this);
                                    GineePref.setAccessToken(SignUpActivity.this, response.headers().get("Access-Token") + "");
                                    Intent otpIntent = new Intent(SignUpActivity.this, SignUpOTPActivity.class);
                                    otpIntent.putExtra("access-token",response.headers().get("Access-Token")+"");
                                    otpIntent.putExtra("phone","+91"+moNo);
                                    startActivity(otpIntent);
                                    finish();
//                                    Toast.makeText(SignUpActivity.this, "register Successfully!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(SignUpActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(SignUpActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                            }

                }  else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(SignUpActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(SignUpActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean localValidation() {
        moNo = etMoNo.getText().toString().trim();
        name = etName.getText().toString().trim();
        if (name.equals("")) {
            etMoNo.setError(null);
            etName.setError(getResources().getString(R.string.user_name_error_message));
            etName.requestFocus();
            return false;
        }else if (moNo.equals("") || moNo.length() < 10) {
            etName.setError(null);
            etMoNo.setError(getResources().getString(R.string.user_phone_error_message));
            etMoNo.requestFocus();
            return false;
        } /*else if (!isValidEmail()) {
            emailEditText.setError(getResources().getString(R.string.user_email_validation_error_message));
            emailEditText.requestFocus();
            return false;
        }*/ else {
            etMoNo.setError(null);
            etName.setError(null);
            return true;
        }
    }

}