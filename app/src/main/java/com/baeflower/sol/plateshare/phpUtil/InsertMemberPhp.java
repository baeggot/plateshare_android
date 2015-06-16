package com.baeflower.sol.plateshare.phpUtil;

import android.os.AsyncTask;
import android.util.Log;

import com.baeflower.sol.plateshare.util.XmlParserUtil;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sol on 2015-06-03.
 */
public class InsertMemberPhp extends AsyncTask<String, Integer, Boolean> {

    private static final String TAG = InsertMemberPhp.class.getSimpleName() + "_회원가입 php 접근 class";

    private void log(String message) {
        Log.d(TAG, message);
    }


    private String insertMemberUrl = "http://plateshare.kr/php/insertMember.php";
    private String insertMemberUrlPost = "http://plateshare.kr/php/insertMemberPost.php";

    private String xmlUrl = "http://plateshare.kr/_xml";


    private String mEmail;
    private String mPassword;

    public InsertMemberPhp(String email, String password) {
        mEmail = email;
        mPassword = password;
    }


    @Override
    protected Boolean doInBackground(String... params) {

        boolean result = memberRegister();

        return result;
    }

    private boolean memberRegisterPost() {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(insertMemberUrlPost);
            List<NameValuePair> pairs = new ArrayList<>();

            pairs.add(new BasicNameValuePair("email", mEmail));
            pairs.add(new BasicNameValuePair("password", mPassword));

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, HTTP.UTF_8);
            post.setEntity(entity);
            HttpResponse response = client.execute(post);

            String result = XmlParserUtil.getXmlData(xmlUrl, "insertMemberResultPost.xml", "result"); //입력 성공여부

            if ("1".equals(result)) { //result 태그값이 1일때 성공
                log("DB insert 성공");
                return true;
            } else {//result 태그값이 1이 아닐때 실패
                log("DB insert 실패");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    private boolean memberRegister() {
        log("memberRegister");

        try {
            //변수값을 UTF-8로 인코딩하기 위해 URLEncoder를 이용하여 인코딩함
            URL url = new URL(insertMemberUrl + "?" + "name=" + URLEncoder.encode(mEmail, "UTF-8") + "&price=" + URLEncoder.encode(mPassword, "UTF-8"));

            url.openStream(); //서버의 DB에 입력하기 위해 웹서버의 insert.php 파일에 입력된 이름과 가격을 넘김

            String result = getXmlData("insertMemberResult.xml", "result"); //입력 성공여부

            if ("1".equals(result)) { //result 태그값이 1일때 성공
                log("DB insert 성공");
                return true;
            }
            else {//result 태그값이 1이 아닐때 실패
                log("DB insert 실패");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        

    }

    private String getXmlData(String filename, String str) { //태그값 하나를 받아오기위한 String 형 함수
        // String rss = xmlUrl + "/_xml";
        String ret = "";

        try { //XML 파싱을 위한 과정
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            URL server = new URL(xmlUrl + "/" + filename);
            InputStream is = server.openStream();
            xpp.setInput(is, "UTF-8");

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals(str)) { //태그 이름이 str 인자값과 같은 경우
                        ret = xpp.nextText();
                    }
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return ret;
    }



}
