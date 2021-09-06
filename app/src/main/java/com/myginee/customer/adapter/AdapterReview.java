package com.myginee.customer.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.R;
import com.myginee.customer.model.ServiceListModel;
import com.myginee.customer.utils.CommonMethod;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.MyViewHolder> {

    private List<ServiceListModel.ServiceList> mModelList;
    private Context context;

    public AdapterReview(Context mContext, List<ServiceListModel.ServiceList> modelList) {
        mModelList = modelList;
        context = mContext;
    }

    @Override
    public AdapterReview.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_review_raw, parent, false);
        return new AdapterReview.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdapterReview.MyViewHolder holder, int position) {
        final ServiceListModel.ServiceList model = mModelList.get(position);
        holder.tvServiceName.setText(model.getServiceName());
        holder.tvServicePrice.setText(model.getPrice()+" Rs. ");
    }

    @Override
    public int getItemCount() {
        return mModelList == null ? 0 : mModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView tvServicePrice, tvServiceName;

        private MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvServicePrice = (TextView) itemView.findViewById(R.id.tvServicePrice);
            tvServiceName = (TextView) itemView.findViewById(R.id.tvServiceName);
        }
    }

}