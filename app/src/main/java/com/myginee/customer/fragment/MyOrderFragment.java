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
import com.myginee.customer.adapter.OrderListAdapter;
import com.myginee.customer.model.OrderModel;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.GineePref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class MyOrderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    View view;
    Toolbar toolbar;
    private OrderModel orderViewModel;
    ArrayList<OrderModel.Datum> OrderModelArrayList;
    OrderListAdapter orderListAdapter;
    RecyclerView orderListCategory;
    ImageView imgBack;
    private ProgressDialog mProgressDialog;
    ArrayList<String> imgSliderList;
    TextView tvTitle, tvNoData;
    ArrayList<OrderModel.ProductList> productArrayList;

    public MyOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyOrderFragment.
     */
    //TODO: Rename and change types and number of parameters
    public static MyOrderFragment newInstance(String param1, String param2) {
        MyOrderFragment fragment = new MyOrderFragment();
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
        orderListCategory.setHasFixedSize(true);
        orderListCategory.setNestedScrollingEnabled(false);
        OrderModelArrayList = new ArrayList<OrderModel.Datum>();
        imgSliderList = new ArrayList<>();
        productArrayList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        orderListCategory.setLayoutManager(linearLayoutManager);
        orderListAdapter = new OrderListAdapter(getActivity(),
                MyOrderFragment.this, getActivity().getSupportFragmentManager(), OrderModelArrayList);
        orderListCategory.setAdapter(orderListAdapter);

        if (!GineePref.getAccessToken(getActivity()).equals("")) {
            getAllOrderListFromAPI();
        }


        return view;
    }

    private void getAllOrderListFromAPI() {
        mProgressDialog.show();
        GineePref.getSharedPreferences(getActivity());
        Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().getAllOrdersList(GineePref.getAccessToken(getActivity()));
        getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
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
                                    imgSliderList = new ArrayList<>();
                                    if (item.getJSONArray("product_list").length() > 0) {
                                        productArrayList = new ArrayList<>();
                                        for (int k = 0; k < item.getJSONArray("product_list").length(); k++) {
                                            JSONObject objService = item.getJSONArray("product_list").getJSONObject(k);
                                            productArrayList.add(new OrderModel.ProductList(objService.getString("_id"),
                                                    Integer.parseInt(objService.getString("price")),
                                                    objService.getString("name"), objService.getInt("quantity")));
                                        }
                                    }
                                    JSONArray array = item.getJSONArray("product_list").getJSONObject(0).getJSONArray("image_urls");
                                    boolean isDispatched = item.getBoolean("is_dispatched");
                                    if(array.length() > 0) {
                                        for (int j = 0; j < array.length(); j++) {
                                            imgSliderList.add(array.get(j).toString());
                                        }
                                    }
                                    OrderModelArrayList.add(new OrderModel.Datum(productArrayList,
                                            imgSliderList,
                                            item.getJSONArray("product_list").getJSONObject(0).getString("_id"),
                                            item.getJSONArray("product_list").getJSONObject(0).getString("name"),
                                            item.getJSONArray("product_list").getJSONObject(0).getString("image_url"),
                                            item.getJSONArray("product_list").getJSONObject(0).getString("description"),
                                            item.getJSONArray("product_list").getJSONObject(0).getString("model_no"),
                                            item.getJSONArray("product_list").getJSONObject(0).getInt("price"),
                                            item.getJSONArray("product_list").getJSONObject(0).getInt("ratings"),
                                            item.getString("created_at"),
                                            item.getString("updated_at"),
                                            item.getJSONArray("product_list").getJSONObject(0).getString("product_category_id"),
                                            item.getJSONArray("product_list").getJSONObject(0).getString("product_sub_category_id"),
                                            isDispatched, item.getString("address")
                                    ));

                                }
                                orderListAdapter.notifyDataSetChanged();
                            }else {
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
                Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
            }
        });
    }



}
