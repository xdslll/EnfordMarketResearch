package com.enford.market.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;

import com.enford.market.R;
import com.enford.market.helper.settinghelper.SettingUtility;
import com.enford.market.util.Consts;

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

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = this;
    }

    /**
     * 初始化返回按钮
     */
    protected void initBackButton() {
        mImgBack = (ImageButton) findViewById(R.id.back);
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackButtonClick();
            }
        });
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
}
