package com.baeflower.sol.plateshare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baeflower.sol.plateshare.R;
import com.baeflower.sol.plateshare.model.PSContent;
import com.baeflower.sol.plateshare.phpUtil.SelectDetailContentPhp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by sol on 2015-06-09.
 */
public class FragmentContentsAdapter extends BaseAdapter {



    private LayoutInflater inflater;
    private Context mContext;
    private ViewHolder viewHolder;

    // 출력 데이터
    private JSONArray mJsonArr;
    private List<PSContent> mDataList;

    //
    private SelectDetailContentPhp mSelectDetailPhp;



    // ViewHolder static class
    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

    // 생성자
    public FragmentContentsAdapter(Context context) {
        mContext = context;
    }

    // 생성자
    public FragmentContentsAdapter(Context context, List<PSContent> contentList) {
        mContext = context;
        mDataList = contentList;
    }

    // 생성자
    public FragmentContentsAdapter(Context context, List<PSContent> contentList, JSONArray jsonArray) {
        mContext = context;
        mDataList = contentList;
        mJsonArr = jsonArray;
        mSelectDetailPhp = new SelectDetailContentPhp();
    }


    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public PSContent getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null){
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.list_items_share, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_list_items_share_image);
            TextView title = (TextView) view.findViewById(R.id.tv_list_items_buy_title);

            viewHolder = new ViewHolder();
            viewHolder.imageView = imageView;
            viewHolder.textView = title;

            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        /*
            int size = mJsonArr.length();
            for (int i = 0; i < size; i++) {
                JSONObject jsonObj = mJsonArr.getJSONObject(i);
                tmpContent = new PSContent();

                tmpContent.setmId(jsonObj.getInt("id"));
                tmpContent.setmTitle(jsonObj.getString("title"));
         */

        try {
            JSONObject jsonObj = mJsonArr.getJSONObject(position);
            int id = jsonObj.getInt("id");
            mSelectDetailPhp.execute(String.valueOf(id));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return view;
    }
}
