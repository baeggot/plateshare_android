package com.baeflower.sol.plateshare.phpUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.baeflower.sol.plateshare.adapter.FragmentContentsAdapter;
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
public class SelectCountContentsByUnivPhp extends AsyncTask<String, Integer, Boolean> {

    private final String TAG = SelectCountContentsByUnivPhp.class.getSimpleName() + "_컨텐츠_카운트";
    private int mCnt;

    private void log(String message) {
        Log.d(TAG, message);
    }


    private final String mGetCountContentsByUnivUrl = "http://plateshare.kr/php/get_count_contents_by_univ.php";

    private ProgressDialog mDialog;
    private Context mContext;
    private String mUniv;

    private String mResultMessage;
    private JSONArray mJsonArr;
    private JSONParser mJsonParser = new JSONParser();

    private ListView mListView;
    private FragmentContentsAdapter mAdapter;

    public SelectCountContentsByUnivPhp(Context context) {
        mContext = context;
    }

    public SelectCountContentsByUnivPhp(Context context, ListView listView) {
        mContext = context;
        mListView = listView;
    }


    @Override
    protected void onPreExecute() {

        mDialog = new ProgressDialog(mContext);
        // mDialog.setMessage("Getting Data ...");
        mDialog.show();

    }


    @Override
    protected Boolean doInBackground(String... params) {
        mUniv = params[0];
        return getCountContentByUniv();
    }


    private boolean getCountContentByUniv() {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("univ", mUniv));

        JSONObject jsonObj = mJsonParser.makeHttpRequestGetJsonObj(mGetCountContentsByUnivUrl, "GET", params);

        try {
            int success = jsonObj.getInt(PSContants.TAG_SUCCESS);

            if (success == 1) {
                log("컨텐츠 있음");
                mCnt = jsonObj.getInt(PSContants.PSCONTENT_COUNT);
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

    private ArrayList<PSContent> mDataList;

    @Override
    protected void onPostExecute(Boolean result) {

        if (result == true) {
            mDataList = new ArrayList<>(mCnt); // mCnt 갯수만큼 크기를 지정해서 초기화
            mAdapter = new FragmentContentsAdapter(mContext, mDataList, mJsonArr); // BaseAdapter
            mListView.setAdapter(mAdapter);

        } else {
            log(mResultMessage);
        }
        mDialog.dismiss();

    }
}
