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
import com.myginee.customer.fragment.DetailFragment;
import com.myginee.customer.model.ServiceSubModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ServiceSubCategoryListAdapter extends RecyclerView.Adapter<ServiceSubCategoryListAdapter.ViewHolder> {

    private static final String TAG = "ServiceSubCategoryListAdapter";
    Context context;
    Fragment fragment;
    FragmentManager fragmentManager;
    ArrayList<ServiceSubModel.Datum> categories;
    LayoutInflater inflater;


    public ServiceSubCategoryListAdapter(Context context, Fragment fragment,
                                      FragmentManager fragmentManager, ArrayList<ServiceSubModel.Datum> categories) {
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
    public ServiceSubCategoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.service_category_raw, parent, false);
        return new ServiceSubCategoryListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ServiceSubCategoryListAdapter.ViewHolder holder, final int position) {

        TextView textViewName = holder.textViewName;
        ImageView imageView = holder.imageViewIcon;

        textViewName.setText(categories.get(position).getName());
        Picasso.with(context)
                .load(categories.get(position).getImageUrl())
                .placeholder(R.drawable.ic_plumber)
                .error(R.drawable.ic_plumber)
                .into(imageView);

        Log.e("Service", ""+position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailFragment detailFragment = new DetailFragment();
                Bundle bundles = new Bundle();
                bundles.putSerializable("sub_category_name", categories.get(position).getName());
                bundles.putSerializable("sub_category_id", categories.get(position).getId());
                bundles.putSerializable("service_id", categories.get(position).getServiceId());
                detailFragment.setArguments(bundles);
                fragmentManager.beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).replace(R.id.frame_container, detailFragment)
                        .addToBackStack(null).commit();
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

