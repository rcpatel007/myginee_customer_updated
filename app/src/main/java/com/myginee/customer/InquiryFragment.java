package com.myginee.customer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.activity.SignInActivity;
import com.myginee.customer.adapter.ImageListAdapter;
import com.myginee.customer.adapter.ServiceAdapter;
import com.myginee.customer.fragment.ReviewFragment;
import com.myginee.customer.model.ServiceListModel;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.GineePref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class InquiryFragment extends Fragment {
    TextView tvProceedBook, tvCategoryDesc, tvDate, tvTime;
    EditText etDesription;
    View root;
    String sub_category_name = "", sub_category_id = "", service_id = "";
    Toolbar toolbar;
    Calendar myCalendar;
    RecyclerView rvServicesCat;
    ImageListAdapter imageListAdapter;
    TextView tvAddressName, txtAddAddress;
    private List<ServiceListModel.ServiceList> mModelList;
    private List<String> mServiceList;
    ServiceAdapter mAdapter;
    private ProgressDialog mProgressDialog;
    JSONArray serviceArray = new JSONArray();
    LinearLayout llAddress, llServices;
    ImageView imgBack;

    public InquiryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.activity_inquiry_form, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        Bundle bundle = getArguments();
        sub_category_name = bundle.getString("sub_category_name");
        sub_category_id = bundle.getString("sub_category_id");
        service_id = bundle.getString("service_id");
        tvProceedBook = root.findViewById(R.id.tvProceedBook);
        tvCategoryDesc = root.findViewById(R.id.tvCategoryDesc);
        tvDate = root.findViewById(R.id.tvDate);
        tvTime = root.findViewById(R.id.tvTime);
        etDesription = root.findViewById(R.id.etDesription);
        rvServicesCat = root.findViewById(R.id.rvServicesCat);
        toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        llAddress = (LinearLayout) root.findViewById(R.id.llAddress);
        llServices = (LinearLayout) root.findViewById(R.id.llServices);
        tvAddressName = (TextView) root.findViewById(R.id.tvAddressName);
        txtAddAddress = (TextView) root.findViewById(R.id.txtAddAddress);
        imgBack = (ImageView) root.findViewById(R.id.imgBack);
        myCalendar = Calendar.getInstance();
        mModelList = new ArrayList<ServiceListModel.ServiceList>();
        mServiceList = new ArrayList<>();
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
//        if(!GineePref.getAddress(getActivity()).equals("")){
            llAddress.setVisibility(View.VISIBLE);
            txtAddAddress.setVisibility(View.VISIBLE);
            tvAddressName.setText(GineePref.getAddress(getActivity())+"");
//        }else {
//            llAddress.setVisibility(View.GONE);
//            txtAddAddress.setVisibility(View.VISIBLE);
//        }
        rvServicesCat.setHasFixedSize(true);
        rvServicesCat.setNestedScrollingEnabled(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL,
                false);
        rvServicesCat.setLayoutManager(linearLayoutManager);
        imageListAdapter = new ImageListAdapter(getActivity(), mServiceList);
        rvServicesCat.setAdapter(imageListAdapter);

        getSubCategoryServiceByIDAPICall(sub_category_id);
        tvCategoryDesc.setText(sub_category_name);
        tvDate.setText(getDate());
        tvTime.setText(getTime());

        txtAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MapsActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        tvProceedBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(mServiceList.size() > 0){
                if(!GineePref.getAccessToken(getActivity()).equals("")) {
                    callAPIForAddServiceToCart();
                }else {
                    Intent signOutIntent = new Intent(getActivity(), SignInActivity.class);
                    getActivity().startActivity(signOutIntent);
                    getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }

            }else {
                Toast.makeText(getActivity(), "Please enter valid data", Toast.LENGTH_SHORT).show();
            }


            }
        });
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), R.style.DialogTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        // Initially we set the variable value to AM
                        String status = "AM";

                        if (selectedHour > 11) {
                            // If the hour is greater than or equal to 12
                            // Then the current AM PM status is PM
                            status = "PM";
                        }

                            tvTime.setText(String.format("%02d",selectedHour) + ":" + String.format("%02d", selectedMinute)
                                    + " " + status);

                    }
                }, hour, minute, true);//Yes 24 hour time
//                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        llServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialogView = new Dialog(getActivity(), R.style.full_screen_dialog);
                dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogView.setContentView(R.layout.dialog_service);
                dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                RecyclerView mRecyclerView = dialogView.findViewById(R.id.rvService);
                ImageView imgCancel = (ImageView) dialogView.findViewById(R.id.imgCancel);
                TextView tvCancel = (TextView) dialogView.findViewById(R.id.tvCancel);
                TextView tvSelect = (TextView) dialogView.findViewById(R.id.tvSelect);
                mAdapter = new ServiceAdapter(getActivity(), mModelList);
                LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(manager);
                mRecyclerView.setAdapter(mAdapter);
                dialogView.show();

                imgCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogView.dismiss();
                    }
                });
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogView.dismiss();
                    }
                });
                tvSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String text = "";
                        serviceArray = new JSONArray();
                        mServiceList.clear();
                        for (ServiceListModel.ServiceList model : mModelList) {

                            if (model.getIs_selected()) {
                                text += " " + model.getServiceName() + " ";
                                mServiceList.add(model.getServiceName());
                                createJsonArray(model.getServiceId(), model.getServiceName(), model.getMeasurement(), model.getPrice());
                                Log.d("TAG", "Output : " + text + " ");

                            }

                        }
                        if(mServiceList.size() > 0){
//                            spinnerServices.setText(text + ",  ");
                            imageListAdapter.notifyDataSetChanged();

                        }else {
//                            imageListAdapter.notifyDataSetChanged();
//                            spinnerServices.setText(text + " ");
                        }

                        dialogView.dismiss();

                    }
                });
            }
        });
        return root;
    }

    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm a");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void callAPIForAddServiceToCart() {
        mProgressDialog.show();

        GineePref.getSharedPreferences(getActivity());
        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("category_id", service_id);
            paramObject.put("subcategory_id", sub_category_id);
            paramObject.put("service_list", serviceArray);

            paramObject.put("description", etDesription.getText().toString());
            paramObject.put("service_provision_date", getTimeInMilliSec(tvDate.getText().toString() + " " + tvTime.getText().toString()));

            RequestBody body =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramObject.toString());

            Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().saveCartDetail(GineePref.getAccessToken(getActivity()),
                    body);
            getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mProgressDialog.dismiss();
                    if (response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getBoolean("success") == true) {

                                ReviewFragment reviewFragment = new ReviewFragment();
                                Bundle bundles = new Bundle();
                                bundles.putSerializable("sub_category_id", sub_category_id);
                                bundles.putSerializable("sub_category_name", sub_category_name);
                                bundles.putSerializable("service_id", service_id);
                                bundles.putSerializable("service_array", serviceArray.toString());
                                bundles.putSerializable("date", tvDate.getText().toString());
                                bundles.putSerializable("time", tvTime.getText().toString());
                                bundles.putSerializable("description", etDesription.getText().toString());

                                reviewFragment.setArguments(bundles);
                                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                                        R.anim.slide_in,  // enter
                                        R.anim.fade_out,  // exit
                                        R.anim.fade_in,   // popEnter
                                        R.anim.slide_out  // popExit
                                ).replace(R.id.frame_container, reviewFragment)
                                        .addToBackStack(reviewFragment.getClass().getName()).commit();
                            } else {
                                Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private long getTimeInMilliSec(String dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yy HH:mm a");
        long timeInMilliseconds = 0;
        try {
            Date mDate = sdf.parse(dateTime);
            timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    private JSONArray createJsonArray(String serviceId, String serviceName, String measurement, String price) {

        JSONObject jsonService = new JSONObject();
//        JSONArray jsonServiceArray = new JSONArray();
        try {
            jsonService.put("service_id", serviceId);
            jsonService.put("service_name", serviceName);
            jsonService.put("measurement", measurement);
            jsonService.put("price", price);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return serviceArray.put(jsonService);
    }

    private void getSubCategoryServiceByIDAPICall(String category_id) {
        mProgressDialog.show();
        GineePref.getSharedPreferences(getActivity());
        Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().getSubCatServiceByID(GineePref.getAccessToken(getActivity()),
                category_id);
        getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            if (jsonObject.getJSONObject("data") != null) {
                                JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                                JSONArray serviceArray = jsonObjectData.getJSONArray("service_list");
                                if (serviceArray.length() > 0) {
                                    for (int i = 0; i < serviceArray.length(); i++) {
                                        JSONObject item = serviceArray.getJSONObject(i);
                                        mModelList.add(new ServiceListModel.ServiceList(
                                                item.getString("service_id"),
                                                item.getString("service_name"),
                                                item.getString("measurement"),
                                                item.getString("price"), false,
                                                item.getString("mrp"),
                                                item.getString("description"),
                                                item.getString("image_url"),
                                                item.getBoolean("is_added_to_cart")
                                                ));
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(getActivity(), "No Service found", Toast.LENGTH_LONG).show();

                            }
                        } else {
                            Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void updateLabel() {
        String myFormat = "dd/MMM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tvDate.setText(sdf.format(myCalendar.getTime()));
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    @Override
    public void onResume() {
        super.onResume();
//        serviceArray = new JSONArray(new ArrayList<String>());
//        if(!GineePref.getAddress(getActivity()).equals("")){
            llAddress.setVisibility(View.VISIBLE);
            txtAddAddress.setVisibility(View.VISIBLE);
            tvAddressName.setText(GineePref.getAddress(getActivity())+"");
//        }else {
//            llAddress.setVisibility(View.GONE);
//            txtAddAddress.setVisibility(View.VISIBLE);
//        }

    }

}
