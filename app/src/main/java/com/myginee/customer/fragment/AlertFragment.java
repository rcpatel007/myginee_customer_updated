package com.myginee.customer.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.R;
import com.myginee.customer.adapter.NotifyAdapter;
import com.myginee.customer.model.AlertDataModel;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.ConnectionDetector;
import com.myginee.customer.utils.GineePref;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    View view;
    Toolbar toolbar;
    ImageView imgBack;
    private ProgressDialog mProgressDialog;
    private static final String TAG = "NotificationActivity";
    ArrayList<AlertDataModel.Datum> notificationArrayList;
    NotifyAdapter notifyAdapter;
    RecyclerView serviceListCategory;
    TextView tvNotFound;

    public AlertFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlertFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlertFragment newInstance(String param1, String param2) {
        AlertFragment fragment = new AlertFragment();
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
        view = inflater.inflate(R.layout.fragment_alert, container, false);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        imgBack = view.findViewById(R.id.imgBack);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        GineePref.getSharedPreferences(getActivity());

        serviceListCategory = view.findViewById(R.id.rvNotiList);
        tvNotFound = view.findViewById(R.id.tvNotiNotFound);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        serviceListCategory.setHasFixedSize(true);
        serviceListCategory.setNestedScrollingEnabled(false);
        notificationArrayList = new ArrayList<>();
        if (!GineePref.getAccessToken(getActivity()).equals("")) {
            getAlertsFromApi();
        } else {
            tvNotFound.setVisibility(View.VISIBLE);
            serviceListCategory.setVisibility(View.GONE);
        }




       /* for(int i = 0; i < 3; i++){
            com.example.mygenineandroid.model.Notification notification = new
                    com.example.mygenineandroid.model.Notification("completed",
                    "description",
                    "service_provision_date",
                    "sub_category_image_url","",
                    "completed");
            notificationArrayList.add(notification);
        }*/


       /* if(GineePref.getArrayList(getActivity()) != null){
            notificationArrayList = GineePref.getArrayList(getActivity());
            tvNotFound.setVisibility(View.GONE);
            serviceListCategory.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,
                    false);
            serviceListCategory.setLayoutManager(linearLayoutManager);
            notifyAdapter = new NotifyAdapter(getActivity(), getActivity(), notificationArrayList);
            serviceListCategory.setAdapter(notifyAdapter);

        }else {
            tvNotFound.setVisibility(View.VISIBLE);
            serviceListCategory.setVisibility(View.GONE);
        }*/

        return view;
    }

    private void getAlertsFromApi() {
        mProgressDialog.show();
        GineePref.getSharedPreferences(getActivity());
        Call<JsonElement> getAboutUsCall = GineeAppApi.api().alerts(GineePref.getAccessToken(getActivity()));
        getAboutUsCall.enqueue(new retrofit2.Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        Log.e(TAG, "onResponse: " + response.body().toString());
                        AlertDataModel alertDataModel = new Gson().fromJson(response.body(), AlertDataModel.class);
                        if (alertDataModel.getSuccess()) {
                            List<AlertDataModel.Datum> data = alertDataModel.getData();
                            if (data.size() > 0) {
                                tvNotFound.setVisibility(View.GONE);
                                serviceListCategory.setVisibility(View.VISIBLE);
                                for (int i = 0; i < data.size(); i++) {
                                    AlertDataModel.Datum model = data.get(i);
                                    if (model.getOrder() != null) {
                                        AlertDataModel.Datum notification = new AlertDataModel.Datum(
                                                model.getType(),
                                                model.getId(), model.getCreatedAt(), model.getUpdatedAt(),
                                                model.getOrderId(),
                                                model.getCustomerId(),
                                                model.getAgentId(),
                                                model.getOrder(),
                                                model.getTitle(), model.getDescription());
                                        notificationArrayList.add(notification);
                                    }


                                }
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,
                                        false);
                                serviceListCategory.setLayoutManager(linearLayoutManager);
                                notifyAdapter = new NotifyAdapter(getActivity(), getActivity(), notificationArrayList);
                                serviceListCategory.setAdapter(notifyAdapter);
                            } else {
                                tvNotFound.setVisibility(View.VISIBLE);
                                serviceListCategory.setVisibility(View.GONE);
                            }

                        } else {
                            Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
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
            public void onFailure(Call<JsonElement> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

}
