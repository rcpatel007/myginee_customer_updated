package com.myginee.customer.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.R;
import com.myginee.customer.fragment.ProductSubCategoryFragment;
import com.myginee.customer.model.ProductModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private static final String TAG = "ProductListAdapter";
    Context context;
    Fragment fragment;
    FragmentManager fragmentManager;
    ArrayList<ProductModel.Datum> categories;
    LayoutInflater inflater;


    public ProductListAdapter(Context context, Fragment fragment,
                                      FragmentManager fragmentManager, ArrayList<ProductModel.Datum> categories) {
        this.context = context;
        this.fragment = fragment;
        this.fragmentManager = fragmentManager;
        this.categories = categories;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgProductIcon;

        TextView textViewName;
        ImageView imageViewIcon;

        public ViewHolder(View view) {
            super(view);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
//            imgProductIcon = (ImageView) view.findViewById(R.id.imgProductIcon);

//            imgProductIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
         /*   if(v.getId() == R.id.imgProductIcon){
                CategoryProductFragment categoryProductFragment = new CategoryProductFragment();
                Bundle bundles = new Bundle();
                HomeModel.Categories category = categories.get(getAdapterPosition());
                bundles.putString("category_id", category.category_id);
                bundles.putString("name", category.name);
                bundles.putBoolean("category", true);
                categoryProductFragment.setArguments(bundles);

                fragmentManager.beginTransaction().replace(R.id.frameContainer, categoryProductFragment).addToBackStack(categoryProductFragment.getClass().getName()).commit();

            }*/
        }
    }

    @Override
    public ProductListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.service_category_raw, parent, false);
        return new ProductListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductListAdapter.ViewHolder holder, final int position) {

        TextView textViewName = holder.textViewName;
        ImageView imageView = holder.imageViewIcon;

        textViewName.setText(categories.get(position).getName());
//        imageView.setImageResource(categories.get(position).getImageUrl());

        //  Picasso.with(context).load(categories.get(position).thumb_image).placeholder(R.mipmap.icon_place_holder).error(R.mipmap.icon_place_holder).into(holder.imgProductIcon);
        Picasso.with(context)
                .load(categories.get(position).getImageUrl())
                .placeholder(R.drawable.ic_plumber)
                .error(R.drawable.ic_plumber)
                .into(imageView);
        Log.e("Service", ""+position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductSubCategoryFragment categoryFragment = new ProductSubCategoryFragment();
                Bundle bundles = new Bundle();
                bundles.putSerializable("category", categories.get(position).getName());
                bundles.putSerializable("category_id", categories.get(position).getId());
                categoryFragment.setArguments(bundles);
                fragmentManager.beginTransaction().replace(R.id.fragMyGinee, categoryFragment)
                        .addToBackStack(categoryFragment.getClass().getName()).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
