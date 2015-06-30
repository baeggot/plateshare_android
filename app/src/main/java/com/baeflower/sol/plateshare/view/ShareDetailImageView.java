package com.baeflower.sol.plateshare.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by sol on 2015-06-24.
 */
public class ShareDetailImageView extends ImageView {

    private static final String TAG = ShareDetailImageView.class.getSimpleName();

    public ShareDetailImageView(Context context) {
        this(context, null);
    }

    public ShareDetailImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShareDetailImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        // this(context, attrs, defStyleAttr, 0);
        super(context, attrs, defStyleAttr);

        init();
    }

//    public ShareDetailImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//
//        init();
//    }

    private void init() {
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int squareDim = 1000000000;

        int h = this.getMeasuredHeight();
        int w = this.getMeasuredWidth();
        int curSquareDim = Math.min(w, h);

        if (curSquareDim < squareDim) {
            squareDim = curSquareDim;
        }

        Log.d(TAG, "h " + h + "w " + w + ", squareDim " + squareDim);

        setMeasuredDimension(squareDim, squareDim);
    }

}
