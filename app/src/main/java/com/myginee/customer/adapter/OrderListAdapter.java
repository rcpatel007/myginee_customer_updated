package com.myginee.customer.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.R;
import com.myginee.customer.model.OrderModel;
import com.myginee.customer.utils.CommonMethod;
import com.myginee.customer.utils.GineePref;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    private static final String TAG = "OrderListAdapter";
    Context context;
    Fragment fragment;
    FragmentManager fragmentManager;
    ArrayList<OrderModel.Datum> categories;
    LayoutInflater inflater;
    private ProgressDialog mProgressDialog;
    int total_price = 0;

    public OrderListAdapter(Context context, Fragment fragment,
                            FragmentManager fragmentManager, ArrayList<OrderModel.Datum> categories) {
        this.context = context;
        this.fragment = fragment;
        this.fragmentManager = fragmentManager;
        this.categories = categories;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageProductService;
        TextView tvNameProductService, tvServicePrice, tvDeliverDate, tvDeliverDateTitle;

        public ViewHolder(View view) {
            super(view);
            this.tvNameProductService = (TextView) itemView.findViewById(R.id.tvNameProductService);
            this.tvServicePrice = (TextView) itemView.findViewById(R.id.tvServicePrice);
            this.tvDeliverDate = (TextView) itemView.findViewById(R.id.tvDeliverDate);
            this.tvDeliverDateTitle = (TextView) itemView.findViewById(R.id.tvDeliverDateTitle);
            imageProductService = (ImageView) view.findViewById(R.id.imageProductService);

        }
    }

    @Override
    public OrderListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.order_list_raw, parent, false);
        return new OrderListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderListAdapter.ViewHolder holder, final int position) {

        TextView tvNameProductService = holder.tvNameProductService;
        TextView tvDeliverDate = holder.tvDeliverDate;
        TextView tvDeliverDateTitle = holder.tvDeliverDateTitle;
        TextView tvServicePrice = holder.tvServicePrice;
        ImageView imageView = holder.imageProductService;
        tvNameProductService.setVisibility(View.GONE);
        tvNameProductService.setText(categories.get(position).getName());
//        tvDeliverDateTitle.setText(CommonMethod.getDate(categories.get(position).getCreatedAt(), "dd-MMM-yyyy"));
        tvDeliverDateTitle.setText(CommonMethod.formateDateFromstring("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd-MMM-yyyy", categories.get(position).getCreatedAt()) + "");

        if (categories.get(position).getDispatched() == true) {
            tvDeliverDate.setText("Delivered on " + CommonMethod.getDate(categories.get(position).getUpdatedAt(), "dd-MMM-yyyy"));
        } else {
            tvDeliverDate.setText("Delivery Arriving");
        }

        tvServicePrice.setText("Rs. " + categories.get(position).getPrice());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOrderByIdAPICall(position);
            }
        });

    }

    private void getOrderByIdAPICall(final int position) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(context.getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);


        mProgressDialog.show();
        GineePref.getSharedPreferences(context);
        mProgressDialog.dismiss();

        final Dialog dialogView = new Dialog(context);
        dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogView.setContentView(R.layout.dialog_service_order);
        dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        AlertServiceListAdapter mAdapter;

        TextView OrderProductName = (TextView) dialogView.findViewById(R.id.OrderProductName);
        TextView tvOrProName = (TextView) dialogView.findViewById(R.id.tvOrProName);
        TextView tvCatPrice = (TextView) dialogView.findViewById(R.id.tvCatPrice);
        TextView tvAddress = (TextView) dialogView.findViewById(R.id.tvAddress);
        ImageView imgOrderProduct = (ImageView) dialogView.findViewById(R.id.imgOrderProduct);
        TextView tvServiceProDate = (TextView) dialogView.findViewById(R.id.tvProDate);
        RecyclerView rvService = (RecyclerView) dialogView.findViewById(R.id.rvService);
        tvOrProName.setVisibility(View.GONE);
        mAdapter = new AlertServiceListAdapter(context, categories.get(position).getProductList());
        LinearLayoutManager manager = new LinearLayoutManager(context);
        rvService.setHasFixedSize(true);
        rvService.setLayoutManager(manager);
        rvService.setAdapter(mAdapter);

        if (categories.get(position).getDispatched() == true) {
            tvServiceProDate.setText("Delivered on " + CommonMethod.getDate(categories.get(position).getUpdatedAt(), "dd-MMM-yyyy"));
        } else {
            tvServiceProDate.setText("Delivery Arriving");
        }
        tvCatPrice.setText("Rs. " + calculateTotal(categories.get(position).getProductList()));
        OrderProductName.setText(categories.get(position).getName());
        tvOrProName.setText(categories.get(position).getName());
        Picasso.with(context)
                .load(categories.get(position).getImageUrl())
                .placeholder(R.drawable.ic_plumber)
                .error(R.drawable.ic_plumber)
                .into(imgOrderProduct);
        tvAddress.setText("ADDRESS : " + categories.get(position).getAddress() + "");

        if (!dialogView.isShowing()) {
            dialogView.show();
        }

    }

    private int calculateTotal(List<OrderModel.ProductList> orderArrayList) {
        total_price = 0;
        for (OrderModel.ProductList p : orderArrayList) {
            int qty = p.getQuantity();
            total_price += p.getPrice() * qty;
        }
        return total_price;
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