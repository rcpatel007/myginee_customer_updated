package com.myginee.customer.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.myginee.customer.CarouselEffectTransformer;
import com.myginee.customer.ChatActivity;
import com.myginee.customer.HomeActivity;
import com.myginee.customer.InquiryFragment;
import com.myginee.customer.R;
import com.myginee.customer.adapter.BannerAdapter;
import com.myginee.customer.adapter.DetailDescriptionAdapter;
import com.myginee.customer.adapter.ReviewAdapter;
import com.myginee.customer.model.ProductSubCatSubModel;
import com.myginee.customer.model.ServiceListModel;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.GineePref;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.myginee.customer.fragment.CartFragment.total_price;

public class DetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View root;
    TextView tvInquiry, tvTitle, tvChat, tvCatRating, tvDesc;
    String sub_category_id = "", sub_category_name = "", service_id = "";
    Toolbar toolbar;
    //    ImageView imgBack;
    RecyclerView rvDescription, rvDetailService;
    DetailDescriptionAdapter detailDescriptionAdapter;
    private List<String> mModelList;
    private ProgressDialog mProgressDialog;
    ReviewAdapter mAdapter;
    private List<ServiceListModel.ServiceList> mDetailModelList;

    ViewPager vpBanner;
    ViewPager viewPagerBackground;
    CirclePageIndicator indicaterBanner;
    int quantity = 1;
    BannerAdapter bannerAdapter;
    ArrayList<String> sliders;

    private Handler handler;
    private Runnable runnable;
    int position = 0;
    int slideTimeOut = 5000;
    public final static int LOOPS = 1000;
    public static int count = 0; //ViewPager items size
    Timer timer;
    private int currentPage = 0;
    /**
     * You shouldn't define first page = 0.
     * Let define firstpage = 'number viewpager size' to make endless carousel
     */
    public static int FIRST_PAGE = 0;
    JSONArray serviceArray = new JSONArray();
    private List<String> mServiceList;
    private List<ServiceListModel.ServiceList> modelList;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
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
        root = inflater.inflate(R.layout.activity_enquiry, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        tvTitle = (TextView) root.findViewById(R.id.tvTitle);
        tvInquiry = (TextView) root.findViewById(R.id.tvInquiry);
        tvChat = (TextView) root.findViewById(R.id.tvChat);
//        tvCatRating = (TextView) root.findViewById(R.id.tvCatRating);
        tvDesc = (TextView) root.findViewById(R.id.tvDesc);
//        imgBack = (ImageView) root.findViewById(R.id.imgBack);
        rvDescription = root.findViewById(R.id.rvDetailDesc);
        rvDetailService = root.findViewById(R.id.rvDetailService);
        vpBanner = (ViewPager) root.findViewById(R.id.vpBanner);
        viewPagerBackground = (ViewPager) root.findViewById(R.id.viewPagerbackground);
        indicaterBanner = (CirclePageIndicator) root.findViewById(R.id.indicaterBanner);
        sliders = new ArrayList<>();
        bannerAdapter = new BannerAdapter(getActivity(), sliders, getActivity().getSupportFragmentManager());
        vpBanner.setAdapter(bannerAdapter);
        indicaterBanner.setViewPager(vpBanner);
        mServiceList = new ArrayList<>();
        toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        ((HomeActivity) getActivity()).setSupportActionBar(toolbar);
        if (((HomeActivity) getActivity()).getSupportActionBar() != null) {
            ((HomeActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((HomeActivity) getActivity()).getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().onBackPressed();
                showHome();
            }
        });
        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run() {

                vpBanner.setCurrentItem(currentPage, true);
                if (currentPage == sliders.size()) {
                    currentPage = 0;
                } else {
                    ++currentPage;
                }
            }
        };


        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 500, 5500);
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                vpBanner.post(new Runnable(){
//
//                    @Override
//                    public void run() {
//                        vpBanner.setCurrentItem((vpBanner.getCurrentItem()+1)%sliders.size());
//                    }
//                });
//            }
//        };
//        timer = new Timer();
//        timer.schedule(timerTask, 3000, 3000);
        try {
            runnable = new Runnable() {
                public void run() {

//                    if (homeModel != null && homeModel.data != null && homeModel.data.sliders != null && homeModel.data.sliders.size() > 0) {

                    if (position >= sliders.size()) {
//                                        if (position > homeModel.data.sliders.size()) {
                        position = 0;
                    } else {
                        position = position + 1;
                    }
                    vpBanner.setCurrentItem(position, true);
                    handler.postDelayed(runnable, slideTimeOut);
                }
//                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }

//        getBannerImagesAPI();


        vpBanner.setClipChildren(false);
        vpBanner.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.pager_margin));
        vpBanner.setOffscreenPageLimit(3);
        vpBanner.setPageTransformer(false, new CarouselEffectTransformer(getActivity()));
        vpBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int index = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
//                int width = viewPagerBackground.getWidth();
//                viewPagerBackground.scrollTo((int) (width * position + width * positionOffset), 0);
            }

            @Override
            public void onPageSelected(int position) {
                index = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                if (state == ViewPager.SCROLL_STATE_IDLE) {
//                    viewPagerBackground.setCurrentItem(index);
//                }
            }
        });


        mModelList = new ArrayList<String>();
        mDetailModelList = new ArrayList<ServiceListModel.ServiceList>();

        detailDescriptionAdapter = new DetailDescriptionAdapter(getActivity(), mModelList);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        rvDescription.setHasFixedSize(true);
        rvDescription.setLayoutManager(manager);
        rvDescription.setAdapter(detailDescriptionAdapter);

        LinearLayoutManager llDetail = new LinearLayoutManager(getActivity());
        mAdapter = new ReviewAdapter(getActivity(), mDetailModelList, DetailFragment.this);
        rvDetailService.setHasFixedSize(true);
        rvDetailService.setLayoutManager(llDetail);
        rvDetailService.setAdapter(mAdapter);

       /* imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHome();
            }
        });*/
        Bundle bundle = getArguments();
        sub_category_name = bundle.getString("sub_category_name");
        sub_category_id = bundle.getString("sub_category_id");
        service_id = bundle.getString("service_id");
        tvTitle.setText(sub_category_name);
        getSubCategoryServiceByIDAPICall(sub_category_id);
//        getProductsAPICall(service_id);
        tvInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InquiryFragment inquiryFragment = new InquiryFragment();
                Bundle bundles = new Bundle();
                bundles.putSerializable("sub_category_name", sub_category_name);
                bundles.putSerializable("sub_category_id", sub_category_id);
                bundles.putSerializable("service_id", service_id);
                inquiryFragment.setArguments(bundles);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).replace(R.id.frame_container, inquiryFragment)
                        .addToBackStack(inquiryFragment.getClass().getName()).commit();
//                startActivity(new Intent(getActivity(), InquiryActivity.class));
            }
        });
//        if(!GineePref.getAgent(getActivity()).equals("")){
//            tvChat.setText("CHAT");
//        }else {
        tvChat.setText("CALL");
//        }
        tvChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvChat.getText().toString().equalsIgnoreCase("call")) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + "+91 9893778304"));
                    startActivity(callIntent);
                } else if (tvChat.getText().toString().equalsIgnoreCase("chat")) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
//                    intent.putExtra("id", sub_category_id+"");
                    intent.putExtra("_id", service_id);
//                    intent.putExtra("product_id", categories.get(position).getCategoryId());
                    intent.putExtra("sub_category_id", sub_category_id);
                    intent.putExtra("sub_category_name", sub_category_name);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                }

            }
        });
        return root;
    }

    public void showHome() {
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        while (fm.getBackStackEntryCount() > 3) {
            fm.popBackStackImmediate();
        }

    }

    private void getSubCategoryServiceByIDAPICall(String category_id) {
        mProgressDialog.show();
        GineePref.getSharedPreferences(getActivity());
        Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().getSubCatServiceByID(GineePref.getAccessToken(getActivity()),
                category_id);
        getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            if (jsonObject.getJSONObject("data") != null) {
                                JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                                if (jsonObjectData != null) {
                                    sliders = new ArrayList<>();
                                    JSONArray array = jsonObjectData.getJSONArray("image_urls");
                                    if (array.length() > 0) {
                                        for (int j = 0; j < array.length(); j++) {
                                            sliders.add(array.get(j).toString());
                                        }
                                        bannerAdapter = new BannerAdapter(getActivity(), sliders, getActivity().getSupportFragmentManager());
                                        vpBanner.setAdapter(bannerAdapter);
                                        indicaterBanner.setViewPager(vpBanner);
                                    }

                                }
                                JSONArray serviceArray = jsonObjectData.getJSONArray("service_list");
                                if (serviceArray.length() > 0) {
                                    for (int i = 0; i < serviceArray.length(); i++) {
                                        JSONObject item = serviceArray.getJSONObject(i);
                                        if (item.has("image_url")) {
                                            mDetailModelList.add(new ServiceListModel.ServiceList(
                                                    item.getString("service_id"),
                                                    item.getString("service_name"),
                                                    item.getString("measurement"),
                                                    item.getString("price"), false,
                                                    item.getString("mrp"),
                                                    item.getString("description"),
                                                    item.getString("image_url"),
                                                    item.getBoolean("is_added_to_cart")
                                            ));
                                        } else {
                                            mDetailModelList.add(new ServiceListModel.ServiceList(
                                                    item.getString("service_id"),
                                                    item.getString("service_name"),
                                                    item.getString("measurement"),
                                                    item.getString("price"), false,
                                                    item.getString("mrp"),
                                                    item.getString("description"),
                                                    item.getBoolean("is_added_to_cart")
                                            ));
                                        }

                                    }
                                    mAdapter.notifyDataSetChanged();
                                    bannerAdapter.notifyDataSetChanged();
//                                    tvCatRating.setText(jsonObjectData.getInt("avg_rating") + " / 5");
                                    tvDesc.setText(jsonObjectData.getString("description") + "");
                                } else {
                                    Toast.makeText(getActivity(), "No Service found", Toast.LENGTH_LONG).show();
                                }

                               /* JSONArray descArray = jsonObjectData.getString("description");
                                if (descArray.length() > 0) {
                                    for (int i = 0; i < descArray.length(); i++) {
                                        mModelList.add(descArray.get(i).toString());
                                    }
                                    detailDescriptionAdapter.notifyDataSetChanged();
                                }else {
                                    Toast.makeText(getActivity(), "No Description found", Toast.LENGTH_LONG).show();
                                }*/
                            } else {
                                Toast.makeText(getActivity(), "No Data found", Toast.LENGTH_LONG).show();
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

    private long getTimeInMilliSec(String dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yy HH:mm a");
        long timeInMilliseconds = 0;
        try {
            Date mDate = sdf.parse(dateTime);
            timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }


    private JSONArray createJsonArray(String serviceId, String serviceName, String measurement, String price) {

        JSONObject jsonService = new JSONObject();
        try {
            jsonService.put("service_id", serviceId);
            jsonService.put("service_name", serviceName);
            jsonService.put("measurement", measurement);
            jsonService.put("price", price);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return serviceArray.put(jsonService);
    }

    public void callAPIForAddServiceToCart(final ServiceListModel.ServiceList model) {
        mProgressDialog.show();
        serviceArray = new JSONArray();
        mServiceList.clear();
//        for (ServiceListModel.ServiceList model : mDetailModelList) {

//            if (model.getIs_selected()) {
//                text += " " + model.getServiceName() + " ";
                mServiceList.add(model.getServiceName());
                createJsonArray(model.getServiceId(), model.getServiceName(), model.getMeasurement(),
                        model.getPrice());
//                Log.d("TAG", "Output : " + text + " ");

//            }

//        }
        GineePref.getSharedPreferences(getActivity());
        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("category_id", service_id);
            paramObject.put("subcategory_id", sub_category_id);
            paramObject.put("service_list", serviceArray);

            paramObject.put("description", model.getDescription());
            paramObject.put("service_provision_date", getTimeInMilliSec(new SimpleDateFormat("dd/MMM/yyyy",
                    Locale.getDefault()).format(new Date()) + " " + new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date())));

            RequestBody body =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramObject.toString());

            Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().saveCartDetail(GineePref.getAccessToken(getActivity()),
                    body);
            getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mProgressDialog.dismiss();
                    if (response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getBoolean("success") == true) {
                                Toast.makeText(getActivity(), "Service added successfully", Toast.LENGTH_LONG).show();
                                mAdapter.notifyDataSetChanged();
                                /*ReviewFragment reviewFragment = new ReviewFragment();
                                Bundle bundles = new Bundle();
                                bundles.putSerializable("sub_category_id", sub_category_id);
                                bundles.putSerializable("sub_category_name", sub_category_name);
                                bundles.putSerializable("service_id", service_id);
                                bundles.putSerializable("service_array", serviceArray.toString());
                                bundles.putSerializable("date", getTimeInMilliSec(new SimpleDateFormat("dd/MMM/yyyy",
                                        Locale.getDefault()).format(new Date())));
                                bundles.putSerializable("time", new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date()));
                                bundles.putSerializable("description", model.getDescription());

                                reviewFragment.setArguments(bundles);
                                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                                        R.anim.slide_in,  // enter
                                        R.anim.fade_out,  // exit
                                        R.anim.fade_in,   // popEnter
                                        R.anim.slide_out  // popExit
                                ).replace(R.id.frame_container, reviewFragment)
                                        .addToBackStack(reviewFragment.getClass().getName()).commit();*/
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

