package com.myginee.customer.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.myginee.customer.HomeActivity;
import com.myginee.customer.R;
import com.myginee.customer.activity.SignInActivity;
import com.myginee.customer.adapter.BannerAdapter;
import com.myginee.customer.model.ProductSubCatSubModel;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.GineePref;
import com.viewpagerindicator.CirclePageIndicator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ProductDetailFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    View view;
    Toolbar toolbar;

    ViewPager vpProducts;
    CirclePageIndicator indicatorProduct;
    int quantity = 1;
    BannerAdapter bannerAdapter;
    ArrayList<String> sliders;

    private Handler handler;
    private Runnable runnable;
    int position = 0;
    int slideTimeOut = 5000;
    private ProgressDialog mProgressDialog;
    String sub_category_id = "", sub_category_name = "", product_id = "", _id = "";
    ArrayList<ProductSubCatSubModel.Data> productsubModelArrayList;

    TextView tvProductName, tvPrice, tvProductDesc;
    LinearLayout llCart, llWishList;
    Timer timer;

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductDetailFragment newInstance(String param1, String param2) {
        ProductDetailFragment fragment = new ProductDetailFragment();
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
        view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        Bundle bundle = getArguments();
        sub_category_name = bundle.getString("sub_category_name");
        sub_category_id = bundle.getString("sub_category_id");
        product_id = bundle.getString("product_id");
        _id = bundle.getString("_id");

        vpProducts = (ViewPager) view.findViewById(R.id.vpProducts);
        indicatorProduct = (CirclePageIndicator) view.findViewById(R.id.indicatorProduct);
        tvPrice = (TextView) view.findViewById(R.id.tvPrice);
        tvProductName = (TextView) view.findViewById(R.id.tvProductName);
        tvProductDesc = (TextView) view.findViewById(R.id.tvProductDesc);
        llWishList = (LinearLayout) view.findViewById(R.id.llWishList);
        llCart = (LinearLayout) view.findViewById(R.id.llCart);

        llCart.setOnClickListener(this);
        llWishList.setOnClickListener(this);

        productsubModelArrayList = new ArrayList<>();
        sliders = new ArrayList<>();
        setViewPager();

        toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        ((HomeActivity)getActivity()).setSupportActionBar(toolbar);
        if (((HomeActivity)getActivity()).getSupportActionBar() != null) {
            ((HomeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((HomeActivity)getActivity()).getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().onBackPressed();
                showHome();
            }
        });
        getProductsAPICall();
        return view;
    }

    public void showHome() {
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        while (fm.getBackStackEntryCount() > 3) {
            fm.popBackStackImmediate();
        }

    }

    private void setViewPager() {
        bannerAdapter = new BannerAdapter(getActivity(), sliders, getActivity().getSupportFragmentManager());
        vpProducts.setAdapter(bannerAdapter);
        indicatorProduct.setViewPager(vpProducts);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                vpProducts.post(new Runnable(){

                    @Override
                    public void run() {
                        if(sliders.size() > 0){
                            vpProducts.setCurrentItem((vpProducts.getCurrentItem()+1)%sliders.size());
                        }

                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 3000, 3000);

        try {
            runnable = new Runnable() {
                public void run() {
                    if (position >= sliders.size()) {
                        position = 0;
                    } else {
                        position = position + 1;
                    }
                    vpProducts.setCurrentItem(position, true);
                    handler.postDelayed(runnable, slideTimeOut);
                }
//                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProductsAPICall() {
        mProgressDialog.show();
        GineePref.getSharedPreferences(getActivity());
        Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().getProductSubID(/*GineePref.getAccessToken(getActivity()),*/
                _id);
        getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            JSONObject item = jsonObject.getJSONObject("data");
                            if (item != null) {
                                sliders = new ArrayList<>();
                                JSONArray array = item.getJSONArray("image_urls");
                                if(array.length() > 0) {
                                    for (int j = 0; j < array.length(); j++) {
                                        sliders.add(array.get(j).toString());
                                    }
                                    setViewPager();
                                }
                                productsubModelArrayList.add(new ProductSubCatSubModel.Data(
                                        sliders,
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
                                tvPrice.setText("Rs. "+item.getInt("price"));
                                tvProductName.setText(item.getString("name"));
                                tvProductDesc.setText(item.getString("description"));

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llCart :
                if (!GineePref.getAccessToken(getActivity()).equals("")) {
                    addProductToCart();
                }else {
                    Intent signOutIntent = new Intent(getActivity(), SignInActivity.class);
                    startActivity(signOutIntent);
//                    getActivity().finish();
                }

                break;

            case R.id.llWishList :
                if (!GineePref.getAccessToken(getActivity()).equals("")) {
                    addProductToWishList();
                }else {
                    Intent signOutIntent = new Intent(getActivity(), SignInActivity.class);
                    startActivity(signOutIntent);
//                    getActivity().finish();
                }

                break;
        }
    }

    private void addProductToWishList() {

            Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().addProductToWishList(GineePref.getAccessToken(getActivity()),
                    _id);
            getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mProgressDialog.dismiss();
                    if (response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getBoolean("success") == true) {

                                JSONObject item = jsonObject.getJSONObject("data");
                                if (item != null) {
                                    Toast.makeText(getActivity(), ""+jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                        }
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

    private void addProductToCart() {
        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("product_id", _id);
            paramObject.put("quantity", 1);
            RequestBody body =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramObject.toString());

            Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().addProductToCart(GineePref.getAccessToken(getActivity()),
                    body);
            getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mProgressDialog.dismiss();
                    if (response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getBoolean("success") == true) {

                                JSONObject item = jsonObject.getJSONObject("data");
                                if (item != null) {
                                    if (!GineePref.getAccessToken(getActivity()).equals("")) {
                                        HomeActivity.getAllCartProductsAPICall(getActivity());
                                    }
                                    Toast.makeText(getActivity(), ""+jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}