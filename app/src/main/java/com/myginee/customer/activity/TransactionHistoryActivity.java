package com.myginee.customer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myginee.customer.R;
import com.myginee.customer.adapter.TransactionAdapter;
import com.myginee.customer.model.WalletTrans;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.ConnectionDetector;
import com.myginee.customer.utils.GineePref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class TransactionHistoryActivity extends AppCompatActivity {

    private static final String TAG = "TransactionHistoryActivity";
    ArrayList<WalletTrans.Datum> transArrayList;
    TransactionAdapter transactionAdapter;
    RecyclerView serviceListCategory;
    private ProgressDialog mProgressDialog;
    TextView tvNotFound;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        mProgressDialog = new ProgressDialog(TransactionHistoryActivity.this);
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        GineePref.getSharedPreferences(TransactionHistoryActivity.this);

        serviceListCategory =  findViewById(R.id.rvTransList);
        tvNotFound = findViewById(R.id.tvTransNotFound);
        imgBack = findViewById(R.id.imgBack);
        serviceListCategory.setHasFixedSize(true);
        serviceListCategory.setNestedScrollingEnabled(false);
        transArrayList = new ArrayList<WalletTrans.Datum>();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        transArrayList = GineePref.getArrayList(TransactionHistoryActivity.this);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TransactionHistoryActivity.this, LinearLayoutManager.VERTICAL,
                    false);
            serviceListCategory.setLayoutManager(linearLayoutManager);
            transactionAdapter = new TransactionAdapter(TransactionHistoryActivity.this, TransactionHistoryActivity.this, transArrayList);
            serviceListCategory.setAdapter(transactionAdapter);


        if (ConnectionDetector.getInstance().isConnectingToInternet(TransactionHistoryActivity.this)) {
            //Call Api
            if (!GineePref.getAccessToken(TransactionHistoryActivity.this).equals("")) {
                getWalletTransactionsFromAPI();
            }

        } else {
            ConnectionDetector.getInstance().showNoInternetAlert(TransactionHistoryActivity.this);
        }
    }

    private void getWalletTransactionsFromAPI() {
        mProgressDialog.show();
        GineePref.getSharedPreferences(TransactionHistoryActivity.this);
        Call<ResponseBody> getAboutUsCall = GineeAppApi.api().getWalletTransaction(GineePref.getAccessToken(TransactionHistoryActivity.this));
        getAboutUsCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            if (dataArray.length() > 0) {
                                tvNotFound.setVisibility(View.GONE);
                                serviceListCategory.setVisibility(View.VISIBLE);
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject item = dataArray.getJSONObject(i);
                                    transArrayList.add(new WalletTrans.Datum(
                                            item.getBoolean("received"),
                                            item.getBoolean("sent"),
                                            item.getString("_id"),
                                            item.getString("order_id"),
                                            item.getString("customer_id"),
                                            item.getInt("wallet_amount"),
                                            item.getString("created_at"),
                                            item.getString("updated_at"),
                                            item.getInt("__v")));
                                }
                                transactionAdapter.notifyDataSetChanged();
                            }else {
                                tvNotFound.setVisibility(View.VISIBLE);
                                serviceListCategory.setVisibility(View.GONE);
                            }

                        } else {
                            Toast.makeText(TransactionHistoryActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(TransactionHistoryActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(TransactionHistoryActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(TransactionHistoryActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

}
