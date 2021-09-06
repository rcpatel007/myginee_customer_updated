package com.myginee.customer.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.R;
import com.myginee.customer.adapter.WishListAdapter;
import com.myginee.customer.model.WishListModel;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.GineePref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class MyWishListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private WishListModel wishListViewModel;
    ArrayList<WishListModel.Datum> wishListModelArrayList;
    WishListAdapter wishListAdapter;
    RecyclerView wishListCategory;
    View root;
    ImageView imgBack;
    private ProgressDialog mProgressDialog;
    ArrayList<String> imgSliderList;
    TextView tvTitle, tvNoData;

    public MyWishListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyWishListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyWishListFragment newInstance(String param1, String param2) {
        MyWishListFragment fragment = new MyWishListFragment();
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
        root = inflater.inflate(R.layout.fragment_wish_list, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        wishListCategory = (RecyclerView) root.findViewById(R.id.rvWishList);
        imgBack = (ImageView) root.findViewById(R.id.imgBack);
        tvNoData = root.findViewById(R.id.tvNoData);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        wishListCategory.setHasFixedSize(true);
        wishListCategory.setNestedScrollingEnabled(false);
        wishListModelArrayList = new ArrayList<WishListModel.Datum>();
        imgSliderList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        wishListCategory.setLayoutManager(linearLayoutManager);
        wishListAdapter = new WishListAdapter(getActivity(),
                MyWishListFragment.this, getActivity().getSupportFragmentManager(), wishListModelArrayList);
        wishListCategory.setAdapter(wishListAdapter);
        if (!GineePref.getAccessToken(getActivity()).equals("")) {
            getAllWishListProductsAPICall();
        }


        return root;
    }



    private void getAllWishListProductsAPICall() {
        mProgressDialog.show();
        GineePref.getSharedPreferences(getActivity());
        Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().getWishListProducts(GineePref.getAccessToken(getActivity()));
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
                                wishListCategory.setVisibility(View.VISIBLE);
                                tvNoData.setVisibility(View.GONE);
                                for (int i = 0; i < serviceArray.length(); i++) {
                                    JSONObject item = serviceArray.getJSONObject(i);
                                    imgSliderList = new ArrayList<>();
                                    JSONArray array = item.getJSONArray("image_urls");
                                    if(array.length() > 0) {
                                        for (int j = 0; j < array.length(); j++) {
                                            imgSliderList.add(array.get(j).toString());
                                        }
                                    }
                                    wishListModelArrayList.add(new WishListModel.Datum(
                                            imgSliderList,
                                            item.getString("_id"),
                                            item.getString("name"),
                                            item.getString("image_url"),
                                            item.getString("description"),
                                            item.getInt("model_no"),
                                            item.getInt("price"),
                                            item.getInt("ratings"),
                                            item.getString("created_at"),
                                            item.getString("updated_at"),
                                            item.getString("product_category_id"),
                                            item.getString("product_sub_category_id")
                                    ));
                                }
                                wishListAdapter.notifyDataSetChanged();
                            }else {
                                wishListCategory.setVisibility(View.GONE);
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
