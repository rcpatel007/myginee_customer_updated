package com.myginee.customer.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.myginee.customer.HomeActivity;
import com.myginee.customer.R;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.ConnectionDetector;
import com.myginee.customer.utils.GineePref;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class RatingFragment extends Fragment {

    RatingBar ratingBar;
    TextView tvSendReview;
    View root;
    Toolbar toolbar;
    private ProgressDialog mProgressDialog;
    String order_id = "";

    public RatingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.dialog_review, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        Bundle bundle = getArguments();
        order_id = bundle.getString("orderId");

        Log.e("Rating onCreateView: ", order_id+"");

        ratingBar = root.findViewById(R.id.ratingBar);
        tvSendReview = root.findViewById(R.id.tvSendReview);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

//                txtRatingValue.setText(String.valueOf(rating));

            }
        });
        tvSendReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.getInstance().isConnectingToInternet(getActivity())) {
                    if (!GineePref.getAccessToken(getActivity()).equals("")) {

                        mProgressDialog.show();
                        callAPIForSendReview();
                    }
                }else {
                    ConnectionDetector.getInstance().showNoInternetAlert(getActivity());
                }
            }
        });
     /*((HomeActivity)getActivity()).setSupportActionBar(toolbar);
        if (((HomeActivity)getActivity()).getSupportActionBar() != null) {
            ((HomeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((HomeActivity)getActivity()).getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });*/
        return root;
    }


    private void callAPIForSendReview() {

        GineePref.getSharedPreferences(getActivity());
        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("type", "service");
            paramObject.put("item_id", order_id);
            paramObject.put("rating", ratingBar.getRating());
            paramObject.put("description", "awesome service !!");

            RequestBody body =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramObject.toString());

            Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().sendRating(GineePref.getAccessToken(getActivity()),
                    body);
            getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mProgressDialog.dismiss();
                    if (response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getBoolean("success") == true) {
                                Toast.makeText(getActivity(),"HEY, Thanks for your valuable feedback..", Toast.LENGTH_LONG).show();
                                Intent mainIntent = new Intent(getActivity(), HomeActivity.class);
                                startActivity(mainIntent);
                                getActivity().finish();
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


}
