package com.baeflower.sol.plateshare.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baeflower.sol.plateshare.R;


public class ShareDetailFragment extends Fragment {



    public static ShareDetailFragment newInstance(int position) {
        ShareDetailFragment f = new ShareDetailFragment();
        Bundle b = new Bundle();
        // b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // position = getArguments().getInt(ARG_POSITION);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_share_detail, container, false);


        return rootView;
    }

}