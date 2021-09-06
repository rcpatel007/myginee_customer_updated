package com.myginee.customer.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.ChatActivity;
import com.myginee.customer.R;
import com.myginee.customer.model.AccServiceListModel;
import com.myginee.customer.utils.GineePref;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccServiceListAdapter extends RecyclerView.Adapter<AccServiceListAdapter.ViewHolder> {

    private static final String TAG = "AccServiceListAdapter";
    Context context;
    Fragment fragment;
    FragmentManager fragmentManager;
    ArrayList<AccServiceListModel.Datum> categories;
    LayoutInflater inflater;
    private ProgressDialog mProgressDialog;

    public AccServiceListAdapter(Context context, Fragment fragment,
                                 FragmentManager fragmentManager, ArrayList<AccServiceListModel.Datum> categories) {
        this.context = context;
        this.fragment = fragment;
        this.fragmentManager = fragmentManager;
        this.categories = categories;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageProductService;

        TextView tvNameProductService, tvServicePrice, tvDeliverDate, tvDeliverDateTitle, tvChat, tvServiceProDate;

        public ViewHolder(View view) {
            super(view);
            this.tvNameProductService = (TextView) itemView.findViewById(R.id.tvNameProductService);
            this.tvServicePrice = (TextView) itemView.findViewById(R.id.tvServicePrice);
            this.tvDeliverDate = (TextView) itemView.findViewById(R.id.tvDeliverDate);
            this.tvDeliverDateTitle = (TextView) itemView.findViewById(R.id.tvDeliverDateTitle);
            this.tvServiceProDate = (TextView) itemView.findViewById(R.id.tvServiceProDate);
            imageProductService = (ImageView) view.findViewById(R.id.imageProductService);
            tvChat = (TextView) view.findViewById(R.id.tvChat);

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
    public AccServiceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_acc_service, parent, false);
        return new AccServiceListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AccServiceListAdapter.ViewHolder holder, final int position) {

        TextView tvNameProductService = holder.tvNameProductService;
        TextView tvDeliverDate = holder.tvDeliverDate;
        TextView tvDeliverDateTitle = holder.tvDeliverDateTitle;
        TextView tvServiceProDate = holder.tvServiceProDate;
        TextView tvServicePrice = holder.tvServicePrice;
        ImageView imageView = holder.imageProductService;
        TextView tvChat = holder.tvChat;
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(categories.get(position).getServiceProvisionDate()));
        tvServiceProDate.setText("Service Date:" + dateString);
        tvDeliverDateTitle.setText(formateDateFromstring("yyyy-MM-dd HH:mm:ss", "dd, MMM yyyy | HH:mm:ss", categories.get(position).getCreatedAt()) + "");
        tvNameProductService.setText(categories.get(position).getSubCategoryName());
//        tvDeliverDateTitle.setText(CommonMethod.getDate(context, categories.get(position).getServiceProvisionDate()));
        if (categories.get(position).getIsAccepted() == true && categories.get(position).getIsActive() == true) {
            tvChat.setEnabled(true);
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                tvChat.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.red_button));
            } else {
                tvChat.setBackground(ContextCompat.getDrawable(context, R.drawable.red_button));
            }

        } else{
            tvChat.setEnabled(false);
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                tvChat.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.disable_button_bg));
            } else {
                tvChat.setBackground(ContextCompat.getDrawable(context, R.drawable.disable_button_bg));
            }

        }

        if (categories.get(position).getIsActive() == true) {
            tvDeliverDate.setText("Service Ginee Arriving");
        } else {
            tvDeliverDate.setText("Delivered on :" + formateDateFromstring("yyyy-MM-dd HH:mm:ss", "dd, MMM yyyy | HH:mm:ss", categories.get(position).getUpdatedAt()) + "");
        }


        tvServicePrice.setText("Rs. " + categories.get(position).getTotalPrice());
//        imageView.setImageResource(categories.get(position).getImage());

        Picasso.with(context)
                .load(categories.get(position).getSubCategoryImageUrl())
                .placeholder(R.drawable.ic_plumber)
                .error(R.drawable.ic_plumber)
                .into(imageView);

        Log.e("/all/order", "" + position);

        tvChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
//                    intent.putExtra("id", sub_category_id+"");
                intent.putExtra("_id", categories.get(position).getId());
                intent.putExtra("agent_id", categories.get(position).getAgentId());
//                intent.putExtra("sub_category_id", categories.get(position).getSubcategoryId());
//                intent.putExtra("sub_category_name", categories.get(position).getSubCategoryName());
                context.startActivity(intent);


            }
        });
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
        DialogServiceOrderAdapter mAdapter;

        mProgressDialog.show();
        GineePref.getSharedPreferences(context);
        mProgressDialog.dismiss();

        final Dialog dialogView = new Dialog(context);
        dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogView.setContentView(R.layout.dialog_service_order);
        dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        List<AccServiceListModel.ServiceList> mModelList = categories.get(position).getServiceList();

        TextView OrderProductName = (TextView) dialogView.findViewById(R.id.OrderProductName);
        TextView tvOrProName = (TextView) dialogView.findViewById(R.id.tvOrProName);
        TextView tvCatPrice = (TextView) dialogView.findViewById(R.id.tvCatPrice);
        TextView tvAddress = (TextView) dialogView.findViewById(R.id.tvAddress);
        ImageView imgOrderProduct = (ImageView) dialogView.findViewById(R.id.imgOrderProduct);
        TextView tvServiceProDate = (TextView) dialogView.findViewById(R.id.tvProDate);
        RecyclerView rvService = (RecyclerView) dialogView.findViewById(R.id.rvService);

        mAdapter = new DialogServiceOrderAdapter(context, categories.get(position).getServiceList());
        LinearLayoutManager manager = new LinearLayoutManager(context);
        rvService.setHasFixedSize(true);
        rvService.setLayoutManager(manager);
        rvService.setAdapter(mAdapter);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dateString = formatter.format(new Date(categories.get(position).getServiceProvisionDate()));
        tvServiceProDate.setText("Service Provision Date:" + dateString);

//        tvDesc.setText(categories.get(position).getDescription());
        tvCatPrice.setText("Rs. " + categories.get(position).getTotalPrice());
        OrderProductName.setText(categories.get(position).getSubCategoryName());
//        tvOrProName.setText(categories.get(position).getDescription());
        Picasso.with(context)
                .load(categories.get(position).getSubCategoryImageUrl())
                .placeholder(R.drawable.ic_plumber)
                .error(R.drawable.ic_plumber)
                .into(imgOrderProduct);
        tvAddress.setText("ADDRESS : " + categories.get(position).getAddress());

        if (!dialogView.isShowing()) {
            dialogView.show();
        }

    }


    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.getDefault());
//        df_input.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat,Locale.getDefault());
//        df_output.setTimeZone(TimeZone.getDefault());
        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);
//            Log.e(TAG, "-- dateFormat"+parsed.getTimezoneOffset());
        } catch (ParseException e) {
            Log.e(TAG, "ParseException - dateFormat");
        }

        return outputDate;

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