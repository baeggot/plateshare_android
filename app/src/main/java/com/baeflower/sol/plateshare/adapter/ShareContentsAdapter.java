package com.baeflower.sol.plateshare.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baeflower.sol.plateshare.R;
import com.baeflower.sol.plateshare.activity.share.ShareDetailActivity;
import com.baeflower.sol.plateshare.listener.AnimateFirstDisplayListener;
import com.baeflower.sol.plateshare.model.ShareInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by sol on 2015-06-10.
 */
public class ShareContentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ShareContentsAdapter.class.getSimpleName();

    private final int VIEW_ITEM = 1; // 데이터 출력
    private final int VIEW_PROG = 0; // progress item 출력



    private void showLog(String message) {
        Log.d(TAG, message);
    }


    private Context mContext;

    private List<ShareInfo> mDataList;
    private SimpleDateFormat mSf = new SimpleDateFormat("yyyy/MM/dd");

    // Android Universal Image Loader
    private DisplayImageOptions mDisplayImageOptions;

    // 리스트 뷰 스크롤에 쓰이는 변수들
    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = false; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 1; // The minimum amount of items to have below your current scroll position before loading more.

    // 인터페이스
    private OnLoadMoreListener onLoadMoreListener;

    public interface OnLoadMoreListener{
        void onLoadMore();

    }
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }


    // 생성자
    public ShareContentsAdapter(Context context, List<ShareInfo> shareInfoList, RecyclerView recyclerView) {

        mContext = context;
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

                    if (loading == false && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });

        } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) { // grid 는 어케 함

            final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    showLog("onScrolled()");

                    int totalItemCount = gridLayoutManager.getItemCount();
                    int lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();

                    showLog("totalItemCount : " + String.valueOf(totalItemCount));
                    showLog("lastVisibleItem : " + String.valueOf(lastVisibleItem));
                    showLog(String.valueOf(loading));

                    if (loading == false && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
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


        // -------------------- Android Universal Image Loader 세팅
        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .threadPoolSize(3)
                .diskCacheExtraOptions(480, 320, null)
            .build();
        ImageLoader.getInstance().init(config);

    }


    // ShareViewHolder 패턴
    // 각각의 data item 의 뷰에 대한 reference 를 제공
    // 하나의 데이터에 대한 모든 view 를 viewHolder 에서 접근할 수 있어야 한다
    public static class ShareViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private TextView tvLocation;
        private TextView tvDday;
        private ImageView ivSharePhoto;
        private CardView cvShare;

        public ShareViewHolder(View v) {
            super(v);
            Log.d(TAG, "ShareViewHolder 생성자");

            tvTitle = (TextView) v.findViewById(R.id.tv_list_items_share_title);
            tvLocation = (TextView) v.findViewById(R.id.tv_list_items_share_location);
            tvDday = (TextView) v.findViewById(R.id.tv_list_items_share_dday);
            ivSharePhoto = (ImageView) v.findViewById(R.id.iv_list_items_share_image);
            cvShare = (CardView) v.findViewById(R.id.card_view_share);
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
        public ImageView getIvSharePhoto() {
            return ivSharePhoto;
        }

        public CardView getCvShare() {
            return cvShare;
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

    private String mImageUrl = "http://plateshare.kr/php/share/resources/";
    private ImageLoadingListener mAnimateFirstListener = new AnimateFirstDisplayListener();

    // Replace the contents of a view (invoked by the layout manager)
    // getView() 같은 기능
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        showLog("onBindViewHolder()");

        if (viewHolder instanceof ShareViewHolder){
            ((ShareViewHolder) viewHolder).getTvTitle().setText(mDataList.get(position).getmTitle());
            ((ShareViewHolder) viewHolder).getTvLocation().setText(mDataList.get(position).getmTextLocation());

            String dDay = mSf.format(mDataList.get(position).getmDday());
            ((ShareViewHolder) viewHolder).getTvDday().setText(dDay);

            // 이미지
            // url, imageView, display image options, animateFirstListener
            ImageLoader.getInstance().displayImage(
                    mImageUrl + mDataList.get(position).getmImageName()
                    , ((ShareViewHolder) viewHolder).getIvSharePhoto()
                    , mDisplayImageOptions
                    , mAnimateFirstListener);

            // 카드뷰
            ((ShareViewHolder) viewHolder).getCvShare().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLog("cardView click");

                    Intent shareDetailIntent = new Intent(mContext, ShareDetailActivity.class);
                    shareDetailIntent.putExtra("shareInfo", mDataList.get(position));
                    mContext.startActivity(shareDetailIntent);
                }
            });


        } else {
            ((ProgressViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    /*
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
*/

    public void setLoaded(){
        loading = false;
    }



}
