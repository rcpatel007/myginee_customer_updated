package com.myginee.customer.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.myginee.customer.R;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.ConnectionDetector;
import com.myginee.customer.utils.GineePref;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class AboutUsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    TextView tvDesc, tvTitle, tvScreenTitle;
    View view;
    Toolbar toolbar;
    ImageView imgBack, productImages;
    private ProgressDialog mProgressDialog;

    public AboutUsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutUsFragment.
     */
    //TODO: Rename and change types and number of parameters
        public static AboutUsFragment newInstance(String param1, String param2) {
            AboutUsFragment fragment = new AboutUsFragment();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putString(ARG_PARAM2, param2);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_about_us, container, false);

            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
            mProgressDialog.setCancelable(false);

            imgBack = (ImageView) view.findViewById(R.id.imgBack);
            productImages = (ImageView) view.findViewById(R.id.productImages);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvDesc = (TextView) view.findViewById(R.id.tvDesc);
            tvScreenTitle = (TextView) view.findViewById(R.id.tvScreenTitle);
            tvScreenTitle.setText("About Us");
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
            if (ConnectionDetector.getInstance().isConnectingToInternet(getActivity())) {
//                if (!GineePref.getAccessToken(getActivity()).equals("")) {
                    getAboutUsFromAPI();
//                }

            } else {
                ConnectionDetector.getInstance().showNoInternetAlert(getActivity());
            }

            return view;
        }

    private void getAboutUsFromAPI() {
        mProgressDialog.show();
        GineePref.getSharedPreferences(getActivity());
        Call<ResponseBody> getAboutUsCall = GineeAppApi.api().aboutUs(/*GineePref.getAccessToken(getActivity())*/);
        getAboutUsCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            JSONArray serviceArray = jsonObject.getJSONArray("data");
                            if (serviceArray.length() > 0) {
                                for (int i = 0; i < serviceArray.length(); i++) {
                                    JSONObject item = serviceArray.getJSONObject(i);
                                    tvTitle.setText(item.getString("title"));
                                    tvDesc.setText(item.getString("description"));
                                    Picasso.with(getActivity())
                                            .load(item.getString("image_url"))
                                            .placeholder(R.drawable.banner)
                                            .error(R.drawable.banner)
                                            .into(productImages);
                                }
                            }

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
    }

}
