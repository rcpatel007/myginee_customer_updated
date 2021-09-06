package com.myginee.customer.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.R;
import com.myginee.customer.adapter.NotifyAdapter;
import com.myginee.customer.model.Notification;
import com.myginee.customer.utils.GineePref;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "NotificationActivity";
    ArrayList<Notification> notificationArrayList;
    NotifyAdapter notifyAdapter;
    RecyclerView serviceListCategory;
    private ProgressDialog mProgressDialog;
    TextView tvNotFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mProgressDialog = new ProgressDialog(NotificationActivity.this);
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        GineePref.getSharedPreferences(NotificationActivity.this);

        serviceListCategory = findViewById(R.id.rvNotificationList);
        tvNotFound = findViewById(R.id.tvNotiNotFound);

        serviceListCategory.setHasFixedSize(true);
        serviceListCategory.setNestedScrollingEnabled(false);
        notificationArrayList = new ArrayList<Notification>();

//        notificationArrayList = GineePref.getArrayList(NotificationActivity.this);


//        if(notificationArrayList.size() > 0){
//            tvNotFound.setVisibility(View.GONE);
//            serviceListCategory.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NotificationActivity.this, LinearLayoutManager.VERTICAL,
                false);
        serviceListCategory.setLayoutManager(linearLayoutManager);
//            notifyAdapter = new NotifyAdapter(NotificationActivity.this, NotificationActivity.this, notificationArrayList);
        serviceListCategory.setAdapter(notifyAdapter);

//        }else {
//            tvNotFound.setVisibility(View.VISIBLE);
//            serviceListCategory.setVisibility(View.GONE);
//        }
//

    }
}
