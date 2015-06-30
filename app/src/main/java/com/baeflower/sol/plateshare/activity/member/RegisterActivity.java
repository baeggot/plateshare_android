package com.baeflower.sol.plateshare.activity.member;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baeflower.sol.plateshare.R;
import com.baeflower.sol.plateshare.model.UserInfo;
import com.baeflower.sol.plateshare.util.JSONParser;
import com.baeflower.sol.plateshare.util.PSContants;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends ActionBarActivity
        implements View.OnClickListener, OnMapReadyCallback {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final int TURN_ON_GPS = 0;

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showLog(String message) {
        Log.d(TAG, message);
    }


    // map
    private MapFragment mMapFragment;
    private LocationManager mLocationManager;
    private String mProvider;
    private GoogleMap mGoogleMap;
    private LatLng mMarkerLatLng;


    //
    private EditText mEtEmail;
    private EditText mEtPassword;
    private EditText mEtPasswordConfirm;
    private EditText mEtUniv;
    private EditText mEtLocationText;

    private EditText mEtSearchLocation;
    private ImageButton mIBtnSearch;

    private Button mBtnMemberRegister;

    private InsertMemberPhp mInsertMemberPhp;



    private void init() {
        mEtEmail = (EditText) findViewById(R.id.et_member_email);
        mEtPassword = (EditText) findViewById(R.id.et_member_password);
        mEtPasswordConfirm = (EditText) findViewById(R.id.et_member_password_confirm);
        mEtUniv = (EditText) findViewById(R.id.et_member_univ);
        mEtLocationText = (EditText) findViewById(R.id.et_member_text_location);

        mEtSearchLocation = (EditText) findViewById(R.id.et_member_gps_location);
        mIBtnSearch = (ImageButton) findViewById(R.id.ib_map_search);
        mIBtnSearch.setOnClickListener(this);

        mBtnMemberRegister = (Button) findViewById(R.id.btn_member_register);
        mBtnMemberRegister.setOnClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();


        // ----------------------- Map
        // 코드로 MapFragment 붙이기
        // mMapFragment = MapFragment.newInstance();
        // FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // fragmentTransaction.add(R.id.my_container, mMapFragment);
        // fragmentTransaction.commit();

        // getSupportFragmentManager 쓰면 에러남 - why?
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment_google_map_register);
        // mMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_google_map_register)).getMap();

        mMapFragment.getMapAsync(this);



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // 위치설정 activity 종료
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case TURN_ON_GPS:
                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                mProvider = mLocationManager.getBestProvider(criteria, true);
                if (mProvider == null){//사용자가 위치설정동의 안했을때 종료
                    finish();
                } else {//사용자가 위치설정 동의 했을때
                    // mLocationManager.requestLocationUpdates(mProvider, 1L, 2F, RegisterActivity.this);
                    mGoogleMap.setMyLocationEnabled(true);
                }
                break;
        }
    }


    private LatLng mMapAddress = new LatLng(36.5651, 128.98955);

    @Override
    public void onMapReady(GoogleMap googleMap) {
        showLog("onMapReady()");

        mGoogleMap = googleMap;

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMapAddress, 5));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }
            @Override
            public void onMarkerDrag(Marker marker) {
            }
            @Override
            public void onMarkerDragEnd(Marker marker) {
                showLog("markerDragEnd");
                mMarkerLatLng = marker.getPosition();
            }
        });


        final GoogleMap constantMap = googleMap;
        constantMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick()");

                MarkerOptions markerOptions = new MarkerOptions();
                // markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon01));
                markerOptions.position(latLng);
                markerOptions.draggable(true);
                markerOptions.title("여기근처");

                constantMap.clear();
                constantMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                constantMap.addMarker(markerOptions);
                constantMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                mMarkerLatLng = latLng; // 위치 저장
            }
        });


        // GPS 켜기(현재 위치로 바로 이동 가능)
        GooglePlayServicesUtil.isGooglePlayServicesAvailable(RegisterActivity.this);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        mProvider = mLocationManager.getBestProvider(criteria, true);

        if ("passive".equals(mProvider)) { // 위치정보 설정하는 액티비티로 이동
            new AlertDialog.Builder(RegisterActivity.this).setMessage("위치서비스 동의")
                    .setNeutralButton("이동", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), TURN_ON_GPS);
                        }
                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
            }).show();

        } else {   //위치 정보 설정이 되어 있으면 현재위치를 받아옵니다, gps
            googleMap.setMyLocationEnabled(true);
        }

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_member_register:
                String email = mEtEmail.getText().toString();
                String password = mEtPassword.getText().toString();
                String univ = mEtUniv.getText().toString();
                String textLocation = mEtLocationText.getText().toString();

                if (mMarkerLatLng == null) {
                    showToast("정보를 빠짐없이 입력해 주세요 :(");
                } else {
                    UserInfo userInfo = new UserInfo(email, password, univ, textLocation, mMarkerLatLng.latitude, mMarkerLatLng.longitude);

                    if (userInfo.isAllSet() == false) {
                        showToast("정보를 빠짐없이 입력해 주세요 :(");
                    } else {
                        mInsertMemberPhp = new InsertMemberPhp();
                        mInsertMemberPhp.execute(userInfo);
                    }
                }
                break;
            case R.id.ib_map_search:
                findLocation();
                break;
        }
    }


    private void findLocation() {
        showLog("findLocation()");

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEtSearchLocation.getWindowToken(), 0);

        Geocoder geoCoder = new Geocoder(RegisterActivity.this);

        try {
            // 숫자 : 최대로 가져오는 결과 개수
            List<Address> addresses = geoCoder.getFromLocationName(mEtSearchLocation.getText().toString(), 1);
            if (addresses.size() > 0) {
                showLog("address size > 0");

                for (Address address : addresses) {
                    showLog(address.getAddressLine(0));

                    double latitude = address.getLatitude();
                    double longitude = address.getLongitude();

                    LatLng latLng= new LatLng(latitude, longitude);
                    mMarkerLatLng = latLng; // 현재위치 저장

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.draggable(true);
                    markerOptions.position(latLng);
                    markerOptions.title("여기근처");

                    mGoogleMap.clear();
                    mGoogleMap.addMarker(markerOptions);

                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }

            } else {
                AlertDialog.Builder adb = new AlertDialog.Builder(RegisterActivity.this);
                adb.setMessage("검색 결과가 없습니다 :(");
                adb.setPositiveButton("닫기", null);
                adb.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    private class InsertMemberPhp extends AsyncTask<UserInfo, Integer, Boolean> {

        private final String TAG = InsertMemberPhp.class.getSimpleName();

        private void log(String message) {
            Log.d(TAG, message);
        }

        private String mCreateUserUrl = "http://plateshare.kr/php/create_user.php";

        private JSONParser mJsonParser = new JSONParser();
        private String mResultMessage;

        private UserInfo mUserInfo;

        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(RegisterActivity.this);
            mDialog.setMessage("잠시만 기다려 주세요 :D");
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected Boolean doInBackground(UserInfo... params) {

            mUserInfo = params[0];

            return memberRegisterJsonPost();
        }

        private boolean memberRegisterJsonPost() {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("email", mUserInfo.getmEmail()));
            params.add(new BasicNameValuePair("password", mUserInfo.getmPassword()));
            params.add(new BasicNameValuePair("univ", mUserInfo.getmUniv()));
            params.add(new BasicNameValuePair("text_location", mUserInfo.getmTextLocation()));
            params.add(new BasicNameValuePair("latitude", String.valueOf(mUserInfo.getmLatitude())));
            params.add(new BasicNameValuePair("longitude", String.valueOf(mUserInfo.getmLongitude())));

            JSONObject json = mJsonParser.makeHttpRequestGetJsonObj(mCreateUserUrl, "POST", params);

            try {
                int success = json.getInt(PSContants.TAG_SUCCESS);

                if (success == 1) { // email 중복 되면 가입 안되네?... 뭐지
                    log("DB insert 성공");
                    return true;
                } else {
                    mResultMessage = json.getString(PSContants.TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean result) {

            if (mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
            }

            if (result == true) { // DB 입력 성공
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setMessage("가입성공");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            } else { // DB 입력 실패
                showToast(mResultMessage);
            }
        }
    }

}
