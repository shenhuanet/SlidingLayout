package com.shenhua.slidinglayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.shenhua.libs.slidinglayout.SlidingLayout;

/**
 * Created by shenhua on 11/11/2016.
 * Email shenhuanet@126.com
 */
public class ListViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        SlidingLayout mSlidingLayout = (SlidingLayout) findViewById(R.id.slidingLayout);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new Adapter());
        mSlidingLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        Log.i("onTouch", "up");
                        return false;
                    default:
                        return false;
                }
            }
        });
        mSlidingLayout.setSlidingListener(new SlidingLayout.SlidingListener() {
            @Override
            public void onSlidingOffset(View view, float delta) {
            }

            @Override
            public void onSlidingStateChange(View view, int state) {
            }

            @Override
            public void onSlidingChangePointer(View view, int pointerId) {

            }
        });
//        mSlidingLayout.setFrontView(front);
    }

    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 60;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(ListViewActivity.this, R.layout.list_item, null);
            }
            return convertView;
        }
    }
}
