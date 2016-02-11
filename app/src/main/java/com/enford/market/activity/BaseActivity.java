package com.enford.market.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.enford.market.R;
import com.enford.market.util.Consts;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/8.
 */
public abstract class BaseActivity extends Activity implements Consts {

    Activity mCtx;

    ImageButton mImgBack;

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
}
