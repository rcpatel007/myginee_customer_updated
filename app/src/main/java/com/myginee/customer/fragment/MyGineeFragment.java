package com.myginee.customer.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.myginee.customer.AddressActivity;
import com.myginee.customer.R;
import com.myginee.customer.activity.TransactionHistoryActivity;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.GineePref;
import com.myginee.customer.utils.IFragmentToActivity;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyGineeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyGineeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View view;
    TextView tvAddress, tvWallet;
    EditText etSearchServices;
    IFragmentToActivity commander;

    public MyGineeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyGineeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyGineeFragment newInstance(String param1, String param2) {
        MyGineeFragment fragment = new MyGineeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_ginee, container, false);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvWallet = view.findViewById(R.id.tvWallet);
        tvWallet = view.findViewById(R.id.tvWallet);
        etSearchServices = view.findViewById(R.id.etSearchServices);

        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddressActivity.class));
            }
        });
        tvWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TransactionHistoryActivity.class));
            }
        });
        MyGineeServiceFragment categoryFragment = new MyGineeServiceFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragMyGinee, categoryFragment)
                .addToBackStack(categoryFragment.getClass().getName()).commit();
        etSearchServices.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Your piece of code on keyboard search click
                    if(etSearchServices.getText().toString().length() > 0){

                        String message = etSearchServices.getText().toString() +
                                "";
                        commander.showToast(message);
                        etSearchServices.setText("");
                    }else {
                        Toast.makeText(getActivity(), "please enter product", Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
                return false;
            }
        });
//        init();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Try to cast the context to our interface SendMessageListener i.e. check whether the
        // activity implements the SendMessageListener. If not a ClassCastException is thrown.
        try {
            commander = (IFragmentToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement the SendMessageListener interface");
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if(!GineePref.getAccessToken(getActivity()).equals("")) {
            getWalletAmount();
        }
    }

    private void getWalletAmount() {

        Call<ResponseBody> userRegister = GineeAppApi.api().getUserByID(GineePref.getAccessToken(getActivity()),
                GineePref.getUSERID(getActivity()));
        userRegister.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            JSONObject jsonData = jsonObject.getJSONObject("data");
                            GineePref.setWalletPrice(getActivity(),
                                    jsonData.getInt("wallet_amount"));
                            tvWallet.setText(jsonData.getInt("wallet_amount")+"");
                        } else {
//                            Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
//                        Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }

                } else {
//                    Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
            }
        });

    }


}
