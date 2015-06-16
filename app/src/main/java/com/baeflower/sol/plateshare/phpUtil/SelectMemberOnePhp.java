package com.baeflower.sol.plateshare.phpUtil;

import com.baeflower.sol.plateshare.util.XmlParserUtil;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sol on 2015-06-05.
 */
public class SelectMemberOnePhp {

    private String selectMemberUrl = "http://plateshare.kr/php/seletMember.php";
    private String selectMemberUrlPost = "http://plateshare.kr/php/seletMemberPost.php";

    private String xmlUrl = "http://plateshare.kr/_xml";

    private boolean selectMemberOnePost(String email, String password) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(selectMemberUrlPost);
            List<NameValuePair> pairs = new ArrayList<>();

            pairs.add(new BasicNameValuePair("email", email));
            pairs.add(new BasicNameValuePair("password", password));

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, HTTP.UTF_8);
            post.setEntity(entity);
            HttpResponse response = client.execute(post);

            String result = XmlParserUtil.getXmlData(xmlUrl, "findMemberOneResultPost.xml", "result"); //입력 성공여부

            if ("1".equals(result)) { //result 태그값이 1일때 회원정보 있음
                return true;
            } else {//result 태그값이 1이 아닐때 실패
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean selectMemberOne(String email, String password) {

        try {
            //변수값을 UTF-8로 인코딩하기 위해 URLEncoder를 이용하여 인코딩함
            URL url = new URL(selectMemberUrl + "?" + "email=" + URLEncoder.encode(email, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8"));
            url.openStream();

            String result = XmlParserUtil.getXmlData(xmlUrl, "findMemberOneResult.xml", "result"); //입력 성공여부

            if ("1".equals(result)) { //result 태그값이 1일때 회원정보 있음
                return true;
            } else {//result 태그값이 1이 아닐때 실패
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
