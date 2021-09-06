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

import com.myginee.customer.R;
import com.myginee.customer.net.GineeAppApi;
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

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {
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
        mProgressDialog = new ProgressDialog(ForgetPasswordActivity.this);
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();

        GineePref.getSharedPreferences(ForgetPasswordActivity.this);

        etName = findViewById(R.id.etName);
        etName.setVisibility(View.GONE);
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
                        callAPIForGetOTP();
                    } else {
                        ConnectionDetector.getInstance().showNoInternetAlert(ForgetPasswordActivity.this);
                    }
                }
                break;

        }
    }

    private void callAPIForGetOTP() {

        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("phone", "+91"+moNo);

            RequestBody body =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramObject.toString());

//        Call<ResponseBody> userRegister = GineeAppApi.api().forgetPassword("+91"+moNo);
        Call<ResponseBody> userRegister = GineeAppApi.api().forgetPassword(body);
        userRegister.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            Intent otpIntent = new Intent(ForgetPasswordActivity.this, ResetPassActivity.class);
                            otpIntent.putExtra("access-token",response.headers().get("Access-Token")+"");
                            otpIntent.putExtra("phone","+91"+moNo);
                            startActivity(otpIntent);
                            finish();
                        } else {
                            Toast.makeText(ForgetPasswordActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(ForgetPasswordActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean localValidation() {
        moNo = etMoNo.getText().toString().trim();
        if (moNo.equals("") || moNo.length() < 10) {
            etMoNo.setError(getResources().getString(R.string.user_phone_error_message));
            etMoNo.requestFocus();
            return false;
        }  else {
            etMoNo.setError(null);
            return true;
        }
    }

}