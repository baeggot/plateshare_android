package com.baeflower.sol.plateshare.test;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.baeflower.sol.plateshare.R;

public class RecyclerTestActivity extends ActionBarActivity {


    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_test);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setAdapter(LargeAdapter.newInstance(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recycler_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_top) {
            recyclerView.smoothScrollToPosition(0);
            return true;
        } else if (id == R.id.action_bottom) {
            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
