package com.myginee.customer.activity;

import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.myginee.customer.MapsActivity;
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

public class ChangePasswordFragment extends Fragment implements View.OnClickListener {

    private ProgressDialog mProgressDialog;
    EditText etOldPass, etNewPass;
    TextView tvChangePass;
    String oldPass, newPass, access_token = "";

    // Parse Network Response
    private Gson gson;
    View root;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_change_password, container, false);
        init();
        return root;
    }

    private void init() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();

        GineePref.getSharedPreferences(getActivity());

        etNewPass = root.findViewById(R.id.etNewPass);
        etOldPass = root.findViewById(R.id.etOldPass);
        tvChangePass = root.findViewById(R.id.tvChangePass);

        tvChangePass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvChangePass:
                if (localValidation()) {
                    if (ConnectionDetector.getInstance().isConnectingToInternet(getActivity())) {
                        //Call Login Api
                        mProgressDialog.show();
                        if(!GineePref.getAccessToken(getActivity()).equals("")) {
                            changePassApiCall();
                        }

                    } else {
                        ConnectionDetector.getInstance().showNoInternetAlert(getActivity());
                    }
                }
                break;

        }
    }

    private void changePassApiCall() {

        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("old_password", oldPass);
            paramObject.put("new_password", newPass);

            RequestBody body =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramObject.toString());
        Call<ResponseBody> savePasswordApi = GineeAppApi.api().changePassByID(GineePref.getAccessToken(getActivity()),
                GineePref.getUSERID(getActivity()), body );
        savePasswordApi.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            Toast.makeText(getActivity(), "Password changed Successfully!", Toast.LENGTH_LONG).show();
                            GineePref.setAccessToken(getActivity(), "");
                            GineePref.setStringValue(getActivity(), "");
                            GineePref.setUSERID(getActivity(), "");
                            Intent signOutIntent = new Intent(getActivity(), SignInActivity.class);
                            startActivity(signOutIntent);
                            getActivity().finish();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        mProgressDialog.dismiss();
                        e.printStackTrace();
                        Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean localValidation() {
        oldPass = etOldPass.getText().toString().trim();
        newPass = etNewPass.getText().toString().trim();

        if (oldPass.equals("")) {
            etNewPass.setError(null);
            etOldPass.setError(getResources().getString(R.string.user_pass_error_message));
            etOldPass.requestFocus();
            return false;
        } else if (newPass.equals("")) {
            etOldPass.setError(null);
            etNewPass.setError(getResources().getString(R.string.user_conf_pass_error_message));
            etNewPass.requestFocus();
            return false;
        } else {
            etOldPass.setError(null);
            etNewPass.setError(null);
            return true;
        }
    }

}