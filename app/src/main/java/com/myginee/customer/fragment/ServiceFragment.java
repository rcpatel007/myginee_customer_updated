package com.myginee.customer.fragment;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.myginee.customer.CarouselEffectTransformer;
import com.myginee.customer.GridSpacingItemDecoration;
import com.myginee.customer.R;
import com.myginee.customer.adapter.BannerAdapter;
import com.myginee.customer.adapter.ServiceCategoryListAdapter;
import com.myginee.customer.model.ServiceModel;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.GineePref;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Response;

public class ServiceFragment extends Fragment {
    private static final String TAG = "serviceFragment";
    private ServiceModel serviceViewModel;
    View root;
    ArrayList<ServiceModel.Datum> serviceModelArrayList;
    ServiceCategoryListAdapter serviceCategoryListAdapter;
    RecyclerView serviceListCategory;
    ViewPager vpBanner, viewPagerBackground;
    CirclePageIndicator indicaterBanner;
    int quantity = 1;
    BannerAdapter bannerAdapter;
    ArrayList<String> sliders;

    private Handler handler;
    private Runnable runnable;
    int position = 0;
    int slideTimeOut = 5000;
    private ProgressDialog mProgressDialog;
    Timer timer;
    private int currentPage = 0;


    public ServiceFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment ServiceFragment.
     */
    public static ServiceFragment newInstance() {
        return new ServiceFragment();
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
        root = inflater.inflate(R.layout.fragment_service, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        serviceListCategory = (RecyclerView) root.findViewById(R.id.rvServiceCategoryList);
        vpBanner = (ViewPager) root.findViewById(R.id.vpBanner);
        viewPagerBackground = (ViewPager) root.findViewById(R.id.viewPagerbackground);
        indicaterBanner = (CirclePageIndicator) root.findViewById(R.id.indicaterBanner);
        sliders = new ArrayList<>();
        /*for(int i = 0; i <= 2; i++){
            sliders.add("i");
        }*/

        bannerAdapter = new BannerAdapter(getActivity(), sliders, getActivity().getSupportFragmentManager());
        vpBanner.setAdapter(bannerAdapter);
        indicaterBanner.setViewPager(vpBanner);


        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run()
            {

                vpBanner.setCurrentItem(currentPage, true);
                if(currentPage == sliders.size())
                {
                    currentPage = 0;
                }
                else
                {
                    ++currentPage ;
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
//                vpBanner.setCurrentItem(currentPage, true);
//                if(currentPage == sliders.size())
//                {
//                    currentPage = 0;
//                }
//                else
//                {
//                    ++currentPage ;
//                }
//    /*            vpBanner.post(new Runnable(){
//
//                    @Override
//                    public void run() {
//                        if(sliders.size() > 0){
//                            vpBanner.setCurrentItem((vpBanner.getCurrentItem()+1)%sliders.size());
//                        }
//
//                    }
//                });*/
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
//                    int curr = vpBanner.getCurrentItem();
//                    int lastReal = vpBanner.getAdapter().getCount() - 2;
//                    if (curr == 0) {
//                        vpBanner.setCurrentItem(lastReal, false);
//                    } else if (curr > lastReal) {
//                        vpBanner.setCurrentItem(1, false);
//                    }
//                }
//                if (state == ViewPager.SCROLL_STATE_IDLE) {
//                    viewPagerBackground.setCurrentItem(index);
//                }
            }
        });

        getBannerImagesAPI();
//        vpBanner.startAutoScroll(true);
        serviceListCategory.setHasFixedSize(true);
        serviceListCategory.setNestedScrollingEnabled(false);
        serviceModelArrayList = new ArrayList<ServiceModel.Datum>();

        getAllServiceAPICall();

        final int numColumns = 3;
        GridLayoutManager categoryGridLayoutManager = new GridLayoutManager(getActivity(),
                numColumns, LinearLayoutManager.VERTICAL, false);
//        StaggeredGridLayoutManager categoryGridLayoutManager = new StaggeredGridLayoutManager(
//                numColumns, LinearLayoutManager.VERTICAL);
        serviceListCategory.setLayoutManager(categoryGridLayoutManager);
        serviceCategoryListAdapter = new

                ServiceCategoryListAdapter(getActivity(),

                ServiceFragment.this,

                getActivity().

                        getSupportFragmentManager(), serviceModelArrayList);
        serviceListCategory.setAdapter(serviceCategoryListAdapter);
        serviceListCategory.setNestedScrollingEnabled(false);
        serviceListCategory.setHasFixedSize(true);
        serviceListCategory.setItemViewCacheSize(20);
        serviceListCategory.setDrawingCacheEnabled(true);
        serviceListCategory.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        serviceListCategory.addItemDecoration(new

                GridSpacingItemDecoration(3, dpToPx(12), true));

        return root;
    }

    public int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void getBannerImagesAPI() {
        mProgressDialog.show();

        Call<ResponseBody> getAllBannersCall = GineeAppApi.api().getBannerImages();
        getAllBannersCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();

                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            if (data.length() > 0) {
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject item = data.getJSONObject(i);
//                                    serviceModelArrayList.add(new ServiceModel.Datum(item.getString("_id"),
//                                            item.getString("name"), item.getString("image_url"), item.getString("created_at"),
//                                            item.getString("updated_at")));
                                    sliders.add(item.getString("image_url"));
                                }
                                bannerAdapter.notifyDataSetChanged();
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

    private void getAllServiceAPICall() {
        mProgressDialog.show();

        GineePref.getSharedPreferences(getActivity());
        Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().getAllService();
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
                                    if (item.getString("name") != null &&
                                            item.getString("name").toLowerCase().contains(MyGineeServiceFragment.searchTxt)) {
                                        serviceModelArrayList.add(new ServiceModel.Datum(item.getString("_id"),
                                                item.getString("name"), item.getString("image_url"), item.getString("created_at"),
                                                item.getString("updated_at"), item.getString("gif_url")));
                                        //something here
                                    }

                                }
                                serviceCategoryListAdapter.notifyDataSetChanged();
//                                for(ServiceModel.Datum d : serviceModelArrayList){

//                                }
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


    //Receive message
    public void youGotMsg(String msg) {
        Toast.makeText(getActivity(), msg + "", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPause() {
        super.onPause();
//        vpBanner.stopAutoScroll();
    }
}