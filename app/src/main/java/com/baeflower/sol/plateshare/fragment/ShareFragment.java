package com.baeflower.sol.plateshare.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baeflower.sol.plateshare.R;
import com.baeflower.sol.plateshare.adapter.ShareContentsAdapter;
import com.baeflower.sol.plateshare.content.ShareCreateActivity;
import com.baeflower.sol.plateshare.model.ShareInfo;
import com.baeflower.sol.plateshare.util.JSONParser;
import com.baeflower.sol.plateshare.util.PSContants;
import com.software.shell.fab.ActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sol on 2015-06-08.
 */
public class ShareFragment extends Fragment {


    private static final String TAG = ShareFragment.class.getSimpleName();

    private void showLog(String message) {
        Log.d(TAG, message);
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private static final int SPAN_COUNT = 2;

    private ShareContentsAdapter mShareAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ActionButton mActionButton;

    private OnDataChangeListener mListener;


    //
    private SelectShareByUnivPhp mSelectShareByUnivPhp;
    private List<ShareInfo> mShareInfoList;
    private String mUniv;


    public enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER,
        STAGGERED_LAYOUT_MANAGER
    }


    public static ShareFragment newInstance(String univ) {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();
        args.putString("univ", univ);
        fragment.setArguments(args);
        return fragment;
    }


    public interface OnDataChangeListener {
        void onDataChanged();
    }


    public void setOnDataChangeListener(OnDataChangeListener listener) {
        mListener = listener;
    }


    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLog("onCreate()");

        mUniv = getArguments().getString("univ");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        showLog("share_onCreateView()");

        View rootView = inflater.inflate(R.layout.fragment_share, container, false);

        // -------------------- Floating Button
        mActionButton = (ActionButton) rootView.findViewById(R.id.action_button_plus_share);
        mActionButton.setButtonColor(getResources().getColor(R.color.blue));
        mActionButton.setButtonColorPressed(getResources().getColor(R.color.blue_pressed));
        mActionButton.setImageResource(R.drawable.fab_plus_icon);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("floating button click");
                Intent intent = new Intent(getActivity(), ShareCreateActivity.class);
                startActivity(intent);
            }
        });


        // -------------------- Recycler View
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_share);
        mRecyclerView.setHasFixedSize(false); // 레이아웃 사이즈가 변하지 않을거면 true

        // Linear 방향으로 Recycler View 사용
        mLayoutManager = new LinearLayoutManager(getActivity());

        // setLayout
        // savedInstanceState (이것도 완벽하진 않지만...)
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRecyclerViewLayoutManager(LayoutManagerType.STAGGERED_LAYOUT_MANAGER);
        }
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mSelectShareByUnivPhp = new SelectShareByUnivPhp();
        mSelectShareByUnivPhp.execute(10); // 10개 가져오기

        // adapter 명시 (현재 데이터는 null 넘김)
        // mShareAdapter = new ShareContentsAdapter(new String[]{ "동해물과", "백두산이", "마르고", "닳도록", "학교종이", "떙땡땡", "어서", "모이자" }, SHARE);


        return rootView;
    }

    private void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        showLog("setRecyclerViewLayoutManager");

        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll
        // position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                break;
            case STAGGERED_LAYOUT_MANAGER:
                mLayoutManager = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
                break;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    private boolean isFirst = true;

    private class SelectShareByUnivPhp extends AsyncTask<Integer, String, List<ShareInfo>> {

        private final String mSelectShareUrl = "http://plateshare.kr/php/get_contents_by_univ.php";

        private ProgressDialog mDialog;
        private Integer mDataNumber;
        private JSONParser mJsonParser = new JSONParser();
        private String mResultMessage;


        // UI thread
        @Override
        protected void onPreExecute() {
            showLog("onPreExecute()");

            if (isFirst == true) {
                mDialog = new ProgressDialog(getActivity());
                mDialog.setCancelable(false);
                mDialog.setMessage("잠시만 기다려 주세요 :D");
                mDialog.show();
            }
        }


        // background thread
        @Override
        protected List<ShareInfo> doInBackground(Integer... params) {
            showLog("doInBackground()");

            mDataNumber = params[0];

            return getShare();
        }

        private List<ShareInfo> getShare() {

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("univ", mUniv));
            params.add(new BasicNameValuePair("number", String.valueOf(mDataNumber)));

            if (isFirst == false) {
                // size() - 1 자리는 progress item
                showLog("mSIL size : " + String.valueOf(mShareInfoList.size()));

                long minId = mShareInfoList.get(mShareInfoList.size() - 2).getmId();
                showLog("minId : " + String.valueOf(minId)); // query 에서 id < min_id

                params.add(new BasicNameValuePair("min_id", String.valueOf(minId)));
            }

            JSONObject jsonObj = mJsonParser.makeHttpRequestGetJsonObj(mSelectShareUrl, "POST", params);

            if (jsonObj == null) {
                return null;
            }

            try {
                int success = jsonObj.getInt(PSContants.TAG_SUCCESS);

                if (success == 1) {
                    JSONArray jsonArr = jsonObj.getJSONArray("contents");
                    showLog(jsonArr.toString());

                    return shareJsonParser(jsonArr);
                } else {
                    mResultMessage = jsonObj.getString(PSContants.TAG_MESSAGE);
                    showLog(mResultMessage);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        private List<ShareInfo> shareJsonParser(JSONArray jsonArray) {
            showLog("shareJsonParser()");

            List<ShareInfo> shareDataList = new ArrayList<>();

            int size = jsonArray.length();
            JSONObject jsonObj;
            ShareInfo shareInfo;
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (int i = 0; i < size; i++) {
                try {
                    shareInfo = new ShareInfo();

                    jsonObj = jsonArray.getJSONObject(i);
                    shareInfo.setmId(jsonObj.getLong("id"));
                    shareInfo.setmTitle(jsonObj.getString("title"));
                    shareInfo.setmExplanation(jsonObj.getString("explanation"));
                    shareInfo.setmImageName(jsonObj.getString("image"));
                    shareInfo.setmDday(sf.parse(jsonObj.getString("d_day")));
                    shareInfo.setmLocation(jsonObj.getString("location"));

                    shareDataList.add(shareInfo);

                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            return shareDataList;
        }


        // onProgressUpdate : UI thread

        @Override
        protected void onPostExecute(List<ShareInfo> shareInfoList) {
            showLog("onPostExecute()");

            if (shareInfoList == null) {
                if (isFirst == true) {
                    mDialog.dismiss();
                    showToast("데이터가 없습니다 :(");
                } else {

                    mShareAdapter.setLoaded();
                    mShareInfoList.remove(mShareInfoList.size() - 1);
                    mShareAdapter.notifyItemRemoved(mShareInfoList.size() - 1);

                    showToast("데이터가 없습니다 :(");
                }
            } else {
                if (isFirst == true) {
                    isFirst = false;

                    mShareInfoList = shareInfoList;
                    mShareAdapter = new ShareContentsAdapter(mShareInfoList, mRecyclerView);
                    mShareAdapter.setOnLoadMoreListener(new ShareContentsAdapter.OnLoadMoreListener() {
                        @Override
                        public void onLoadMore() {
                            showLog("onLoadMore()");

                            // add progress item
                            mShareInfoList.add(null);
                            mShareAdapter.notifyItemInserted(mShareInfoList.size() - 1);

                            mSelectShareByUnivPhp = new SelectShareByUnivPhp();
                            mSelectShareByUnivPhp.execute(10);
                        }
                    });

                    mRecyclerView.setAdapter(mShareAdapter);
                    mDialog.dismiss();

                } else {
                    // remove progress item
                    mShareInfoList.remove(mShareInfoList.size() - 1);
                    mShareAdapter.notifyItemRemoved(mShareInfoList.size() - 1);

                    int size = shareInfoList.size();
                    for (int i = 0; i < size; i++) {
                        mShareInfoList.add(shareInfoList.get(i));
                        mShareAdapter.notifyItemInserted(mShareInfoList.size() - 1);
                    }
                    mShareAdapter.setLoaded();
                }
            }

        }

    }

}
