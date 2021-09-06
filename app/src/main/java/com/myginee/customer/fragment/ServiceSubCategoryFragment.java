package com.myginee.customer.fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.myginee.customer.R;
import com.myginee.customer.adapter.ServiceSubCategoryListAdapter;
import com.myginee.customer.model.ServiceSubModel;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.GineePref;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceSubCategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceSubCategoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    String category = "", category_id = "", gif_url = "";
    View root;
    ArrayList<ServiceSubModel.Datum> serviceModelArrayList;
    ServiceSubCategoryListAdapter serviceSubCategoryListAdapter;
    RecyclerView serviceSubCatListCategory;
    TextView tvCategory;
    private ProgressDialog mProgressDialog;
    private final int SPLASH_DISPLAY_LENGTH = 10 * 1000;
    LinearLayout llData;
    GifImageView imgLoader;
    WebView wb;

    public ServiceSubCategoryFragment() {
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
    public static ServiceSubCategoryFragment newInstance(String param1, String param2) {
        ServiceSubCategoryFragment fragment = new ServiceSubCategoryFragment();
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
        gif_url = bundle.getString("gif_url");

        serviceSubCatListCategory = (RecyclerView) root.findViewById(R.id.rvServiceSubCategoryList);
        tvCategory = (TextView) root.findViewById(R.id.tvCategory);
        llData = (LinearLayout) root.findViewById(R.id.llData);
        imgLoader = (GifImageView) root.findViewById(R.id.imgLoader);
        tvCategory.setText(category);
        serviceSubCatListCategory.setHasFixedSize(true);
        serviceSubCatListCategory.setNestedScrollingEnabled(false);
        serviceModelArrayList = new ArrayList<ServiceSubModel.Datum>();

        Log.e("gif_loader :", gif_url + "");

        wb = (WebView) root.findViewById(R.id.webView);

        if (!gif_url.equals("")) {
            wb.loadUrl(gif_url);
            wb.setInitialScale(1);
            wb.setBackgroundColor(Color.TRANSPARENT);
            wb.getSettings().setLoadWithOverviewMode(true);
            wb.getSettings().setUseWideViewPort(true);
            wb.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

            wb.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    handler.proceed();
                }
            });
            Picasso.with(getActivity()).load(Uri.parse(gif_url)).placeholder(R.raw.comman_myginee)
                    .into(imgLoader);

            llData.setVisibility(View.GONE);
            imgLoader.setVisibility(View.VISIBLE);
//            wb.setVisibility(View.VISIBLE);
        } else {
            llData.setVisibility(View.GONE);
            imgLoader.setVisibility(View.VISIBLE);
            wb.setVisibility(View.GONE);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSubCategoryByIDAPICall(category_id);
            }
        }, SPLASH_DISPLAY_LENGTH);

        final int numColumns = 3;
        GridLayoutManager categoryGridLayoutManager = new GridLayoutManager(getActivity(), numColumns);
        serviceSubCatListCategory.setLayoutManager(categoryGridLayoutManager);
        serviceSubCatListCategory.addItemDecoration(new SpacesItemDecoration(1));

        serviceSubCategoryListAdapter = new ServiceSubCategoryListAdapter(getActivity(),
                ServiceSubCategoryFragment.this, getActivity().getSupportFragmentManager(), serviceModelArrayList);
        serviceSubCatListCategory.setAdapter(serviceSubCategoryListAdapter);
        return root;
    }

    private void getSubCategoryByIDAPICall(String category_id) {
//        mProgressDialog.show();

        GineePref.getSharedPreferences(getActivity());
        Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().getServiceSubCatByID(/*GineePref.getAccessToken(getActivity()),*/
                category_id);
        getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                mProgressDialog.dismiss();
                llData.setVisibility(View.VISIBLE);
                imgLoader.setVisibility(View.GONE);
                wb.setVisibility(View.GONE);
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            JSONArray serviceArray = jsonObject.getJSONArray("data");
                            if (serviceArray.length() > 0) {
                                for (int i = 0; i < serviceArray.length(); i++) {
                                    JSONObject item = serviceArray.getJSONObject(i);
                                    serviceModelArrayList.add(new ServiceSubModel.Datum(
                                            item.getString("_id"),
                                            item.getString("name"),
                                            item.getString("image_url"),
                                            item.getString("service_id"),
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
//                mProgressDialog.dismiss();
                llData.setVisibility(View.VISIBLE);
                imgLoader.setVisibility(View.GONE);
                wb.setVisibility(View.GONE);
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
