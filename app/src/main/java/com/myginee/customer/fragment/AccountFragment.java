package com.myginee.customer.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.myginee.customer.HomeActivity;
import com.myginee.customer.R;
import com.myginee.customer.activity.SignInActivity;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.GineePref;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import okio.ByteString;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class AccountFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView tvSignOut, tvProfile, tvAboutUs, tvOrder, tvWishList, tvService, tvPolicy, tvSignIn;
    View root;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ProgressDialog mProgressDialog;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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
        root = inflater.inflate(R.layout.fragment_account, container, false);
        tvSignOut = root.findViewById(R.id.tvSignOut);
        tvSignIn = root.findViewById(R.id.tvSignIn);
        tvProfile = root.findViewById(R.id.tvProfile);
        tvAboutUs = root.findViewById(R.id.tvAboutUs);
        tvPolicy = root.findViewById(R.id.tvPolicy);
        tvOrder = root.findViewById(R.id.tvOrder);
        tvWishList = root.findViewById(R.id.tvWishList);
        tvService = root.findViewById(R.id.tvService);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        GineePref.getSharedPreferences(getActivity());

        if (!GineePref.getAccessToken(getActivity()).equals("")) {
            tvSignIn.setVisibility(View.GONE);

            tvSignOut.setVisibility(View.VISIBLE);
            tvAboutUs.setVisibility(View.VISIBLE);
            tvPolicy.setVisibility(View.VISIBLE);
            tvOrder.setVisibility(View.VISIBLE);
            tvService.setVisibility(View.VISIBLE);
            tvWishList.setVisibility(View.VISIBLE);
            tvProfile.setVisibility(View.VISIBLE);
        } else {
            tvSignIn.setVisibility(View.VISIBLE);
            tvAboutUs.setVisibility(View.VISIBLE);
            tvPolicy.setVisibility(View.VISIBLE);

            tvSignOut.setVisibility(View.GONE);
            tvOrder.setVisibility(View.GONE);
            tvService.setVisibility(View.GONE);
            tvWishList.setVisibility(View.GONE);
            tvProfile.setVisibility(View.GONE);
        }
        tvSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogOutConfirmAlert();
            }
        });
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signOutIntent = new Intent(getActivity(), SignInActivity.class);
                startActivity(signOutIntent);
//                getActivity().finish();
            }
        });
        tvProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyProfileFragment profileFragment = new MyProfileFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, profileFragment)
                        .addToBackStack(profileFragment.getClass().getName()).commit();
            }
        });
        tvAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutUsFragment aboutUsFragment = new AboutUsFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, aboutUsFragment)
                        .addToBackStack(aboutUsFragment.getClass().getName()).commit();
            }
        });
        tvOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyOrderFragment myOrderFragment = new MyOrderFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, myOrderFragment)
                        .addToBackStack(myOrderFragment.getClass().getName()).commit();
            }
        });
        tvService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccMyserviceFragment myserviceFragment = new AccMyserviceFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, myserviceFragment)
                        .addToBackStack(myserviceFragment.getClass().getName()).commit();
            }
        });
        tvWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyWishListFragment wishListFragment = new MyWishListFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, wishListFragment)
                        .addToBackStack(wishListFragment.getClass().getName()).commit();
            }
        });
        tvPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PolicyFragment policyFragment = new PolicyFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, policyFragment)
                        .addToBackStack(policyFragment.getClass().getName()).commit();
            }
        });

        return root;
    }

    public void showLogOutConfirmAlert() {

        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder =
                new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(getResources().getString(R.string.logout_alert_title))
                .setMessage(getResources().getString(R.string.logout_alert_message))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.alert_ok_button_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mProgressDialog.show();
                                callSignoutAPI();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.alert_cancel_button_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // create alert dialog
        final androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button buttonPositive = alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
                buttonPositive.setTextColor(getResources().getColor(R.color.colorAccent));
                Button buttonNegative = alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE);
                buttonNegative.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });
        // show it
        alertDialog.show();
    }

    private void callSignoutAPI() {
        GineePref.getSharedPreferences(getActivity());
        Call<ResponseBody> signOutApi = GineeAppApi.api().signOut(GineePref.getRefreshToken(getActivity()));
        signOutApi.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            Toast.makeText(getActivity(), "Signout Successfully!", Toast.LENGTH_LONG).show();
                            GineePref.setAccessToken(getActivity(), "");
                            GineePref.setStringValue(getActivity(), "");
                            GineePref.setUSERID(getActivity(), "");
                            GineePref.setLng(getActivity(), "");
                            GineePref.setLat(getActivity(), "");
                            GineePref.setIsOrdered(getActivity(), false);
//                            GineePref.setFCMToken(getActivity(), "");
                            GineePref.setAddressLoc(getActivity(), "");
                            GineePref.setAddress(getActivity(), "");
                            GineePref.setRefreshToken(getActivity(), "");
                            GineePref.setAgent(getActivity(), "");
                            GineePref.setUSERName(getActivity(), "");
                            GineePref.setEmail(getActivity(), "");
                            GineePref.setWalletPrice(getActivity(), 0);
                            Intent signOutIntent = new Intent(getActivity(), HomeActivity.class);
                            startActivity(signOutIntent);
                            getActivity().finish();

                        } else {
                            Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }

                } else if (response.code() == 401) {
                    mProgressDialog.show();
                    callRefreshTokenAPI();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getActivity(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void callRefreshTokenAPI() {
        Call<ResponseBody> getOtpApi = GineeAppApi.api().accessTokenRefresh(ByteString.encodeUtf8(GineePref.getRefreshToken(getActivity())).base64());
        getOtpApi.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            GineePref.setAccessToken(getActivity(), response.headers().get("Access-Token") + "");
                            GineePref.setRefreshToken(getActivity(), response.headers().get("refresh-token") + "");
                            mProgressDialog.show();
                            callSignoutAPI();
                        } else {
                            Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }

                } else if (response.code() == 401) {
//                    callRefreshTokenAPI();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getActivity(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

}
