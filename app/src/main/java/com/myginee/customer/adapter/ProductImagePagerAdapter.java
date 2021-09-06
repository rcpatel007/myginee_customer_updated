package com.myginee.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

import com.myginee.customer.R;

import java.util.ArrayList;

public class ProductImagePagerAdapter extends PagerAdapter {
    Context context;
    ArrayList<String> sliders;
    LayoutInflater inflater;
    FragmentManager fragmentManager;

    private static final String TAG = "ProductImagePagerAdapter";
    public ProductImagePagerAdapter(Context context, ArrayList<String> sliders, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.sliders = sliders;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view;
        view = inflater.inflate(R.layout.product_pager_raw, container, false);
        ImageView image = (ImageView) view.findViewById(R.id.productImages);
        container.addView(view);

        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }


}
