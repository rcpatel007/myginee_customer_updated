package com.myginee.customer.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.myginee.customer.R;
import com.myginee.customer.activity.ChangePasswordFragment;
import com.myginee.customer.activity.SignUpOTPActivity;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.ConnectionDetector;
import com.myginee.customer.utils.GineePref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class MyProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView tvSignOut, tvProfile, tvChangePass, tvSaveChanges;
    EditText etCustomerName, etFCM, etEmail;
    View root;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ProgressDialog mProgressDialog;
    ImageView imgBack;
    String name, email, profile_image_url, address;
    public MyProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_profile, container, false);
//        tvSignOut = root.findViewById(R.id.tvSignOut);
//        tvProfile = root.findViewById(R.id.tvProfile);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);


        imgBack = (ImageView) root.findViewById(R.id.imgBack);
        etCustomerName = (EditText) root.findViewById(R.id.etCustomerName);
//        etMoNo = (EditText) root.findViewById(R.id.etMoNo);
        etEmail = (EditText) root.findViewById(R.id.etEmail);
        etFCM = (EditText) root.findViewById(R.id.etFCM);
        tvChangePass = (TextView) root.findViewById(R.id.tvChangePass);
        tvSaveChanges = (TextView) root.findViewById(R.id.tvSaveChanges);

        etFCM.setText(GineePref.getFCMToken(getActivity()));
        etEmail.setText(GineePref.getEmail(getActivity()));

        tvSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(localValidation()){
                    if (ConnectionDetector.getInstance().isConnectingToInternet(getActivity())) {
                        //Call Login Api
                        mProgressDialog.show();
                        if(!GineePref.getAccessToken(getActivity()).equals("")) {
                            updateCustomerByID();
                        }

                    } else {
                        ConnectionDetector.getInstance().showNoInternetAlert(getActivity());
                    }

                }
            }
        });
        tvChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordFragment changePassFragment = new ChangePasswordFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, changePassFragment)
                        .addToBackStack(changePassFragment.getClass().getName()).commit();
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        if(!GineePref.getAccessToken(getActivity()).equals("")) {
            getUserBYID();
        }


        return root;
    }

    private void updateCustomerByID() {

        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("name", name);
            paramObject.put("email", email);
            paramObject.put("profile_image_url", profile_image_url);
            paramObject.put("address", address);

            RequestBody body =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramObject.toString());

            Call<ResponseBody> resetPassAPI = GineeAppApi.api().updateCustomer(GineePref.getAccessToken(getActivity()),
                    GineePref.getUSERID(getActivity()),body);
            resetPassAPI.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mProgressDialog.dismiss();
                    if (response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getBoolean("success") == true) {
//                                GineePref.setStringValue(getActivity(), phone);
                                GineePref.setEmail(getActivity(), email);
                                Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                        }

                    } else {
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

    private void getUserBYID() {

            Call<ResponseBody> userRegister = GineeAppApi.api().getUserByID(GineePref.getAccessToken(getActivity()),
                    GineePref.getUSERID(getActivity()));
            userRegister.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mProgressDialog.dismiss();
                    if (response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getBoolean("success") == true) {
                                JSONObject jsonData = jsonObject.getJSONObject("data");
                                etCustomerName.setText(jsonData.getString("name"));
//                                etMoNo.setText(jsonData.getString("phone"));
//                                {"email":"test@gmail.com",
//                                "password":"$2b$08$aaJmE8yGBuPF1Pe5yq6rhOCP3MsfRE2v7tCKQcvXRgUXeFa5WlVq2",
//                                "profile_image_url":"","address":[null],"is_active":true,
//                                "wallet_amount":10,"_id":"5f6b80011e0f430017201275",
//                                "name":"customer1","phone":"+917777777777",
//                                "created_at":"2020-09-23T17:04:01.703Z","updated_at":"2020-12-07T16:15:34.451Z","__v":0}
                                email = jsonData.getString("email");
//                                phone = jsonData.getString("phone");
                                name = jsonData.getString("name");
                                GineePref.setWalletPrice(getActivity(),
                                        jsonData.getInt("wallet_amount") );
//                                profile_image_url = jsonData.getString("profile_image_url");
                                JSONArray addArray = jsonData.getJSONArray("address");
                                /*if(addArray.length() > 0) {
                                    address = addArray.getJSONObject(0).toString();
                                }*/
                            } else {
                                Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            });

    }

    private boolean localValidation() {
        name = etCustomerName.getText().toString().trim();
//        phone = etMoNo.getText().toString().trim();
        email = etEmail.getText().toString().trim();

        if (name.equals("")) {
//            etMoNo.setError(null);
            etEmail.setError(null);
            etCustomerName.setError(getResources().getString(R.string.user_name_error_message));
            etCustomerName.requestFocus();
            return false;
        } /*else if (phone.equals("")) {
            etCustomerName.setError(null);
            etEmail.setError(null);
            etMoNo.setError(getResources().getString(R.string.user_phone_error_message));
            etMoNo.requestFocus();
            return false;
        } */else if (email.equals("")) {
            etCustomerName.setError(null);
//            etMoNo.setError(null);
            etEmail.setError(getResources().getString(R.string.email_error_message));
            etEmail.requestFocus();
            return false;
        } else {
            etCustomerName.setError(null);
//            etMoNo.setError(null);
            etEmail.setError(null);
            return true;
        }
    }


}
