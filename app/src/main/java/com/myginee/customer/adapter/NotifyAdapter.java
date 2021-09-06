package com.myginee.customer.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.myginee.customer.ChatActivity;
import com.myginee.customer.HomeActivity;
import com.myginee.customer.R;
import com.myginee.customer.model.AlertDataModel;
import com.myginee.customer.model.Message;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.ConnectionDetector;
import com.myginee.customer.utils.GineePref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.ViewHolder> {

    private static final String TAG = "NotifyAdapter";
    Context context;
    Activity activity;
    Fragment fragment;
    FragmentManager fragmentManager;
    ArrayList<AlertDataModel.Datum> categories;
    LayoutInflater inflater;
    private ProgressDialog mProgressDialog;
    Dialog dialogView;

    public NotifyAdapter(Context context, Activity activity, ArrayList<AlertDataModel.Datum> categories) {
        this.context = context;
        this.fragment = fragment;
        this.fragmentManager = fragmentManager;
        this.categories = categories;
        this.activity = activity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrProName, tvCatPrice, tvReview, tvTapToView;
        RatingBar ratingBar;
        RelativeLayout rlRating;

        public ViewHolder(View view) {
            super(view);
            this.tvOrProName = (TextView) itemView.findViewById(R.id.tvOrProName);
            this.tvCatPrice = (TextView) itemView.findViewById(R.id.tvCatPrice);
            this.tvReview = (TextView) itemView.findViewById(R.id.tvReview);
//            this.tvTapToView = (TextView) itemView.findViewById(R.id.tvTapToView);
            this.ratingBar = itemView.findViewById(R.id.ratingBar);
            this.rlRating = (RelativeLayout) itemView.findViewById(R.id.rlRating);
        }

    }

    @Override
    public NotifyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.notification_raw, parent, false);
        return new NotifyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NotifyAdapter.ViewHolder holder, final int position) {

        TextView tvOrProName = holder.tvOrProName;
        TextView tvCatPrice = holder.tvCatPrice;
        TextView tvReview = holder.tvReview;
//        TextView tvTapToView = holder.tvTapToView;
        RatingBar ratingBar = holder.ratingBar;
        RelativeLayout rlRating = holder.rlRating;

        tvOrProName.setText(categories.get(position).getTitle());
        tvCatPrice.setText(categories.get(position).getDescription());

        if (categories.get(position).getType().equals("complete")) {
            rlRating.setVisibility(View.VISIBLE);
            tvReview.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);
//            tvTapToView.setVisibility(View.GONE);

        } else {
            rlRating.setVisibility(View.GONE);
            tvReview.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
//            tvTapToView.setVisibility(View.VISIBLE);
        }
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.e(TAG, "onRatingChanged: " + String.valueOf(rating));
            }
        });
        /*ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });*/
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setMessage(context.getResources().getString(R.string.loading_message));
                mProgressDialog.setCancelable(false);

                dialogView = new Dialog(context, R.style.full_screen_dialog);
                dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogView.setContentView(R.layout.dialog_review);
                dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                final RatingBar ratingBarDialog = dialogView.findViewById(R.id.ratingBar);
                TextView tvSendReview = dialogView.findViewById(R.id.tvSendReview);
                ratingBarDialog.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    public void onRatingChanged(RatingBar ratingBar, float rating,
                                                boolean fromUser) {
                        Log.e(TAG, "onRatingChanged: " + String.valueOf(rating));
//                txtRatingValue.setText(String.valueOf(rating));

                    }
                });
                tvSendReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ConnectionDetector.getInstance().isConnectingToInternet(context)) {
                            if (!GineePref.getAccessToken(context).equals("")) {
                                mProgressDialog.show();

                                callAPIForSendReview(categories.get(position).getOrderId() + "", ratingBarDialog.getRating());
                            }

                        } else {
                            Toast.makeText(context, activity.getResources().getString(R.string.no_internet_alert_message) + "", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                dialogView.show();

                return false;
            }
        });
    }

    private void callAPIForSendReview(String order_id, float rating) {
        GineePref.getSharedPreferences(context);
        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("type", "service");
            paramObject.put("item_id", order_id);
            paramObject.put("rating", rating);
            paramObject.put("description", "awesome service !!");

            RequestBody body =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramObject.toString());

            Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().sendRating(GineePref.getAccessToken(context),
                    body);
            getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }

                    if (response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getBoolean("success") == true) {
                                Toast.makeText(context, "HEY, Thanks for your valuable feedback..", Toast.LENGTH_LONG).show();
                                if (dialogView != null && dialogView.isShowing()) {
                                    dialogView.dismiss();
                                }
                                Intent mainIntent = new Intent(context, HomeActivity.class);
                                context.startActivity(mainIntent);
//                                finish();
                            } else {
                                Toast.makeText(context, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, context.getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        try {
                            Log.e(TAG, "onResponse: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(context, context.getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(context, context.getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
