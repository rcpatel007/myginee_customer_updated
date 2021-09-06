package com.myginee.customer.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.myginee.customer.R;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.ConnectionDetector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myginee.customer.utils.GineePref;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SignUpOTPActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog mProgressDialog;
    EditText etOTP;
    TextView tvOTPNext;
    String otp, access_token = "", mo_no = "";

    // Parse Network Response
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_otp);
        init();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(SignUpOTPActivity.this);
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        access_token = getIntent().getStringExtra("access-token");
        mo_no = getIntent().getStringExtra("phone");

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();

        etOTP = findViewById(R.id.etOTP);
        tvOTPNext = findViewById(R.id.tvOTPNext);

        tvOTPNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvOTPNext :
                if (localValidation()) {
                    if (ConnectionDetector.getInstance().isConnectingToInternet(this)) {
                        //Call Login Api
                        mProgressDialog.show();
                        if(!GineePref.getAccessToken(SignUpOTPActivity.this).equals("")) {
                            getOTPApiCall();
                        }

                    } else {
                        ConnectionDetector.getInstance().showNoInternetAlert(SignUpOTPActivity.this);
                    }
                }
                break;

        }
    }

    private void getOTPApiCall() {

        Call<ResponseBody> getOtpApi = GineeAppApi.api().getOTPOnRegister(access_token, otp);
        getOtpApi.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getBoolean("success") == true) {
                                    Intent otpIntent = new Intent(SignUpOTPActivity.this, SignUpSetPassActivity.class);
                                    otpIntent.putExtra("access-token",access_token+"");
                                    otpIntent.putExtra("phone",mo_no+"");
                                    startActivity(otpIntent);
                                    finish();
//                                    Toast.makeText(SignUpOTPActivity.this, "register Successfully!", Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(SignUpOTPActivity.this, ""+jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(SignUpOTPActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                            }

                } else {
                    Toast.makeText(SignUpOTPActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(SignUpOTPActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean localValidation() {
        otp = etOTP.getText().toString().trim();

        if (otp.equals("") || otp.length() < 6) {
            etOTP.setError(getResources().getString(R.string.user_otp_error_message));
            etOTP.requestFocus();
            return false;
        }else {
            etOTP.setError(null);
            return true;
        }
    }

}