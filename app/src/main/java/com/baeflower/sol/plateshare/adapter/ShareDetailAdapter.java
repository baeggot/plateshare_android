package com.baeflower.sol.plateshare.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baeflower.sol.plateshare.R;
import com.baeflower.sol.plateshare.listener.AnimateFirstDisplayListener;
import com.baeflower.sol.plateshare.model.ShareInfo;
import com.baeflower.sol.plateshare.util.PSContants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by sol on 2015-06-26.
 */
public class ShareDetailAdapter extends RecyclerView.Adapter<ShareDetailAdapter.ContentViewHolder> implements OnMapReadyCallback {

    private static final String TAG = ShareDetailAdapter.class.getSimpleName();
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_HEADER = 2;


    private void showLog(String message) {
        Log.d(TAG, message);
    }


    private List<ShareInfo> mDataList;
    private Activity mContext;
    private DisplayImageOptions mDisplayImageOptions;
    private SimpleDateFormat mSf;
    private String mImageUrl = "http://plateshare.kr/php/share/resources/";
    private ImageLoadingListener mAnimateFirstListener = new AnimateFirstDisplayListener();


    // 생성자
    public ShareDetailAdapter(List<ShareInfo> mDataList, Activity context) {
        this.mDataList = mDataList;
        mContext = context;

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

    // ViewHolder
    public static class ContentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvWriter;
        private TextView tvDday;
        private ImageView ivPhoto;
        private TextView tvExplanation;
        private TextView tvTextLocation;

        public MapFragment mapFragment;

        public ContentViewHolder(final View parent) {
            super(parent);

            tvTitle = (TextView) parent.findViewById(R.id.tv_share_detail_title);
            tvWriter = (TextView) parent.findViewById(R.id.tv_share_detail_writer);
            tvDday = (TextView) parent.findViewById(R.id.tv_share_detail_dday);
            ivPhoto = (ImageView) parent.findViewById(R.id.iv_share_detail_photo);
            tvExplanation = (TextView) parent.findViewById(R.id.tv_share_detail_explanation);
            tvTextLocation = (TextView) parent.findViewById(R.id.tv_share_detail_text_location);
        }

        /*
        public ContentViewHolder(View v) {
            super(v);

            tvTitle = (TextView) v.findViewById(R.id.tv_share_detail_title);
            tvWriter = (TextView) v.findViewById(R.id.tv_share_detail_writer);
            tvDday = (TextView) v.findViewById(R.id.tv_share_detail_dday);
            ivPhoto = (ImageView) v.findViewById(R.id.iv_share_detail_photo);
            tvExplanation = (TextView) v.findViewById(R.id.tv_share_detail_explanation);
            tvTextLocation = (TextView) v.findViewById(R.id.tv_share_detail_text_location);

        }
        */

        public static ContentViewHolder newInstance(View parent) {
            return new ContentViewHolder(parent);
        }

        public TextView getTvTitle() {
            return tvTitle;
        }

        public TextView getTvWriter() {
            return tvWriter;
        }

        public TextView getTvDday() {
            return tvDday;
        }

        public ImageView getIvPhoto() {
            return ivPhoto;
        }

        public TextView getTvExplanation() {
            return tvExplanation;
        }

        public TextView getTvTextLocation() {
            return tvTextLocation;
        }

    }


    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_share_detail, parent, false);
            return ContentViewHolder.newInstance(view);
        } else if (viewType == TYPE_HEADER) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_header, parent, false);
            return new ContentViewHolder(view);
        }

//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share_detail, parent, false);
//        ContentViewHolder vh = new ContentViewHolder(view);
//        return vh;
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types    correctly");
    }

    // getView()
    @Override
    public void onBindViewHolder(ContentViewHolder holder, int position) {

        if (isPositionHeader(position) == false) {
            ShareInfo shareInfo = mDataList.get(position - 1);

            mSf = new SimpleDateFormat("yyyy/MM/dd");

            holder.getTvTitle().setText(shareInfo.getmTitle());
            holder.getTvWriter().setText(PSContants.USERINFO.getmEmail());
            holder.getTvDday().setText(mSf.format(shareInfo.getmDday()));

            ImageLoader.getInstance().displayImage(
                    mImageUrl + shareInfo.getmImageName()
                    , holder.getIvPhoto()
                    , mDisplayImageOptions
                    , mAnimateFirstListener);

            holder.getTvExplanation().setText(shareInfo.getmExplanation());
            holder.getTvTextLocation().setText(shareInfo.getmTextLocation());

            // 현재 등록메뉴에서 text_location 이나 latitude, longitude 를 새로 받고 있지 않기 때문에 사용자정보로 저장된 것을 쓴다

            holder.mapFragment = (MapFragment) mContext.getFragmentManager().findFragmentById(R.id.fragment_google_map_share_detail);
            holder.mapFragment.getMapAsync(this);
        }

    }

    @Override
    public int getItemCount() {
        return getBasicItemCount() + 1; // Header
    }

    public int getBasicItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        final LatLng mapAddress = new LatLng(PSContants.USERINFO.getmLatitude(), PSContants.USERINFO.getmLongitude());

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapAddress, 15)); // 해당 위치로 지도 이동

        MarkerOptions markerOptions = new MarkerOptions();
        // markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon01));
        markerOptions.position(mapAddress);
        markerOptions.title("여기근처");
        googleMap.addMarker(markerOptions); // 마커 추가

        final GoogleMap constantGoogleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                constantGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapAddress, 15)); // 해당 위치로 지도 이동
                return false;
            }
        });

    }


    private boolean isPositionHeader(int position) {
        return position == 0;
    }



}
