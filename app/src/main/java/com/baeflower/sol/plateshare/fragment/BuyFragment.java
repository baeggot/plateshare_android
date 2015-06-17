package com.baeflower.sol.plateshare.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baeflower.sol.plateshare.R;
import com.baeflower.sol.plateshare.adapter.ShareContentsAdapter;
import com.baeflower.sol.plateshare.phpUtil.SelectCountContentsByUnivPhp;
import com.software.shell.fab.ActionButton;

/**
 * Created by sol on 2015-06-11.
 */
public class BuyFragment extends Fragment {


    private static final String TAG = BuyFragment.class.getSimpleName() + "_";

    private final int BUY = 1;

    private void showLog(String message) {
        Log.d(TAG, message);
    }

    private static final int SPAN_COUNT = 2;

    private ShareContentsAdapter mRVAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ActionButton mActionButton;

    private OnDataChangeListener mListener;
    private SelectCountContentsByUnivPhp mSelectContentsPhp;
    private String mUniv;

    public enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER,
        STAGGERED_LAYOUT_MANAGER
    }


    public static BuyFragment newInstance(String univ) {
        BuyFragment fragment = new BuyFragment();
        Bundle args = new Bundle();
        args.putString("univ", univ);
        fragment.setArguments(args);
        return fragment;
    }


    public interface OnDataChangeListener {
        void onDataChanged();

    }


    public void setOnDataChangeListener(OnDataChangeListener listener) {
        mListener = listener;
    }


    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLog("onCreate()");

        mUniv = getArguments().getString("univ");

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        showLog("buy_onCreateView()");

        View rootView = inflater.inflate(R.layout.fragment_buy, container, false);

        // -------------------- Floating Button
        mActionButton = (ActionButton) rootView.findViewById(R.id.action_button_plus_buy);
        mActionButton.setButtonColor(getResources().getColor(R.color.blue));
        mActionButton.setButtonColorPressed(getResources().getColor(R.color.blue_pressed));
        mActionButton.setImageResource(R.drawable.fab_plus_icon);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("floating button click");
            }
        });


        // -------------------- Recycler View
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_buy);
        mRecyclerView.setHasFixedSize(false); // 레이아웃 사이즈가 변하지 않을거면 true

        // Linear 방향으로 Recycler View 사용
        mLayoutManager = new LinearLayoutManager(getActivity());

        // setLayout
        // savedInstanceState (이것도 완벽하진 않지만...)
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRecyclerViewLayoutManager(LayoutManagerType.STAGGERED_LAYOUT_MANAGER);
        }
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // mRVAdapter = new ShareContentsAdapter(new String[]{ "헤이요", "김밥에김이없데", "요요요", "촛불하나", "목이", "간지러" }, BUY);
        // mRecyclerView.setAdapter(mRVAdapter);

        return rootView;
    }

    private void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        showLog("setRecyclerViewLayoutManager");

        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll
        // position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                break;
            case STAGGERED_LAYOUT_MANAGER:
                mLayoutManager = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
                break;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }


}
