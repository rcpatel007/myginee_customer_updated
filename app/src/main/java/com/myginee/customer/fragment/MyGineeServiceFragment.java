package com.myginee.customer.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myginee.customer.R;
import com.myginee.customer.adapter.MygeniePagerAdapter;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyGineeServiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyGineeServiceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View view;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ViewPager viewPager;
    MygeniePagerAdapter tabsPagerAdapter;
    TabLayout tabs;
    public static String searchTxt = "";
    public MyGineeServiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyGineeServiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyGineeServiceFragment newInstance(String param1, String param2) {
        MyGineeServiceFragment fragment = new MyGineeServiceFragment();
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_ginee_service, container, false);
        init();
        return view;
    }

    private void init() {
        tabsPagerAdapter = new MygeniePagerAdapter(getActivity(), getChildFragmentManager());
        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(tabsPagerAdapter);
        tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    //Receive message
    public void youGotMsg(String msg) {

        searchTxt = msg;
        tabsPagerAdapter.notifyDataSetChanged();
        Log.e("search for", msg+"");
    }

    @Override
    public void onResume() {
        super.onResume();
        searchTxt = "";
        tabsPagerAdapter.notifyDataSetChanged();
    }

}
