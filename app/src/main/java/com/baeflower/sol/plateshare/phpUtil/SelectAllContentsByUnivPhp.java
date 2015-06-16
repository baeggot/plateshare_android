package com.baeflower.sol.plateshare.phpUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.baeflower.sol.plateshare.model.PSContent;
import com.baeflower.sol.plateshare.util.JSONParser;
import com.baeflower.sol.plateshare.util.PSContants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sol on 2015-06-09.
 */
public class SelectAllContentsByUnivPhp extends AsyncTask<String, Integer, Boolean> {

    private final String TAG = SelectAllContentsByUnivPhp.class.getSimpleName() + "_컨텐츠 php 접근";

    private void log(String message) {
        Log.d(TAG, message);
    }

    private Context mContext;

    private final String mGetContentsByUnivUrl = "http://plateshare.kr/php/get_contents_by_univ.php";

    private JSONArray mJsonArr;
    private String mUniv;
    private String mResultMessage;
    private JSONParser mJsonParser = new JSONParser();


    private ArrayList<PSContent> mContentDataList;

    public SelectAllContentsByUnivPhp(Context context, ListView listView) {
        mContentDataList = new ArrayList<>();
    }

    private ProgressDialog mDialog;

    @Override
    protected void onPreExecute() {

        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("Getting Data ...");
        mDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        mUniv = params[0];

        return getContentByUniv();
    }

    private boolean getContentByUniv() {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("univ", mUniv));

        JSONObject jsonObj = mJsonParser.makeHttpRequestGetJsonObj(mGetContentsByUnivUrl, "GET", params);

        try {
            int success = jsonObj.getInt(PSContants.TAG_SUCCESS);

            if (success == 1) {
                log("컨텐츠 있음");
                mJsonArr = jsonObj.getJSONArray("contents");
                return true;
            } else {
                mResultMessage = jsonObj.getString(PSContants.TAG_MESSAGE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {

        if (result == true) {

            PSContent tmpContent;
            int size = mJsonArr.length();
            for (int i = 0; i < size; i++) {

                try {
                    JSONObject jsonObj = mJsonArr.getJSONObject(i);
                    tmpContent = new PSContent();

                    // Log.i("log_tag", "id: " + jsonObj.getInt("id") + ", title: " + jsonObj.getString("title") + ", explanation: " + jsonObj.getString("explanation"));

                    tmpContent.setmId(jsonObj.getInt("id"));
                    tmpContent.setmTitle(jsonObj.getString("title"));

                    mContentDataList.add(tmpContent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // mPagerAdapter.setmPSContentList(mContentDataList);

        } else {
            log(mResultMessage);
        }
        mDialog.dismiss();
    }
}
