package com.enford.market.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.enford.market.R;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/7.
 */
public class SettingsActivity extends BaseActivity {

    RelativeLayout mFeedback;
    Button mBtnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        initBackButton();

        mFeedback = (RelativeLayout) findViewById(R.id.settings_feedback);
        mFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mCtx, SettingsFeedbackActivity.class));
            }
        });

        mBtnLogout = (Button) findViewById(R.id.settings_logout);
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
