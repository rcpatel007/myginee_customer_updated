package com.myginee.customer.fragment;

import android.app.ProgressDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.R;
import com.myginee.customer.adapter.ProductSubCatListAdapter;
import com.myginee.customer.model.ProductSubModel;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.GineePref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ProductSubCategoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    String category = "", category_id = "";
    View root;
    ArrayList<ProductSubModel.Datum> serviceModelArrayList;
    ProductSubCatListAdapter serviceSubCategoryListAdapter;
    RecyclerView serviceSubCatListCategory;
    TextView tvCategory;
    private ProgressDialog mProgressDialog;

    public ProductSubCategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiceSubCategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductSubCategoryFragment newInstance(String param1, String param2) {
        ProductSubCategoryFragment fragment = new ProductSubCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_service_sub_category, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        Bundle bundle = getArguments();
        category = bundle.getString("category");
        category_id = bundle.getString("category_id");
        serviceSubCatListCategory = (RecyclerView) root.findViewById(R.id.rvServiceSubCategoryList);
        tvCategory = (TextView) root.findViewById(R.id.tvCategory);
        tvCategory.setText(category);
        serviceSubCatListCategory.setHasFixedSize(true);
        serviceSubCatListCategory.setNestedScrollingEnabled(false);
        serviceModelArrayList = new ArrayList<ProductSubModel.Datum>();

        getSubCategoryByIDAPICall(category_id);
        final int numColumns = 3;
        GridLayoutManager categoryGridLayoutManager = new GridLayoutManager(getActivity(), numColumns);
        serviceSubCatListCategory.setLayoutManager(categoryGridLayoutManager);
        serviceSubCatListCategory.addItemDecoration(new ProductSubCategoryFragment.SpacesItemDecoration(1));

        serviceSubCategoryListAdapter = new ProductSubCatListAdapter(getActivity(),
                ProductSubCategoryFragment.this, getActivity().getSupportFragmentManager(), serviceModelArrayList);
        serviceSubCatListCategory.setAdapter(serviceSubCategoryListAdapter);
        return root;
    }

    private void getSubCategoryByIDAPICall(String category_id) {
        mProgressDialog.show();
        GineePref.getSharedPreferences(getActivity());
        Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().getProductSubCatByID(/*GineePref.getAccessToken(getActivity()),*/
                category_id);
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
                                for(int i = 0; i < serviceArray.length(); i++){
                                    JSONObject item = serviceArray.getJSONObject(i);
                                    serviceModelArrayList.add(new ProductSubModel.Datum(
                                            item.getString("_id"),
                                            item.getString("product_category_id"),
                                            item.getString("name"),
                                            item.getString("image_url"),
                                            item.getString("created_at"),
                                            item.getString("updated_at")));
                                }
                                serviceSubCategoryListAdapter.notifyDataSetChanged();
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

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            outRect.top = space;

        }
    }

}
