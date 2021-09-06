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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.R;
import com.myginee.customer.adapter.AccServiceListAdapter;

import com.myginee.customer.model.AccServiceListModel;
import com.myginee.customer.model.OrderModel;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.GineePref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class AccMyserviceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    View view;
    Toolbar toolbar;
    private OrderModel orderViewModel;
    ArrayList<AccServiceListModel.Datum> serviceModelArrayList;
    ArrayList<AccServiceListModel.ServiceList> serviceModelServiceArrayList;
    AccServiceListAdapter serviceListAdapter;
    RecyclerView orderListCategory;
    ImageView imgBack;
    TextView tvTitle, tvNoData;
    private ProgressDialog mProgressDialog;

    public AccMyserviceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccMyserviceFragment.
     */
    //TODO: Rename and change types and number of parameters
    public static AccMyserviceFragment newInstance(String param1, String param2) {
        AccMyserviceFragment fragment = new AccMyserviceFragment();
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
        view = inflater.inflate(R.layout.fragment_my_order, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        imgBack = (ImageView) view.findViewById(R.id.imgBack);
        tvNoData = view.findViewById(R.id.tvNoData);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        orderListCategory = (RecyclerView) view.findViewById(R.id.rvOrderList);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText("YOUR SERVICES");
        orderListCategory.setHasFixedSize(true);
        orderListCategory.setNestedScrollingEnabled(false);
        serviceModelArrayList = new ArrayList<AccServiceListModel.Datum>();
        serviceModelServiceArrayList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        orderListCategory.setLayoutManager(linearLayoutManager);
        serviceListAdapter = new AccServiceListAdapter(getActivity(),
                AccMyserviceFragment.this, getActivity().getSupportFragmentManager(), serviceModelArrayList);
        orderListCategory.setAdapter(serviceListAdapter);

        if(!GineePref.getAccessToken(getActivity()).equals("")) {
            getAllOrderListFromAPI();
        }

        return view;
    }

    private void getAllOrderListFromAPI() {
        mProgressDialog.show();
        GineePref.getSharedPreferences(getActivity());
        Call<ResponseBody> getAllOrderListFromAPI = GineeAppApi.api().getAllServiceList(GineePref.getAccessToken(getActivity()));

        getAllOrderListFromAPI.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            JSONArray serviceArray = jsonObject.getJSONArray("data");
                            if (serviceArray.length() > 0) {
                                orderListCategory.setVisibility(View.VISIBLE);
                                tvNoData.setVisibility(View.GONE);
                                for (int i = 0; i < serviceArray.length(); i++) {
                                    JSONObject item = serviceArray.getJSONObject(i);
                                    serviceModelServiceArrayList = new ArrayList<>();
                                    JSONArray array = item.getJSONArray("service_list");
                                    if (array.length() > 0) {
                                        serviceModelServiceArrayList = new ArrayList<>();
                                        for (int j = 0; j < array.length(); j++) {
                                            JSONObject objService = array.getJSONObject(j);
                                            serviceModelServiceArrayList.add(new AccServiceListModel.ServiceList(
                                                    objService.getString("service_id"),
                                                    objService.getString("service_name"),
                                                    objService.getString("measurement"),
                                                    objService.getString("price")
                                            ));
                                        }
                                    }
                                    serviceModelArrayList.add(new AccServiceListModel.Datum(
                                            serviceModelServiceArrayList,
                                            item.getInt("total_price"),
                                            item.getInt("wallet_price"),
                                            item.getBoolean("is_active"),
                                            item.getBoolean("is_accepted"),
                                            item.getString("_id"),
                                            item.getString("category_id"),
                                            item.getString("subcategory_id"),
                                            item.getString("description"),
                                            item.getLong("service_provision_date"),
                                            item.getString("address"),
                                            item.getString("agent_id"),
                                            item.getString("created_at"),
                                            item.getString("updated_at"),
                                            item.getString("user_id"),
                                            item.getString("sub_category_name"),
                                            item.getString("sub_category_image_url")

                                    ));
                                }
                                serviceListAdapter.notifyDataSetChanged();
                            } else {
                                orderListCategory.setVisibility(View.GONE);
                                tvNoData.setVisibility(View.VISIBLE);
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
