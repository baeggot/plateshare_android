package com.baeflower.sol.plateshare.phpUtil;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
public class SelectDetailContentPhp extends AsyncTask<String, Integer, Boolean> {

    private final String TAG = SelectDetailContentPhp.class.getSimpleName() + "_컨텐츠_상세";

    private void showLog(String message) {
        Log.d(TAG, message);
    }

    private final String mGetDetailContentByUnivUrl = "http://plateshare.kr/php/get_count_contents_by_univ.php";

    private Context mContext;
    private String mId;
    private String mResultMessage;
    private JSONArray mJsonArr;
    private JSONParser mJsonParser = new JSONParser();


    public SelectDetailContentPhp() {
    }

    public SelectDetailContentPhp(Context context) {
        mContext = context;
    }


    @Override
    protected Boolean doInBackground(String... params) {
        mId = params[0];
        return null;
    }


    private boolean getCountContentByUniv() {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("id", mId));

        JSONObject jsonObj = mJsonParser.makeHttpRequestGetJsonObj(mGetDetailContentByUnivUrl, "GET", params);

        try {
            int success = jsonObj.getInt(PSContants.TAG_SUCCESS);

            if (success == 1) {
                showLog("컨텐츠 있음");
                mJsonArr = jsonObj.getJSONArray("content");
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

            try {
                JSONObject jsonObj = mJsonArr.getJSONObject(0); // detail 은 객체 1개 들어있음(id 로 가져오니깐)
                PSContent psContent = new PSContent();
                psContent.setmId(jsonObj.getInt("id"));
                psContent.setmTitle(jsonObj.getString("title"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {

        }

    }


}
