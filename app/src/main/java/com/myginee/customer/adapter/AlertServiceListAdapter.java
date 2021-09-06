package com.myginee.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.R;
import com.myginee.customer.model.OrderModel;

import java.util.List;

public class AlertServiceListAdapter extends RecyclerView.Adapter<AlertServiceListAdapter.MyViewHolder> {

    private List<OrderModel.ProductList> mModelList;
    private Context context;

    public AlertServiceListAdapter(Context mContext, List<OrderModel.ProductList> modelList) {
        mModelList = modelList;
        context = mContext;
    }

    @Override
    public AlertServiceListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_raw, parent, false);
        return new AlertServiceListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AlertServiceListAdapter.MyViewHolder holder, int position) {
        final OrderModel.ProductList model = mModelList.get(position);
        holder.tvServiceName.setVisibility(View.VISIBLE);
        holder.tvServiceName.setText(model.getName());
        holder.tvServiceQty.setText(model.getQuantity()+"");
        holder.tvServicePrice.setText("Rs. "+model.getPrice());


    }

    @Override
    public int getItemCount() {
        return mModelList == null ? 0 : mModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView tvServiceName, tvServicePrice, tvServiceQty;

        private MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvServiceQty = (TextView) itemView.findViewById(R.id.tvServiceQty);
            tvServicePrice = (TextView) itemView.findViewById(R.id.tvServicePrice);
            tvServiceName = (TextView) itemView.findViewById(R.id.tvServiceName);
        }
    }

}