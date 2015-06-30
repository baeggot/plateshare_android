package com.baeflower.sol.plateshare.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
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
import com.baeflower.sol.plateshare.activity.share.ShareCreateActivity;
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
import java.util.Date;
import java.util.List;

/**
 * Created by sol on 2015-06-08.
 */
public class ShareFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private static final String TAG = ShareFragment.class.getSimpleName();
    private static final int FIRST_DATA_LOADING = 0;
    private static final int LOAD_MORE_LOADING = 1;
    private static final int REFRESH_DATA_LOADING = 2;


    private void showLog(String message) {
        Log.d(TAG, message);
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private static final int SPAN_COUNT = 2;

    // 컴포넌트
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ShareContentsAdapter mShareAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private CardView mDvShare;


    private ActionButton mActionButton;

    // listener 예시
    private OnDataChangeListener mListener;

    public interface OnDataChangeListener {
        void onDataChanged();
    }

    public void setOnDataChangeListener(OnDataChangeListener listener) {
        mListener = listener;
    }


    // 서버 , 데이터
    private SelectShareByUnivPhp mSelectShareByUnivPhp;
    private List<ShareInfo> mShareInfoList;


    // 레이아웃
    public enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER,
        STAGGERED_LAYOUT_MANAGER
    }


    // fragment 생성
    // 계속 생성하는게 나을까, 싱글턴으로 생성하는게 나을까?
    public static ShareFragment newInstance() {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();
        // args.putString("univ", univ);
        fragment.setArguments(args);
        return fragment;
    }


    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLog("onCreate()");

        // mUniv = getArguments().getString("univ");

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        showLog("share_onCreateView()");

        View rootView = inflater.inflate(R.layout.fragment_share, container, false);


        // -------------------- Recycler View
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_share);
        mRecyclerView.setHasFixedSize(false); // 레이아웃 사이즈가 변하지 않을거면 true
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        //


        // -------------------- pull to refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        // Refresh 할 때 돌아가는 화살표의 색상 설정
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);



        // -------------------- Floating Button
        mActionButton = (ActionButton) rootView.findViewById(R.id.action_button_plus_share);
        mActionButton.setButtonColor(getResources().getColor(R.color.blue));
        mActionButton.setButtonColorPressed(getResources().getColor(R.color.blue_pressed));
        mActionButton.setImageResource(R.drawable.fab_plus_icon);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showLog("floating button click");
                Intent intent = new Intent(getActivity(), ShareCreateActivity.class);
                startActivity(intent);
            }
        });


        // -------------------- Layout
        if (savedInstanceState == null) { // 처음 로딩
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
            } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRecyclerViewLayoutManager(LayoutManagerType.GRID_LAYOUT_MANAGER);
            }
        } else { // 회전했을 경우 (첫 로딩이 아님)

        }
        mRecyclerView.setLayoutManager(mLayoutManager);


        // 데이터
        mSelectShareByUnivPhp = new SelectShareByUnivPhp();
        mSelectShareByUnivPhp.execute(FIRST_DATA_LOADING, PSContants.SHARE_LOAD_COUNT); // 10개 가져오기


        return rootView;
    }


    @Override
    public void onRefresh() {
        showLog("onRefresh()");

        // 현재 맨 위에 있는 데이터 보다 더 최근에 만들어진 데이터 가져와서 데이터리스트에 추가

        mSelectShareByUnivPhp = new SelectShareByUnivPhp();
        mSelectShareByUnivPhp.execute(REFRESH_DATA_LOADING, PSContants.SHARE_LOAD_COUNT);
        mSwipeRefreshLayout.setRefreshing(false); // To disable the gesture and progress animation

    }


    @Override
    public void onPause() {
        super.onPause();

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }

    }

    // fragment 가 사라질 때 현재의 상태를 저장하고 나중에 fragment 가 돌아오면 다시 저장한 내용을 사용할 수 있게 해줌
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Parcelable mListState = mLayoutManager.onSaveInstanceState();
        // outState.putParcelable("layoutManager", mListState);
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

        mRecyclerView.scrollToPosition(scrollPosition);
    }






    private boolean isFirst = true;

    private class SelectShareByUnivPhp extends AsyncTask<Integer, String, List<ShareInfo>> {

        private int DATA_LOADING_STATE;

        private final String mSelectShareUrl = "http://plateshare.kr/php/get_contents_by_univ.php";

        private ProgressDialog mDialog;
        private Integer mDataNumber;
        private JSONParser mJsonParser = new JSONParser();
        private SimpleDateFormat mSf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

            DATA_LOADING_STATE = params[0];
            mDataNumber = params[1];

            return getShare();
        }


        private List<ShareInfo> getShare() {

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("univ", PSContants.USERINFO.getmUniv()));
            params.add(new BasicNameValuePair("number", String.valueOf(mDataNumber)));

            switch (DATA_LOADING_STATE) {
                case FIRST_DATA_LOADING:
                    break;
                case LOAD_MORE_LOADING:
                    // size() - 1 자리는 progress item
                    showLog("mSIL size : " + String.valueOf(mShareInfoList.size()));

                    Date lastDate = mShareInfoList.get(mShareInfoList.size() - 2).getmCreatedDate();
                    params.add(new BasicNameValuePair("created_date", mSf.format(lastDate)));
                    break;
                case REFRESH_DATA_LOADING:
                    if (mShareInfoList != null) {
                        Date latestDate = mShareInfoList.get(0).getmCreatedDate();
                        params.add(new BasicNameValuePair("latest_date", mSf.format(latestDate)));
                    } else {
                        params.add(new BasicNameValuePair("latest_date", "0000-00-00"));
                    }
                    break;
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

            for (int i = 0; i < size; i++) {
                try {
                    shareInfo = new ShareInfo();

                    jsonObj = jsonArray.getJSONObject(i);

                    shareInfo.setmId(jsonObj.getLong("id"));
                    shareInfo.setmTitle(jsonObj.getString("title"));
                    shareInfo.setmExplanation(jsonObj.getString("explanation"));
                    shareInfo.setmImageName(jsonObj.getString("image"));

                    shareInfo.setmDday(mSf.parse(jsonObj.getString("d_day")));

                    shareInfo.setmTextLocation(jsonObj.getString("location"));
                    shareInfo.setmLatitude(jsonObj.getDouble("latitude"));
                    shareInfo.setmLongitude(jsonObj.getDouble("longitude"));

                    shareInfo.setmCreatedDate(mSf.parse(jsonObj.getString("created_date")));

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

            switch (DATA_LOADING_STATE) {
                case FIRST_DATA_LOADING:
                    mDialog.dismiss(); // dialog 없애기
                    isFirst = false;

                    if (shareInfoList == null) {
                        showToast("데이터가 없습니다 :(");
                    }  else {
                        mShareInfoList = shareInfoList;
                        initLoadSetting();
                    }

                    break;
                case LOAD_MORE_LOADING:

                    if (shareInfoList == null) {
                        mShareInfoList.remove(mShareInfoList.size() - 1);
                        mShareAdapter.notifyItemRemoved(mShareInfoList.size());

                        showToast("데이터가 없습니다 :(");
                    } else {
                        mShareInfoList.remove(mShareInfoList.size() - 1);
                        mShareAdapter.notifyItemRemoved(mShareInfoList.size());

                        int size = shareInfoList.size();
                        for (int i = 0; i < size; i++) {
                            mShareInfoList.add(shareInfoList.get(i));
                            mShareAdapter.notifyItemInserted(mShareInfoList.size() - 1);
                        }
                        mShareAdapter.setLoaded();
                    }

                    break;
                case REFRESH_DATA_LOADING:

                    if (shareInfoList == null) {
                        showToast("새로운 데이터가 없습니다 :(");
                    } else {

                        if (mShareInfoList == null) {
                            mShareInfoList = shareInfoList;
                            initLoadSetting();
                        } else {
                            int size = shareInfoList.size();
                            for (int i = 0; i < size; i++) {
                                mShareInfoList.add(0, shareInfoList.get(i));
                                mShareAdapter.notifyItemInserted(0);
                            }
                        }

                        mShareAdapter.setLoaded();
                        mRecyclerView.scrollToPosition(0);
                    }
                    break;
            } // end switch
        } // end onPostExecute

        private void initLoadSetting() {
            mShareAdapter = new ShareContentsAdapter(getActivity(), mShareInfoList, mRecyclerView);

            // scroll 했을 때 데이터 더 가져오는 이벤트 연결
            mShareAdapter.setOnLoadMoreListener(new ShareContentsAdapter.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    showLog("onLoadMore()");

                    // add progress item
                    mShareInfoList.add(null);
                    mShareAdapter.notifyItemInserted(mShareInfoList.size() - 1);

                    mSelectShareByUnivPhp = new SelectShareByUnivPhp();
                    mSelectShareByUnivPhp.execute(LOAD_MORE_LOADING, PSContants.SHARE_LOAD_COUNT);
                }
            });
            mRecyclerView.setAdapter(mShareAdapter);
        }




    }

}
