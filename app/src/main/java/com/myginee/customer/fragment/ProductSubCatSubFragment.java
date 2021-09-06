package com.myginee.customer.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.R;
import com.myginee.customer.adapter.ProductSubCatSubAdapter;
import com.myginee.customer.model.ProductModel;
import com.myginee.customer.model.ProductSubCatSubModel;
import com.myginee.customer.model.WishListModel;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.GineePref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ProductSubCatSubFragment extends Fragment {
    private static final String TAG = "ProductSubCatSubFragment";
    private ProductModel productViewModel;
    ArrayList<ProductSubCatSubModel.Data> productsubModelArrayList;
    ArrayList<WishListModel.Datum> wishListModelArrayList;
    ArrayList<String> imgSliderList;
    ProductSubCatSubAdapter productListAdapter;
    RecyclerView productListCategory;
    View root;
    private ProgressDialog mProgressDialog;
    EditText etSearchProd;
    String sub_category_id = "", sub_category_name = "", service_id = "", search_text = "";
    Activity a;

    public ProductSubCatSubFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment ProductSubCatSubFragment.
     */
    public static ProductSubCatSubFragment newInstance() {
        return new ProductSubCatSubFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_products, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        GineePref.getSharedPreferences(getActivity());

        Bundle bundle = getArguments();

        productListCategory = (RecyclerView) root.findViewById(R.id.rvProductsList);
        etSearchProd = (EditText) root.findViewById(R.id.etSearchProd);
        productListCategory.setHasFixedSize(true);
        productListCategory.setNestedScrollingEnabled(false);
        productsubModelArrayList = new ArrayList<ProductSubCatSubModel.Data>();
        wishListModelArrayList = new ArrayList<WishListModel.Datum>();

        LinearLayoutManager subproductLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        productListCategory.setLayoutManager(subproductLayoutManager);
        productListAdapter = new ProductSubCatSubAdapter(getActivity(),
                ProductSubCatSubFragment.this, getActivity().getSupportFragmentManager(), productsubModelArrayList, wishListModelArrayList);
        productListCategory.setAdapter(productListAdapter);

        if(bundle.getString("search_text") != null){
            search_text = bundle.getString("search_text");
            if(!GineePref.getAccessToken(getActivity()).equals("")) {
                getSearchProductsAPICall();
            }

        }else {
            sub_category_name = bundle.getString("sub_category_name");
            sub_category_id = bundle.getString("sub_category_id");
            service_id = bundle.getString("service_id");
            getSubCatProductsAPICall();

        }


        return root;
    }

    private void getSubCatProductsAPICall() {
        mProgressDialog.show();
        GineePref.getSharedPreferences(getActivity());
        Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().getProductSubCatSubBySubID(/*GineePref.getAccessToken(getActivity()),*/
                sub_category_id);
        getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            JSONArray serviceArray = jsonObject.getJSONArray("data");
                            if(serviceArray.length() > 0){
                                ArrayList<String> list = new ArrayList<>();
                                for(int i = 0; i < serviceArray.length(); i++){
                                    list = new ArrayList<>();
                                    JSONObject item = serviceArray.getJSONObject(i);
                                    JSONArray array = item.getJSONArray("image_urls");
                                    for (int j = 0; j < array.length(); j++) {
                                        list.add(array.get(j).toString());
                                    }

                                    productsubModelArrayList.add(new ProductSubCatSubModel.Data(
                                            list,
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
                                            item.getString("product_sub_category_id")));
                                }
                                if(GineePref.getAccessToken(a) != null){
                                    if (!GineePref.getAccessToken(a).equals("")) {
                                        getAllWishListProductsAPICall();
                                    }
                                }
                                productListAdapter.notifyDataSetChanged();

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

    private void getSearchProductsAPICall() {
        mProgressDialog.show();
        GineePref.getSharedPreferences(getActivity());
        Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().getSearchProductByStr(GineePref.getAccessToken(getActivity()),
                search_text);
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
                                ArrayList<String> list = new ArrayList<>();
                                for (int i = 0; i < serviceArray.length(); i++) {
                                    list = new ArrayList<>();
                                    JSONObject item = serviceArray.getJSONObject(i);

                                    JSONArray array = item.getJSONArray("image_urls");
                                    for (int j = 0; j < array.length(); j++) {
                                        list.add(array.get(j).toString());
                                    }

                                    productsubModelArrayList.add(new ProductSubCatSubModel.Data(
                                            list,
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
                                            item.getString("product_sub_category_id"))
                                    );
                                }
                                productListAdapter.notifyDataSetChanged();
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

    private void getAllWishListProductsAPICall() {
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
                                productListAdapter.notifyDataSetChanged();
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

    private void checkWishlistProduct() {
        for(int i = 0; i< productsubModelArrayList.size(); i++){

            for(int j =0; j < wishListModelArrayList.size(); j++){

                if(wishListModelArrayList.get(j).equals(productsubModelArrayList.get(i))){

                }

            }

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            a=(Activity) context;
        }

    }

}