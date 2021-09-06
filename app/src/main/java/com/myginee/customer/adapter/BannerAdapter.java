package com.myginee.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.myginee.customer.MainActivity;
import com.myginee.customer.R;
import com.myginee.customer.utils.CarouselLinearLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class BannerAdapter extends PagerAdapter {
    Context context;
    ArrayList<String> sliders;
    LayoutInflater inflater;
    FragmentManager fragmentManager;

    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    private float scale;


    private static final String TAG = "BannerAdapter";
    public BannerAdapter(Context context, ArrayList<String> sliders, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.sliders = sliders;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        return sliders.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view;
        view = inflater.inflate(R.layout.banner_item_raw, container, false);
        ImageView image = (ImageView) view.findViewById(R.id.bannerImages);

        Picasso.with(context)
                .load(sliders.get(position).toString())
                .placeholder(R.drawable.banner)
                .error(R.drawable.banner)
                .into(image);

        container.addView(view);

        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }


}
