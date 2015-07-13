package com.baeflower.sol.plateshare.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.baeflower.sol.plateshare.R;
import com.baeflower.sol.plateshare.activity.share.ShareCreateActivity;
import com.baeflower.sol.plateshare.fragment.BuyFragment;
import com.baeflower.sol.plateshare.fragment.ShareFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sol on 2015-07-07.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showLog(String message) {
        Log.d(TAG, message);
    }

    //
    private DrawerLayout mDrawerLayout;
    private CoordinatorLayout mCooldinatorLayout;


    //
    private boolean mNavigationViewOpen = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // ------------------------- Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar); // Toolbar 를 Actionbar 로 세팅

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setShowHideAnimationEnabled(true);

        // 어떤 Toolbar 는 세짝대기로 나오고, 어떤 Toolbar 는 화살표로 나오고.. 무슨 차이지?
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);


        mCooldinatorLayout = (CoordinatorLayout) findViewById(R.id.coorLayout_main_content);


        // ------------------------- Navigation View (옆에 나오는거)
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout_main);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer){
            @Override
            public void onDrawerClosed(View drawerView) {
                mNavigationViewOpen = false;

                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                showLog("onDrawerSlide");

                if (mNavigationViewOpen == true) {
                    /*
                    final int newLeftMargin = 5;
                    Animation a = new Animation() {

                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mCooldinatorLayout.getLayoutParams();
                            params.leftMargin = (int)(newLeftMargin * interpolatedTime);
                            mCooldinatorLayout.setLayoutParams(params);
                        }
                    };
                    a.setDuration(500); // in ms
                    mCooldinatorLayout.startAnimation(a);

                    */
                } else {


                }

                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mNavigationViewOpen = true;

                super.onDrawerOpened(drawerView);
            }
        };
        // actionbarToggle 을 drawer layout 에 세팅
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        // calling sync state is necessary or else your hamburger icon wont show up ... ???
        actionBarDrawerToggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
            navigationView.setNavigationItemSelectedListener(this);
        }


        // ------------------------- View Pager
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }


        // ------------------------- FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                    // setAction 에 click listener 안달면 UNDO 안나오네?!
                    Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                            .setAction(R.string.snackbar_action_undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();
                */

                // Share 글 등록 Activity 호출
                Intent intent = new Intent(MainActivity.this, ShareCreateActivity.class);
                startActivity(intent);

            }
        });


        // ------------------------- TAB
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_main);
        // viewPager setup 하기 전에 바꿔야된다!!! 이거때문에 삽질
        tabLayout.setTabTextColors(Color.parseColor("#8A8A8A"), Color.parseColor("#3F51B5"));
        tabLayout.setupWithViewPager(viewPager);



    }


    // Navigation View Item Selected
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        if (menuItem.isChecked())
            menuItem.setChecked(false);
        else
            menuItem.setCheckable(true);

        switch (menuItem.getItemId()) {
            case R.id.navigation_item_share:
                // Fragment 교체
                /*
                Toast.makeText(getApplicationContext(),"Share Selected",Toast.LENGTH_SHORT).show();
                ContentFragment fragment = new ContentFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame,fragment);
                fragmentTransaction.commit();
                return true;
                */

                return true;
            case R.id.navigation_item_buy:
                return true;
            case R.id.navigation_item_my_info_edit:
                return true;
        }


        return false;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new ShareFragment(), "나누기");
        adapter.addFragment(new BuyFragment(), "같이사기");
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }


}
