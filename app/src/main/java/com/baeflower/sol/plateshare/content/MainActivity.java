package com.baeflower.sol.plateshare.content;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.baeflower.sol.plateshare.R;
import com.baeflower.sol.plateshare.fragment.BuyFragment;
import com.baeflower.sol.plateshare.fragment.ShareFragment;
import com.baeflower.sol.plateshare.model.PSContent;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = MainActivity.class.getSimpleName();

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showLog(String message) {
        Log.d(TAG, message);
    }


    // 전체 레이아웃 (ViewPager)
    //private ActionBar mActionBar;
    private PagerSlidingTabStrip mTabs;

    private ViewPager mViewPager;
    private MyPagerAdapter mPagerAdapter;


    // Navigation View
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle; // v7
    private NavigationView mNavigation;


    // php 로 데이터 가져오기
    private String mUniv; // 컨텐츠 가져올 대학교


    // Shared Preference 에 저장할 xml 파일명
    private final String mSettingFileName = "plateshare_setting";
    private SharedPreferences mPrefUserSetting;
    private SharedPreferences.Editor mPrefEditor;


    // 콘텐츠 출력


    private void init() {
        mPrefUserSetting = getSharedPreferences(mSettingFileName, 0);
        mPrefEditor = mPrefUserSetting.edit(); // Preference Editor
        mUniv = mPrefUserSetting.getString("univ", "");


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        // mActionBar = this.getSupportActionBar();

        // -------------------- ViewPager 세팅
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                showLog(String.valueOf(position));


            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setAdapter(mPagerAdapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        mViewPager.setPageMargin(pageMargin);

        mTabs.setViewPager(mViewPager);

        // -------------------- Navigation View 세팅
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout_main);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setShowHideAnimationEnabled(true);

        mNavigation = (NavigationView) findViewById(R.id.navigation_view);
        mNavigation.setNavigationItemSelectedListener(this);


    }

    // 액션바 오버플로우 메뉴 레이아웃
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // 액션바 오버플로우 메뉴 실행
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_contact:
                return true;
        }
        return super.onOptionsItemSelected(item) || mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        showToast("onNavigationItemSelected()");

        return false;
    }


    private int mFinishCnt = 0;

    @Override
    public void onBackPressed() {

        // 기다리는 화면에서는 back 키가 동작 안하도록
        // getSupportFragmentManager().getFragments(); // stack 에 들어간 fragment 가 다 나오나?

        // 0 이 아닐 때, ...
        /*
            int backStackCnt = getSupportFragmentManager().getBackStackEntryCount();
            if (backStackCnt != 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        */


        if (mFinishCnt == 0) {
            showToast("한번 더 누를 경우 종료합니다 ^o^");
            mFinishCnt++;
        } else {
            super.onBackPressed();
        }

    }


    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        private final String[] mMenus = {"나누기", "같이사기"};
        private final int SHARE = 0;
        private final int BUY = 1;
        private int mCurrentMenu = 0;

        private ShareFragment mFragment;

        private ArrayList<PSContent> mPSContentList;

        public void setmPSContentList(ArrayList<PSContent> contentList) {
            mPSContentList = contentList;
        }

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mMenus[position];
        }

        @Override
        public int getCount() {
            return mMenus.length;
        }

        @Override
        public Fragment getItem(int position) {
            showLog("getItem_" + String.valueOf(position));

            switch (position) {
                case SHARE:
                    return ShareFragment.newInstance(mUniv);
                case BUY:
                    return BuyFragment.newInstance(mUniv);
                default:
                    return null;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            showLog("instantiateItem");
            return super.instantiateItem(container, position);
        }
    }


}
