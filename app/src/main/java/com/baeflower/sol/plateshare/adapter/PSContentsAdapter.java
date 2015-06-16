package com.baeflower.sol.plateshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baeflower.sol.plateshare.R;
import com.baeflower.sol.plateshare.model.PSContent;

import java.util.List;

/**
 * Created by sol on 2015-06-10.
 */
public class PSContentsAdapter extends RecyclerView.Adapter<PSContentsAdapter.ViewHolder> {

    private static final String TAG = "PSContentsAdapter";

    private List<PSContent> mDataList;
    private String[] mDataArr; // test

    private final int SHARE = 0;
    private final int BUY = 1;
    private int mCurrentFragment;

    // 생성자
    public PSContentsAdapter(List<PSContent> dataList) {
        mDataList = dataList;
    }
    // test 용 생성자
    public PSContentsAdapter(String[] dataArr, int currentFragment) {
        mDataArr = dataArr;
        mCurrentFragment = currentFragment;
    }


    // ViewHolder 패턴
    // 각각의 data item 의 뷰에 대한 reference 를 제공
    // 하나의 데이터에 대한 모든 view 를 viewHolder 에서 접근할 수 있어야 한다
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final int SHARE = 0;
        private final int BUY = 1;


        private TextView textView;

        public ViewHolder(View v, int fragment) {
            super(v);

            if (fragment == SHARE) {
                textView = (TextView) v.findViewById(R.id.tv_list_items_share_title);
            } else {
                textView = (TextView) v.findViewById(R.id.tv_list_items_buy_title);
            }


            // Define click listener for the ViewHolder's View
            /*
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            */

        }

        public TextView getTextView() {
            return textView;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;

        // 새로운 view 생성
        if (mCurrentFragment == SHARE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_share, parent, false);
        } else if (mCurrentFragment == BUY) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_buy, parent, false);
        }

        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(view, mCurrentFragment);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set");

        // Get element from your dataset at this position and replace the contents of the view with that element
        viewHolder.getTextView().setText(mDataArr[position]);
//        viewHolder.getTextView().setText(mDataList.get(position).getmTitle());
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataArr.length;
//        return mDataList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}
