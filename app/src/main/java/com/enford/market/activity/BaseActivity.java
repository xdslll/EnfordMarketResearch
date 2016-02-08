package com.enford.market.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/8.
 */
public abstract class BaseActivity extends Activity {

    Activity mCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = this;
    }
}
