package com.baeflower.sol.plateshare.member;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baeflower.sol.plateshare.R;
import com.baeflower.sol.plateshare.util.JSONParser;
import com.baeflower.sol.plateshare.util.PSContants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends ActionBarActivity implements View.OnClickListener {

    private static final String TAG = RegisterActivity.class.getSimpleName() + "_회원가입 액티비티";

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void log(String message) {
        Log.d(TAG, message);
    }


    private EditText mEtEmail;
    private EditText mEtPassword;
    private EditText mEtPasswordConfirm;
    private EditText mEtUniv;
    private EditText mEtLocationText;

    private Button mBtnMemberRegister;

    private InsertMemberPhp mInsertMemberPhp;

    private void init() {
        mEtEmail = (EditText) findViewById(R.id.et_member_email);
        mEtPassword = (EditText) findViewById(R.id.et_member_password);
        mEtPasswordConfirm = (EditText) findViewById(R.id.et_member_password_confirm);
        mEtUniv = (EditText) findViewById(R.id.et_member_univ);
        mEtLocationText = (EditText) findViewById(R.id.et_member_text_location);

        mBtnMemberRegister = (Button) findViewById(R.id.btn_member_register);
        mBtnMemberRegister.setOnClickListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_member_register:
                String email = mEtEmail.getText().toString();
                String password = mEtPassword.getText().toString();

                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                    showToast("정보를 제대로 입력해 주세요 :D");
                } else {
                    mInsertMemberPhp = new InsertMemberPhp();
                    mInsertMemberPhp.execute(email, password);
                }

                /*
                    String email = mEtEmail.getText().toString();
                    String password = mEtPassword.getText().toString();
                    String passwordConfirm = mEtPasswordConfirm.getText().toString();
                    String univ = mEtUniv.getText().toString();
                    String locationText = mEtLocationText.getText().toString();
                 */
                break;
        }
    }

    private class InsertMemberPhp extends AsyncTask<String, Integer, Boolean> {

        private final String TAG = InsertMemberPhp.class.getSimpleName() + "_회원가입 php 접근 class";

        private void log(String message) {
            Log.d(TAG, message);
        }


        private String mCreateUserUrl = "http://plateshare.kr/php/create_user.php";

        private JSONParser mJsonParser = new JSONParser();

        private String mEmail;
        private String mPassword;

        private String mResultMessage;

        @Override
        protected Boolean doInBackground(String... params) {
            mEmail = params[0];
            mPassword = params[1];

            return memberRegisterJsonPost();
        }

        private boolean memberRegisterJsonPost() {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("email", mEmail));
            params.add(new BasicNameValuePair("password", mPassword));

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
