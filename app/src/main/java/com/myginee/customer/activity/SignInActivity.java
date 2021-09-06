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

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog mProgressDialog;
    EditText etMoNo, etPass;
    TextView tvSignIn, tvFgtPwd, tvSignUp;
    String moNo, pass;
    String TAG = "SignInActivity";
    // Parse Network Response
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(SignInActivity.this);
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();

        etMoNo = findViewById(R.id.etMoNo);
        etPass = findViewById(R.id.etPass);
        tvSignIn = findViewById(R.id.tvSignIn);
        tvFgtPwd = findViewById(R.id.tvFgtPwd);
        tvSignUp = findViewById(R.id.tvSignUp);

        tvSignIn.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        tvFgtPwd.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSignIn:
                if (localValidation()) {
                    if (ConnectionDetector.getInstance().isConnectingToInternet(this)) {
                        //Call Login Api
                        mProgressDialog.show();
                        loginApiCall();
                    } else {
                        ConnectionDetector.getInstance().showNoInternetAlert(SignInActivity.this);
                    }
                }
                break;

            case R.id.tvSignUp:
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                break;

            case R.id.tvFgtPwd:
                startActivity(new Intent(SignInActivity.this, ForgetPasswordActivity.class));
                break;
        }
    }

    private void loginApiCall() {

        Call<ResponseBody> userLogin = GineeAppApi.api().userLogin("+91" + moNo, pass,
                CommonMethod.getDeviceUniqueId(SignInActivity.this), GineePref.getFCMToken(SignInActivity.this));
        userLogin.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            JSONObject jsonCustomer = jsonObject.getJSONObject("customer");
                            Toast.makeText(SignInActivity.this, "Logged in Successfully", Toast.LENGTH_LONG).show();
                            GineePref.getSharedPreferences(SignInActivity.this);
                            GineePref.setStringValue(SignInActivity.this, "+91" + moNo);
                            GineePref.setEmail(SignInActivity.this, jsonCustomer.getString("email"));
                            GineePref.setUSERName(SignInActivity.this, jsonCustomer.getString("name"));
                            GineePref.setAccessToken(SignInActivity.this, response.headers().get("Access-Token") + "");
                            GineePref.setRefreshToken(SignInActivity.this, response.headers().get("refresh-token") + "");
                            GineePref.setUSERID(SignInActivity.this, jsonCustomer.getString("_id") + "");
                            GineePref.setWalletPrice(SignInActivity.this,
                                    jsonCustomer.getInt("wallet_amount"));
                            if (jsonCustomer.getJSONArray("address").length() > 0) {
                                GineePref.setAddress(SignInActivity.this,
                                        jsonCustomer.getJSONArray("address").get(0) + "");
                            }

//                            Intent otpIntent = new Intent(SignInActivity.this, HomeActivity.class);
//                            otpIntent.putExtra("access-token", response.headers().get("Access-Token") + "");
//                            startActivity(otpIntent);
                            finish();
                        } else {
                            Toast.makeText(SignInActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(SignInActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(SignInActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(SignInActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean localValidation() {
        moNo = etMoNo.getText().toString().trim();
        pass = etPass.getText().toString().trim();

        if (moNo.equals("") || moNo.length() < 10) {
            etMoNo.setError(getResources().getString(R.string.user_phone_error_message));
            etMoNo.requestFocus();
            return false;
        } /*else if (!isValidEmail()) {
            emailEditText.setError(getResources().getString(R.string.user_email_validation_error_message));
            emailEditText.requestFocus();
            return false;
        }*/ else if (pass.equals("")) {
            etMoNo.setError(null);
            etPass.setError(getResources().getString(R.string.user_pass_error_message));
            etPass.requestFocus();
            return false;
        } else {
            etMoNo.setError(null);
            etPass.setError(null);
            return true;
        }
    }

   /* @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    // release listener in onStop
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }*/

}