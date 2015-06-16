package com.baeflower.sol.plateshare.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.baeflower.sol.plateshare.R;
import com.baeflower.sol.plateshare.content.MainActivity;

/**
 * Created by sol on 2015-06-09.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public MainActivityTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    public void testHelloString(){
        Activity activity = getActivity();
        TextView tvHello = (TextView) activity.findViewById(android.R.id.text1);
        assertEquals(activity.getText(R.string.hello_world), tvHello.getText().toString());
    }
}
