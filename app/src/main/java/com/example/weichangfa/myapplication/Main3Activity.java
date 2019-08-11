package com.example.weichangfa.myapplication;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Main3Activity extends AppCompatActivity {

    private PopupWindow popupWindow = null;
    private static final int strNum = R.id.custom_layout_title;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private  LinearLayout rlContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
     rlContent = (LinearLayout) findViewById(R.id.content_main3);
        setSupportActionBar(toolbar);
        List<String> mList = new ArrayList<>();
        mList.add("你好1");
        mList.add("你好11");
        mList.add("你好111");
        rlContent.addView(createView(1,mList));
        List<String> mList2 = new ArrayList<>();
        mList2.add("你好2");
        mList2.add("你好22");
        mList2.add("你好222");
        rlContent.addView(createView(2,mList2));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow = new PopupWindow();
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                View viewPopWindow = View.inflate(Main3Activity.this, R.layout.fragment_frist, null);
                popupWindow.setContentView(viewPopWindow);
                popupWindow.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
                popupWindow.setHeight(800);
                popupWindow.showAsDropDown(toolbar);
                // popupWindow.showAtLocation(toolbar, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void main3(View view) {
        popupWindow.dismiss();
    }

    /**
     * 创建 备注下面的热点
     *
     * @return
     */
    private ViewGroup createView(int type,List<String> mList) {
        ScrollView mScrollView = new ScrollView(this);
        mScrollView.setFillViewport(true);

        LinearLayout llContent = new LinearLayout(this);


        llContent.setId(R.id.custom_layout_title);
        LinearLayout.LayoutParams llPamarams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        llContent.setLayoutParams(llPamarams);
        llContent.setOrientation(LinearLayout.VERTICAL);
        final TextView titleTv = new TextView(this);
        titleTv.setLayoutParams(new ViewGroup.LayoutParams(DeviceUtil.px2dip(this, 500), ViewGroup.LayoutParams.WRAP_CONTENT));
        titleTv.setTextColor(getResources().getColor(R.color.text_black));
        titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19.33f);
        titleTv.setText("你好请问这：");
        llContent.addView(titleTv);

        final FlowLayout mLayout = getFlowLayout(mScrollView,type,mList);
        // llContent.addView(mLayout);
        mScrollView.addView(mLayout);
        llContent.addView(mScrollView);
        return llContent;

    }

    @NonNull
    private FlowLayout getFlowLayout(final ScrollView mScrollView, int type,List<String> mList) {
        final FlowLayout mLayout = new FlowLayout(this);
        int layoutPadding = DeviceUtil.dip2px(this, 10);
        int layoutPaddingTop = DeviceUtil.dip2px(this, 13);
        mLayout.setPadding(layoutPadding, layoutPaddingTop, layoutPadding, layoutPadding);
        mLayout.setHorizontalSpacing(layoutPadding);
        mLayout.setVerticalSpacing(layoutPaddingTop);

        int textPaddingV = DeviceUtil.dip2px(this, 10);
        int textPaddingH = DeviceUtil.dip2px(this, 13);
        int contentColor = getResources().getColor(R.color.title);
        int backColor = getResources().getColor(R.color.title);
        int radius = DeviceUtil.dip2px(this, 5);
        for (int i = 0; i < mList.size(); i++) {
            final TextView tv = new TextView(this);
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DeviceUtil.px2dip(this, 30)));
            int color = getResources().getColor(R.color.bg_white);

            final String text = mList.get(i);
            tv.setText(text);
            tv.setTextColor(getResources().getColor(R.color.text_black));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15.33f);

            final GradientDrawable normalDrawable = DrawableUtils.createDrawable(color, backColor, radius);
            final GradientDrawable pressDrawable = DrawableUtils.createDrawable(contentColor, backColor, radius);
            tv.setBackgroundDrawable(normalDrawable);
            tv.setTag(i);
            tv.setTag(strNum, type);

            int  type22 = (int) tv.getTag(strNum);


            com.orhanobut.logger.Logger.d("1111111主线程22==="+type22);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(textPaddingH, textPaddingV, textPaddingH, textPaddingV);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) v.getTag();
                    for (int j = 0; j < mLayout.getChildCount(); j++) {
                        TextView childTV = (TextView) mLayout.getChildAt(j);
                        childTV.setTextColor(getResources().getColor(R.color.text_black));
                        childTV.setBackgroundDrawable(normalDrawable);

                    }
                    TextView selectTv = (TextView) v;
                    selectTv.setTextColor(getResources().getColor(R.color.bg_white));
                    selectTv.setBackgroundDrawable(pressDrawable);


                    int  type = (int) tv.getTag(strNum);


                    com.orhanobut.logger.Logger.d("1111111主线程==="+type);
                    ViewGroup childView=(ViewGroup)rlContent.getChildAt(type);
                    if(childView!=null){
                        childView.removeAllViews();
                        List<String> mList4 = new ArrayList<>();
                        mList4.add("aaaa"+position);
                        mList4.add("bbbb"+position);
                        mList4.add("ccc"+position);
                        childView.addView(createView(type+1,mList4));
                    }

                }
            });

            mLayout.addView(tv);
        }
        return mLayout;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main3 Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
