package com.enford.market.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enford.market.R;
import com.enford.market.helper.settinghelper.SettingUtility;
import com.enford.market.util.Consts;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/8.
 */
public abstract class BaseActivity extends Activity implements Consts {

    protected Activity mCtx;

    private ImageButton mImgBack;
    private TextView mTxtTitle;
    private ImageButton mImgRight;
    private RelativeLayout mLayoutTitle;
    private ViewStub mViewStubLoading = null;
    private LinearLayout mLayoutLoading = null;
    private TextView mTxtLoadingText = null;

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //日志加密
        AnalyticsConfig.enableEncrypt(true);
        //友盟统计
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //友盟统计
        MobclickAgent.onPause(this);
    }

    /**
     * 初始化返回按钮
     */
    protected void initBackButton() {
        mImgBack = (ImageButton) findViewById(R.id.back);
        mImgBack.setVisibility(View.VISIBLE);
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackButtonClick();
            }
        });
    }

    protected void initTitle(Object text) {
        String title = null;
        if (text instanceof Integer) {
            title = getString((Integer) text);
        } else if (text instanceof String) {
            title = (String) text;
        }
        if (title != null) {
            mTxtTitle = (TextView) findViewById(R.id.title);
            mTxtTitle.setText(title);
        }
    }

    protected void hideBackButton() {
        mImgBack.setVisibility(View.GONE);
    }

    protected void hideTitleBar() {
        mLayoutTitle = (RelativeLayout) findViewById(R.id.title_layout);
        mLayoutTitle.setVisibility(View.GONE);
    }

    protected void initTitleRightButton(Drawable drawable, View.OnClickListener listener) {
        mImgRight = (ImageButton) findViewById(R.id.right);
        mImgRight.setVisibility(View.VISIBLE);
        mImgBack.setImageDrawable(drawable);
        mImgBack.setOnClickListener(listener);
    }

    protected void initTitleBar(Object text) {
        initBackButton();
        initTitle(text);
    }

    protected void setTitleText(String text) {
        mTxtTitle.setText(text);
    }

    protected void appenpTitleText(String text) {
        mTxtTitle.append(text);
    }

    /**
     * 返回按钮的点击事件
     */
    protected void onBackButtonClick() {
        finish();
    }

    protected void logout() {
        SettingUtility.clearDefaultUser();
        finish();
        Intent intent = new Intent(mCtx, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public static class MyHandler extends Handler {

        private WeakReference<Activity> activityWeakReference;

        public MyHandler(Activity activity) {
            this.activityWeakReference = new WeakReference<Activity>(activity);
        }

        public WeakReference<Activity> getActivityWeakReference() {
            return activityWeakReference;
        }
    }

    protected String formatDate(Date date) {
        return mDateFormat.format(date);
    }

    protected void gotoLogin() {
        finish();
        Intent intent = new Intent(mCtx, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * 显示Loading进度条
     *
     */
    protected void showLoadingView() {
        if (mLayoutLoading == null) {
            mViewStubLoading = (ViewStub) findViewById(R.id.common_viewstub);
            mViewStubLoading.inflate();
            mLayoutLoading = (LinearLayout) findViewById(R.id.common_loading_layout);
            mTxtLoadingText = (TextView) findViewById(R.id.common_loading_layout_text);
            mLayoutLoading.setVisibility(View.VISIBLE);
        } else {
            mLayoutLoading.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏Loading进度条
     */
    protected void dismissLoadingView() {
        if (mLayoutLoading != null) {
            mLayoutLoading.setVisibility(View.GONE);
        }
    }

    /**
     * 设置Loading文本
     *
     * @param text
     */
    protected void setLoadingViewText(String text) {
        if (mTxtLoadingText != null) {
            mTxtLoadingText.setText(text);
        }
    }
}
