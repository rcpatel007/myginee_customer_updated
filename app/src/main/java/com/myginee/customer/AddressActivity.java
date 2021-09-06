package com.myginee.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.myginee.customer.utils.GineePref;

public class AddressActivity extends AppCompatActivity {
    LinearLayout llMap;
    ImageView imgBack;
    TextView tvOffice, tvAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        llMap = findViewById(R.id.llMap);
        imgBack = findViewById(R.id.imgBack);
        tvOffice = findViewById(R.id.tvOffice);
        tvAdd = findViewById(R.id.tvAdd);
        GineePref.getSharedPreferences(AddressActivity.this);
        tvOffice.setText(GineePref.getAddressLoc(AddressActivity.this));
        tvAdd.setText(GineePref.getAddress(AddressActivity.this));

        llMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddressActivity.this, MapsActivity.class));
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onResume() {
        tvOffice.setText(GineePref.getAddressLoc(AddressActivity.this));
        tvAdd.setText(GineePref.getAddress(AddressActivity.this));

        super.onResume();
    }
}
