package com.baeflower.sol.plateshare.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baeflower.sol.plateshare.R;
import com.baeflower.sol.plateshare.model.ShareInfo;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by sol on 2015-06-10.
 */
public class ShareContentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ShareContentsAdapter";


    private void showLog(String message) {
        Log.d(TAG, message);
    }


    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<ShareInfo> mDataList;
    private String[] mDataArr; // test

    private boolean isFooterEnabled = true;

    private SimpleDateFormat mSf = new SimpleDateFormat("yyyy/MM/dd");

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = false; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 1; // The minimum amount of items to have below your current scroll position before loading more.

    private OnLoadMoreListener onLoadMoreListener;

    public interface OnLoadMoreListener{
        void onLoadMore();

    }
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }


    // 생성자
    public ShareContentsAdapter(List<ShareInfo> shareInfoList, RecyclerView recyclerView) {
        mDataList = shareInfoList;

        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    showLog("onScrolled()");

                    int totalItemCount = linearLayoutManager.getItemCount();
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    showLog("totalItemCount : " + String.valueOf(totalItemCount));
                    showLog("lastVisibleItem : " + String.valueOf(lastVisibleItem));
                    showLog(String.valueOf(loading));

                    if (loading == false && totalItemCount <= (lastVisibleItem + visibleThreshold + 1)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }

    }

    // 생성자
    public ShareContentsAdapter(List<ShareInfo> dataList) {
        showLog("shareContentAdapter 생성자");

        mDataList = dataList;
    }

    // 생성자 : test
    public ShareContentsAdapter(String[] dataArr) {
        mDataArr = dataArr;
    }


    // ShareViewHolder 패턴
    // 각각의 data item 의 뷰에 대한 reference 를 제공
    // 하나의 데이터에 대한 모든 view 를 viewHolder 에서 접근할 수 있어야 한다
    public static class ShareViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private TextView tvLocation;
        private TextView tvDday;

        public ShareViewHolder(View v) {
            super(v);
            Log.d(TAG, "ShareViewHolder 생성자");

            tvTitle = (TextView) v.findViewById(R.id.tv_list_items_share_title);
            tvLocation = (TextView) v.findViewById(R.id.tv_list_items_share_location);
            tvDday = (TextView) v.findViewById(R.id.tv_list_items_share_dday);

            // Define click listener for the ShareViewHolder's View
            /*
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            */
        }

        public TextView getTvTitle() {
            return tvTitle;
        }
        public TextView getTvLocation() {
            return tvLocation;
        }
        public TextView getTvDday() {
            return tvDday;
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);

            progressBar = (ProgressBar) v.findViewById(R.id.bottom_progressBar);
        }
    }


    @Override
    public int getItemViewType(int position) {
//        return (isFooterEnabled && position >= mDataList.size()) ? VIEW_PROG : VIEW_ITEM;
        return mDataList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        showLog("onCreateViewHolder()");

        // 새로운 view 생성
        // View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_share, parent, false);
        // set the view's size, margins, paddings and layout parameters

        RecyclerView.ViewHolder vh;
        if(viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_share, parent, false);
            vh = new ShareViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
//        return new ShareViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    // getView() 같은 기능
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        showLog("onBindViewHolder()");

        /*
        // Get element from your dataset at this position and replace the contents of the view with that element
        viewHolder.getTvTitle().setText(mDataList.get(position).getmTitle());
        viewHolder.getTvLocation().setText(mDataList.get(position).getmLocation());

        String dDay = mSf.format(mDataList.get(position).getmDday());
        viewHolder.getTvDday().setText(dDay);
        */

        if (viewHolder instanceof ShareViewHolder){
            ((ShareViewHolder) viewHolder).getTvTitle().setText(mDataList.get(position).getmTitle());
            ((ShareViewHolder) viewHolder).getTvLocation().setText(mDataList.get(position).getmLocation());

            String dDay = mSf.format(mDataList.get(position).getmDday());
            ((ShareViewHolder) viewHolder).getTvDday().setText(dDay);
        } else {
            ((ProgressViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        showLog("getItemCount() : " + String.valueOf(isFooterEnabled));
//        return (isFooterEnabled) ? mDataList.size() + 1 : mDataList.size();
        return mDataList.size();
    }

    /*
        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            showLog("onAttachedToRecyclerView()");
            super.onAttachedToRecyclerView(recyclerView);
        }
    */

    public void setLoaded(){
        loading = false;
    }

//    public void enableFooter(boolean isEnabled){
//        this.isFooterEnabled = isEnabled;
//    }

    /*
    public void add(ViewModel item, int position) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(ViewModel item) {
        int position = items.indexOf(item);
        items.remove(position);
        notifyItemRemoved(position);
    }
    */

}
