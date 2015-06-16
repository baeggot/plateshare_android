package com.baeflower.sol.plateshare.util;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sol on 2015-06-04.
 */
public class XmlParserUtil {

    public static String getXmlData(String xmlUrl, String filename, String str) { //태그값 하나를 받아오기위한 String 형 함수
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


    //태그값 여러개를 받아오기위한 ArrayList<string>형 변수
    private ArrayList<String> getXmlDataList(String xmlUrl, String filename, String str) {
        // String rss = insertMemberUrl + "/_xml";
        ArrayList<String> ret = new ArrayList<>();

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
                        ret.add(xpp.nextText());
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
