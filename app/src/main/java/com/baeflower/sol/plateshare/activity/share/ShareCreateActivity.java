package com.baeflower.sol.plateshare.activity.share;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baeflower.sol.plateshare.R;
import com.baeflower.sol.plateshare.fragment.WorkaroundMapFragment;
import com.baeflower.sol.plateshare.util.JSONParser;
import com.baeflower.sol.plateshare.util.PSContants;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ShareCreateActivity extends ActionBarActivity implements View.OnClickListener, TextView.OnEditorActionListener {


    private static final String TAG = ShareCreateActivity.class.getSimpleName();
    private static final int TURN_ON_GPS = 0;


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showLog(String message) {
        Log.d(TAG, message);
    }


    private static final int REQUEST_IMAGE_CAPTURE = 1;


    //
    private ScrollView mScrollView;
    private EditText mEtShareTitle;
    private EditText mEtShareExplanation;
    private TextView mTvShareExplaCount;
    private ImageView mIvSharePhoto;
    private Button mBtnShareGetPhoto;
    private Button mBtnShareCancelPhoto;
    private Button mBtnDday;
    private EditText mEtShareTextLocation;
    private Button mBtnShareCreate;



    // 서버 업로드
    private InsertSharePhp mInsertSharePhp;

    private String mUploadFileName;
    private Uri mPicUri;
    private Bitmap mTakenPhoto;
    private String mFolderPathStr;
    private String mImageRealPathFromGall;


    // 데이터 입력 확인
    private boolean mDateSet = false;
    private boolean mImageSet = false;


    // 사용자 정보
    private int mId;
    private String mUniv;

    // Map
    private GoogleMap mGoogleMap;
    private LatLng mMarkerLatLng;
    private LocationManager mLocationManager;
    private String mProvider;


    private void init() {

        mEtShareTitle = (EditText) findViewById(R.id.et_share_create_title);

        mEtShareExplanation = (EditText) findViewById(R.id.et_share_create_explanation);
//        mEtShareExplanation.setImeOptions(EditorInfo.IME_ACTION_DONE); // 완료 : 엔터가 여기로 들어오네..
        mEtShareExplanation.setOnEditorActionListener(this);
        mEtShareExplanation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int inputLength = s.toString().getBytes().length;

                if (inputLength > 200) {
                    s.delete(s.length() - 2, s.length() - 1);
                    Toast.makeText(getApplicationContext(), "over 200 byte", Toast.LENGTH_SHORT).show();
                } else {
                    changeBytesCountView(inputLength);
                }
            }
        });
        mTvShareExplaCount = (TextView) findViewById(R.id.tv_share_create_expla_count);


        mIvSharePhoto = (ImageView) findViewById(R.id.iv_share_photo);
        mBtnShareGetPhoto = (Button) findViewById(R.id.btn_share_get_photo); // 사진 등록 버튼
        mBtnShareGetPhoto.setOnClickListener(this);
        mBtnShareCancelPhoto = (Button) findViewById(R.id.btn_share_cancel_photo); // 등록한 사진 취소 버튼
        mBtnShareCancelPhoto.setOnClickListener(this);

        mBtnDday = (Button) findViewById(R.id.btn_dday_date); // 날짜 입력 버튼
        mBtnDday.setOnClickListener(this);

        mEtShareTextLocation = (EditText) findViewById(R.id.et_share_create_text_location);  // 가까운 위치

        mBtnShareCreate = (Button) findViewById(R.id.btn_share_create); // 글올리기 버튼
        mBtnShareCreate.setOnClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_create);

        init();

        // --------------------- 사용자 데이터
        mEtShareTextLocation.setText(PSContants.USERINFO.getmTextLocation());


        // --------------------- Map Setting
        mGoogleMap = ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_google_map_share_create)).getMap();
        mScrollView = (ScrollView) findViewById(R.id.sv_share_create_container);

        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_google_map_share_create))
                .setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        mScrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });

        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                mMarkerLatLng = new LatLng(PSContants.USERINFO.getmLatitude(), PSContants.USERINFO.getmLongitude());

                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
                mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);


                // -------------------------- 해당 위치로 Map 이동
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMarkerLatLng, 15));


                // -------------------------- Map Marker 추가
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(mMarkerLatLng);
                markerOptions.draggable(true);
                markerOptions.title("여기근처");
                mGoogleMap.addMarker(markerOptions);


                // -------------------------- Map Marker Drag
                mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                    }
                    @Override
                    public void onMarkerDrag(Marker marker) {
                    }
                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        mMarkerLatLng = marker.getPosition();
                    }
                });


                // -------------------------- Map Click
                final GoogleMap constantMap = mGoogleMap;
                constantMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                        MarkerOptions markerOptions = new MarkerOptions();
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

                // --------------------------  GPS 켜기(현재 위치로 바로 이동 가능)
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(ShareCreateActivity.this);
                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                mProvider = mLocationManager.getBestProvider(criteria, true);

                if ("passive".equals(mProvider)) { // 위치정보 설정하는 액티비티로 이동
                    new AlertDialog.Builder(ShareCreateActivity.this).setMessage("위치서비스 동의")
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
                    mGoogleMap.setMyLocationEnabled(true);
                }
            }
        });

    }

    private boolean fromGallery;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    if (data != null) {
                        Uri outputFileUri = data.getData();

                        // 카메라로 찍은 사진은
                        // file:///storage/emulated/0/Android/data/com.baeflower.sol.plateshare/files/Pictures/IMG_20150619_160541.jpg // B612
                        // 갤러리에서 오는 사진은
                        // content://media/external/images/media/2292 이런 형식으로 들어옴

                        if (outputFileUri.toString().contains("content")) {
                            imageSetFromGallery(outputFileUri);
                        } else if (outputFileUri.toString().contains("file")) {
                            imageSetFromCamera();
                        }

                    } else {
                        imageSetFromCamera();
                    }

                    mIvSharePhoto.setImageBitmap(mTakenPhoto);
                    mIvSharePhoto.setVisibility(View.VISIBLE);
                    mImageSet = true;

                    break;
                case TURN_ON_GPS:
                    mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    mProvider = mLocationManager.getBestProvider(criteria, true);

                    if (mProvider == null){ //사용자가 위치설정동의 안했을때 종료
                        finish();
                    } else {//사용자가 위치설정 동의 했을때
                        // mLocationManager.requestLocationUpdates(mProvider, 1L, 2F, RegisterActivity.this);
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                    break;
            }
        }

    }

    private void imageSetFromGallery(Uri photoUri) {
        try {
            mImageRealPathFromGall = getFilePathFromContentUri(photoUri);
            mTakenPhoto = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);

        } catch (IOException e) {
            e.printStackTrace();
        }
        fromGallery = true;
    }

    private void imageSetFromCamera() {
        try {
            mTakenPhoto = MediaStore.Images.Media.getBitmap(getContentResolver(), mPicUri);
            String root = Environment.getExternalStorageDirectory().getAbsolutePath(); // /storage/emulated/0
            mFolderPathStr = root + "/plate_share";

            fromGallery = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share_get_photo: // 사진 가져오기
                dispatchTakePictureIntent();
                break;
            case R.id.btn_share_cancel_photo: // 사진 취소
                cancelPhoto();
                break;
            case R.id.btn_dday_date: // 날짜 세팅
                showDatePickerDialog();
                break;
            case R.id.btn_share_create: // 글올리기
                createShareContent();
                break;
        }
    }


    private void dispatchTakePictureIntent() {
        // 카메라 호출
        Intent cameraIntent = new Intent();
        cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);// 사진 폴더
        mUploadFileName = createFileName();
        mPicUri = Uri.fromFile(new File(path, mUploadFileName));
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPicUri);

        // 갤러리
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);

        Intent chooserIntent = Intent.createChooser(galleryIntent, "갤러리 또는 카메라 선택");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});

        // 주고
        startActivityForResult(chooserIntent, REQUEST_IMAGE_CAPTURE);
    }

    final static String JPEG_FILE_PREFIX = "IMG_";
    final static String JPEG_FILE_SUFFIX = ".jpg";

    // 파일 이름 세팅
    private String createFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "";
        imageFileName += JPEG_FILE_SUFFIX;
        return imageFileName;
    }


    private void cancelPhoto() {
        mIvSharePhoto.setImageBitmap(null);
        mIvSharePhoto.setVisibility(View.GONE);
        mImageSet = false;
    }


    // 글 등록
    private void createShareContent() {
        String title = mEtShareTitle.getText().toString();
        String expla = mEtShareExplanation.getText().toString();
        String dday = mBtnDday.getText().toString();
        String textLocation = mEtShareTextLocation.getText().toString();


        if (TextUtils.isEmpty(title) == false && TextUtils.isEmpty(expla) == false && TextUtils.isEmpty(textLocation) == false
                && mImageSet && mDateSet) {

            if (mMarkerLatLng != null) {
                mInsertSharePhp = new InsertSharePhp(ShareCreateActivity.this);
                mInsertSharePhp.execute(title, expla, dday, textLocation, String.valueOf(mMarkerLatLng.latitude), String.valueOf(mMarkerLatLng.longitude));
            }

        } else {
            showToast("빠짐없이 입력해 주세요 :D");
        }
    }


    // DB 및 서버 저장
    private class InsertSharePhp extends AsyncTask<String, Integer, Boolean> {

        private final String TAG = InsertSharePhp.class.getSimpleName();

        private WeakReference<Activity> callerActivity;
        private WeakReference<ProgressDialog> dialog;


        public InsertSharePhp(Activity activity) {
            callerActivity = new WeakReference<>(activity);
        }

        private final String mSharePhotoSaveUrl = "http://plateshare.kr/php/share/insert_share_photo.php";
        private final String mShareSaveUrl = "http://plateshare.kr/php/insert_share.php";

        private String mTitle;
        private String mExplanation;
        private String mSavedImageName;
        private String mTextLocation;
        private String mLatitude;
        private String mLongitude;

        private Date mDday;
        private String mDdayStr;
        private JSONParser mJsonParser;


        @Override
        protected void onPreExecute() {

            if( callerActivity.get() != null ) {
                dialog = new WeakReference<>( new ProgressDialog( callerActivity.get() ) );
                dialog.get().setMessage("저장하는중.. :D");
                dialog.get().setCancelable(false);
                if( callerActivity.get() != null && !callerActivity.get().isFinishing()) {
                    dialog.get().show();
                }
            }
        }


        @Override
        protected Boolean doInBackground(String... params) {
            mTitle = params[0]; // 제목
            mExplanation = params[1]; // 설명

            try {
                SimpleDateFormat sf = new SimpleDateFormat("yyyy/mm/dd");
                mDdayStr = params[2]; // 디데이
                mDday = sf.parse(mDdayStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mTextLocation = params[3]; // 가까운 위치
            mLatitude = params[4]; // 위도
            mLongitude = params[5]; // 경도

            return createShareContent();
        }

        private boolean createShareContent() {

            boolean imageSaved = uploadImageFile();

            if (imageSaved) {
                return insertShare();
            } else {
                return false;
            }
        }


        private boolean insertShare() {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("user_id", String.valueOf(PSContants.USERINFO.getmId()))); // user_id
            params.add(new BasicNameValuePair("title", mTitle)); // 글 제목
            params.add(new BasicNameValuePair("explanation", mExplanation)); // 설명
            params.add(new BasicNameValuePair("univ", PSContants.USERINFO.getmUniv())); // 학교
            params.add(new BasicNameValuePair("image", mSavedImageName)); // 서버에 저장된 이미지 이름
            params.add(new BasicNameValuePair("d_day", mDdayStr)); // 디데이
            params.add(new BasicNameValuePair("location", mTextLocation)); // 가까운 위치
            params.add(new BasicNameValuePair("latitude", mLatitude)); //
            params.add(new BasicNameValuePair("longitude", mLongitude)); //

            mJsonParser = new JSONParser();
            String resultMessage;

            JSONObject json = mJsonParser.makeHttpRequestGetJsonObj(mShareSaveUrl, "POST", params);
            showLog(json.toString());

            try {
                int success = json.getInt(PSContants.TAG_SUCCESS);

                if (success == 1) {
                    resultMessage = json.getString(PSContants.TAG_MESSAGE);
                    showLog(resultMessage);
                    return true;
                } else {
                    resultMessage = json.getString(PSContants.TAG_MESSAGE);
                    showLog(resultMessage);
                }

            } catch (JSONException e) {
                showLog("JSONException");
            }
            return false;
        }

        private boolean uploadImageFile() {
            int imageUploadResult;

            if (fromGallery) {
                imageUploadResult = uploadFile(mImageRealPathFromGall);
            } else { // camera
                imageUploadResult = uploadFile(mFolderPathStr + "/" + mUploadFileName);
            }

            if (imageUploadResult == 200) { // 성공
                return true;
            }
            return false;
        }


        private int uploadFile(final String sourceFileUri) {
            if (fromGallery == false) { // 카메라로 찍은거면 이미지 저장
                saveImage();
            }

            showLog(sourceFileUri); // 계정에 연결된 사진일 경우 null (구글계정프로필사진이라든가...)

            HttpURLConnection conn;
            DataOutputStream dos;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            File sourceFile = new File(sourceFileUri);

            if (!sourceFile.isFile()) {
                Log.e("uploadFile", "Source File not exist :" + sourceFileUri);
                return 0;
            } else {
                int serverResponseCode = 0;

                try {
                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(mSharePhotoSaveUrl);

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", sourceFileUri);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + sourceFileUri + "" + lineEnd);
                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    // send multipart form data necessary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage(); // OK
                    Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                    if (serverResponseCode == 200) {

                        // php 에서 받은 JSON 데이터 읽기 (저장한 이미지 이름을 가져옴)
                        try {
                            InputStream in = new BufferedInputStream(conn.getInputStream());
                            readStream(in);
                        } finally{
                            conn.disconnect();
                        }
                    }

                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                } catch (MalformedURLException ex) {
                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                } catch (Exception e) {
                    showLog("Upload file to server Exception" + e.getMessage());
                }

                return serverResponseCode;
            } // End else block
        }


        private void saveImage() { // 카메라로 찍은 사진일 경우 먼저 폰에 저장
            showLog("saveImage");

            File folder = new File(mFolderPathStr);
            if (folder.exists() == false) {
                folder.mkdir();
            }

            File targetFile = new File(folder, mUploadFileName);

            try {
                FileOutputStream fos = new FileOutputStream(targetFile, false);
                mTakenPhoto.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();

                Uri uri = Uri.fromFile(targetFile);

                // Media Scan
                // 사진 앱 들의 DB를 갱신하기 위해서(사진 앱을 실행했을 때 이 파일이 바로 검색 되도록?!)
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

            } catch (FileNotFoundException e) {
                Log.d(TAG, "FileNotFoundException");
            } catch (IOException e) {
                Log.d(TAG, "IOException");
            }
        }


        // JSON 데이터 읽기
        private void readStream(InputStream in) {
            String mJsonStr = "";

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                in.close();
                mJsonStr = sb.toString();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }

            // try parse the string to a JSON object
            try {
                JSONObject mJsonObj = new JSONObject(mJsonStr);

                int result = mJsonObj.getInt("success");
                if (result == 1) {
                    mSavedImageName = mJsonObj.getString("image");
                } else if (result == 0) {// 이미지 저장 실패
                    showLog("이미지 저장 실패");
                }

            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
        }


        @Override
        protected void onPostExecute(Boolean shareSaved) {

            if (dialog.get() != null) {
                dialog.get().dismiss();
                dialog = null;
            }

            if (shareSaved) {
                showToast("저장 되었습니다 :D");
                finish();
            } else {
                showToast("저장에 실패했습니다 :(");
            }
        }
    }






    // 내용에 글자 쓸 때마다 바이트 세기
    private void changeBytesCountView(int inputBytesLength) {
        mTvShareExplaCount.setText(inputBytesLength + " / 200 byte");
    }

    private Calendar mCalendar;
    private SimpleDateFormat mSimpleDateFormat;
    private int mYear;
    private int mMonth;
    private int mDay;

    // 데이트 다이얼로그 띄우기
    private void showDatePickerDialog() {
        mSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        mCalendar = new GregorianCalendar();

        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(ShareCreateActivity.this, dateSetListener, mYear, mMonth, mDay).show();
    }


    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, 1); // 다음날
            int tomorrowYear = calendar.get(Calendar.YEAR);
            int tomorrowMonth = calendar.get(Calendar.MONTH);
            int tomorrow = calendar.get(Calendar.DAY_OF_MONTH);

            String tomorrowStr = String.valueOf(tomorrowYear) + String.valueOf(tomorrowMonth) + tomorrow;
            String inputStr = String.valueOf(year) + String.valueOf(monthOfYear) + String.valueOf(dayOfMonth);

            if (Integer.parseInt(inputStr) < Integer.parseInt(tomorrowStr)) {// 최소 내일 이후로
                mDateSet = false;
                showToast("최소한 내일 이후로 입력해 주세요 :D");
                mBtnDday.setText("0000/00/00");
            } else {
                mDateSet = true;
                mCalendar.set(year, monthOfYear, dayOfMonth);
                mBtnDday.setText(mSimpleDateFormat.format(mCalendar.getTime()));
            }
        }
    };


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (v.getId()) {
            case R.id.et_share_create_explanation:
//                if (EditorInfo.IME_ACTION_DONE == actionId || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                    showLog("next");
//                }
                break;
        }
        return false;
    }

    private String getFilePathFromContentUri(Uri uri) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();

        return filePath;
    }

}
