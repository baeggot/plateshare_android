package com.baeflower.sol.plateshare.activity.share;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baeflower.sol.plateshare.R;
import com.baeflower.sol.plateshare.adapter.ShareDetailAdapter;
import com.baeflower.sol.plateshare.listener.HidingScrollListener;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ShareDetailActivity extends ActionBarActivity implements OnMapReadyCallback {

    private static final String TAG = ShareDetailActivity.class.getSimpleName();

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showLog(String message) {
        Log.d(TAG, message);
    }


    private ShareDetailAdapter mAdapter;
    private Toolbar mToolbar;


    //
    private ShareInfo mShareDetail;
    private SimpleDateFormat mSf;


    //
    private TextView mTvTitle;
    private TextView mTvWriter;
    private TextView mTvDday;
    private ImageView mIvPhoto;
    private TextView mTvExplanation;
    private TextView mTvTextLocation;

    // Map
    private MapFragment mMapFragment;


    // Android Universal Image Loader
    private DisplayImageOptions mDisplayImageOptions;


    private void init() {
        mTvTitle = (TextView) findViewById(R.id.tv_share_detail_title);
        mTvWriter = (TextView) findViewById(R.id.tv_share_detail_writer);
        mTvDday = (TextView) findViewById(R.id.tv_share_detail_dday);
        mIvPhoto = (ImageView) findViewById(R.id.iv_share_detail_photo);
        mTvExplanation = (TextView) findViewById(R.id.tv_share_detail_explanation);
        mTvTextLocation = (TextView) findViewById(R.id.tv_share_detail_text_location);


        // -------------------- Android Universal Image Loader 세팅
        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3)
                .diskCacheExtraOptions(480, 320, null)
                .build();
        ImageLoader.getInstance().init(config);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_detail);

        init();

        // ----------------------------- Tool Bar
        // AndroidManifest.xml -> 해당 activity -> NoActionBar 테마설정
        initToolbar();

        if (getIntent() != null) {

            // -------------------------- 데이터
            mShareDetail = getIntent().getParcelableExtra("shareInfo");

            List<ShareInfo> dataList = new ArrayList<>();
            dataList.add(mShareDetail); // RecyclerView 쓰는거라서.. 데이터는 1개 뿐

            initRecyclerView(dataList);

            /*
            mTvTitle.setText(mShareDetail.getmTitle()); // 글제목
            mTvWriter.setText(PSContants.USERINFO.getmEmail()); // 작성자

            mSf = new SimpleDateFormat("yyyy/MM/dd");
            mTvDday.setText(mSf.format(mShareDetail.getmDday())); // 디데이

            mTvTextLocation.setText(mShareDetail.getmTextLocation()); // 위치

            // 이미지
            // url, imageView, display image options, animateFirstListener
            ImageLoader.getInstance().displayImage(
                    mImageUrl + mShareDetail.getmImageName()
                    , mIvPhoto
                    , mDisplayImageOptions
                    , mAnimateFirstListener);

            mTvExplanation.setText(mShareDetail.getmExplanation()); // 내용

            // -------------------------- Map
            mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment_google_map_share_detail);
            mMapFragment.getMapAsync(this);
*/

        }

    } // onCreate


    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.tb_share_detail_title);
        setSupportActionBar(mToolbar);
        setTitle(getString(R.string.app_name));
//        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 화살표

//        getSupportActionBar().setShowHideAnimationEnabled(true); // 머하는애야?
    }


    private void initRecyclerView(List<ShareInfo> dataList) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_share_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ShareDetailAdapter recyclerAdapter = new ShareDetailAdapter(dataList, this);
        recyclerView.setAdapter(recyclerAdapter);

        // -------------------- Recycler View
        final boolean isFirstSet = true;

        recyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }
            @Override
            public void onShow() {
                showViews();
            }
        });

        /*
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                    mToolbar.setAlpha(0.8f);
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: // Toolbar Title 옆에 화살표
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 옆으로 밀면서 사라지는 애니메이션
        // this.overridePendingTransition(R.anim.finish_right_animation, R.anim.finish_left_animation);
    }


    private void hideViews() {
        /*
        mToolbar.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFabButton.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        mFabButton.animate().translationY(mFabButton.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
        */
    }


    private void showViews() {
        /*
        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        mFabButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        */
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


    } // onMapReady





}
