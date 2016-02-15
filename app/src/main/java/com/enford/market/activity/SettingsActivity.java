package com.enford.market.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.enford.market.R;
import com.enford.market.helper.ImageHelper;
import com.umeng.fb.FeedbackAgent;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/7.
 */
public class SettingsActivity extends BaseActivity {

    RelativeLayout mClearCache;
    RelativeLayout mFeedback;
    RelativeLayout mAbout;
    Button mBtnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        initBackButton();

        mClearCache = (RelativeLayout) findViewById(R.id.settings_clear_cache);
        mFeedback = (RelativeLayout) findViewById(R.id.settings_feedback);
        mAbout = (RelativeLayout) findViewById(R.id.settings_about);
        mBtnLogout = (Button) findViewById(R.id.settings_logout);

        mClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageHelper.clearCache();
                Toast.makeText(mCtx, R.string.clear_cache_success, Toast.LENGTH_SHORT).show();
            }
        });

        mFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(mCtx, SettingsFeedbackActivity.class));
                FeedbackAgent agent = new FeedbackAgent(mCtx);
                agent.startFeedbackActivity();
            }
        });

        mAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mCtx)
                        .setTitle(R.string.settings_about)
                        .setMessage(R.string.copy_right)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mCtx)
                        .setTitle(R.string.confirm_title)
                        .setMessage(R.string.confirm_logout)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
    }

}
