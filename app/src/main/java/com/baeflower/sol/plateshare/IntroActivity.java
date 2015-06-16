package com.baeflower.sol.plateshare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.baeflower.sol.plateshare.content.MainActivity;
import com.baeflower.sol.plateshare.member.RegisterActivity;
import com.baeflower.sol.plateshare.util.JSONParser;
import com.baeflower.sol.plateshare.util.PSContants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class IntroActivity extends ActionBarActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = IntroActivity.class.getSimpleName();
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private EditText mEtId;
    private EditText mEtPassword;
    private CheckBox mCbAutoLogin;
    private boolean mCbIsChecked;
    private Button mBtnMemberRegister;
    private Button mBtnMemberLogin;

    private SelectMemberPhp mSelectMemberPhp;

    private final String mSettingFileName = "plateshare_setting";
    private SharedPreferences mPrefUserSetting;
    private SharedPreferences.Editor mPrefEditor;


    private void init() {
        mEtId = (EditText) findViewById(R.id.et_intro_id);
        mEtPassword = (EditText) findViewById(R.id.et_intro_password);

        mCbAutoLogin = (CheckBox) findViewById(R.id.cb_auto_login);
        mCbAutoLogin.setOnCheckedChangeListener(this);

        mBtnMemberRegister = (Button) findViewById(R.id.btn_intro_register);
        mBtnMemberRegister.setOnClickListener(this);
        mBtnMemberLogin = (Button) findViewById(R.id.btn_intro_login);
        mBtnMemberLogin.setOnClickListener(this);

        /*
             plateshare_setting : 기록할 xml 이름, data/data/패키지이름/shared_prefs 에 저장
             뒤에 0 : 0 - 읽기, 쓰기 가능
             MODE_WORLD_READABLE : 읽기 공유
             MODE_WORLD_WRITEABLE : 쓰기 공유
             getSharedPreferences(int mode); // mode 를 설정하는 것은 같지만 xml 파일 이름이 없기 때문에, 현재 액티비티의 이름으로 설정됨
             PreferenceManager.getDefaultSharedPreferences(Context context) : 환경 설정 액티비티에서 설정한 값이 저장된
                                                                                SharedPreferences 의 데이터에 접근할 때 사용합니다.

            설정 값 가져오기 : mPrefUserSetting.getBoolean(key, defValue); // type 별로 가져다 쓰기
            값 넣기 : mPrefUserSetting.putString(key, value);
                      mPrefEditor.commit(); 또는 mPrefEditor.apply(); 를 해줘야 xml 에 저장됨
            기록된 값 지우기 : 전체제거 - editor.clear(); , 부분제거 - editor.remove(key);
        */

        mPrefUserSetting = getSharedPreferences(mSettingFileName, 0);
        mPrefEditor = mPrefUserSetting.edit(); // Preference Editor


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


        init();

        // preference 에 자동로그인 정보가 들어있는지 아닌지 확인
        if (mPrefUserSetting.getBoolean("auto_login_enabled", false)) {
            mEtId.setText(mPrefUserSetting.getString("email", ""));
            mEtPassword.setText(mPrefUserSetting.getString("password", ""));
            mCbAutoLogin.setChecked(true);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_intro_register: // 회원가입
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_intro_login: // 로그인
                String email = mEtId.getText().toString();
                String password = mEtPassword.getText().toString();

                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                    showToast("아이디 또는 비밀번호를 입력하세요");
                } else {
                    // DB 접근 - 아이디 비밀번호 일치하는지 확인
                    mSelectMemberPhp = new SelectMemberPhp();
                    mSelectMemberPhp.execute(email, password);
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mCbIsChecked = true;
        } else {
            mCbIsChecked = false;
        }
    }

    private class SelectMemberPhp extends AsyncTask<String, Integer, Boolean> {

        private final String TAG = SelectMemberPhp.class.getSimpleName() + "_회원찾기 php 접근 class";

        private void log(String message) {
            Log.d(TAG, message);
        }


        private String mSelectMemberJson = "http://plateshare.kr/php/get_user_login.php";

        private JSONParser mJsonParser = new JSONParser();
        private String mResultMessage;
        private String mEmail;
        private String mPassword;
        private JSONObject mJsonObj;

        public SelectMemberPhp() {
        }

        private ProgressDialog mDialog = new ProgressDialog(IntroActivity.this);

        @Override
        protected void onPreExecute() {
            mDialog.setMessage("로그인 정보 확인중 ...");
            mDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            mEmail = params[0];
            mPassword = params[1];

            return selectMemberOneJson();
        }

        private boolean selectMemberOneJson() {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("email", mEmail));
            params.add(new BasicNameValuePair("password", mPassword));

            JSONObject json = mJsonParser.makeHttpRequestGetJsonObj(mSelectMemberJson, "POST", params);

            try {
                int success = json.getInt(PSContants.TAG_SUCCESS);

                if (success == 1) {
                    log("사용자 정보 있음");
                    mJsonObj = json.getJSONArray("user").getJSONObject(0);
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

            if (result == true) { // 회원정보있음
                /*
                    AlertDialog.Builder builder = new AlertDialog.Builder(IntroActivity.this);
                    builder.setMessage("로그인성공");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                */

                // showToast("플레이트쉐어에 오신 것을 환영합니다 :D");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

                if (mCbIsChecked) {
                    savePrefDataSetting();
                } else {
                    deletePrefDataSetting();
                }

            } else { // 회원정보없음
                showToast(mResultMessage);
            }
            mDialog.dismiss();
        }

        private void savePrefDataSetting() {
            try {
                mPrefEditor.putString("email", mEmail);
                mPrefEditor.putString("password", mPassword);

                mPrefEditor.putString("univ", mJsonObj.getString("univ"));
                mPrefEditor.putBoolean("auto_login_enabled", true);
                mPrefEditor.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void deletePrefDataSetting() {
            mPrefEditor.clear();
            mPrefEditor.commit();
        }
    }


}
