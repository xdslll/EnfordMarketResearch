package com.enford.market.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.TypeReference;
import com.enford.market.R;
import com.enford.market.helper.FastJSONHelper;
import com.enford.market.helper.HttpHelper;
import com.enford.market.model.EnfordSystemUser;
import com.enford.market.model.RespBody;

import cz.msebera.android.httpclient.Header;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/7.
 */
public class LoginActivity extends BaseActivity {

    EditText mEdtUser, mEdtPwd;
    Button mBtnDoLogin;

    private static final int DRAWABLE_RIGHT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mEdtUser = (EditText) findViewById(R.id.login_edt_user);
        mEdtPwd = (EditText) findViewById(R.id.login_edt_pwd);
        mBtnDoLogin = (Button) findViewById(R.id.login_dologin);

        /**
         * 测试语句
         */
        mEdtUser.setText("test");
        mEdtPwd.setText("test");
        //login();


        initListner();
    }

    private void initListner() {
        setEdtEditorListener(mEdtUser);
        setEdtEditorListener(mEdtPwd);
        setEdtRightButton(mEdtUser);
        setEdtRightButton(mEdtPwd);
        setButtonListener();
    }

    private void setButtonListener() {
        //设置按钮点击事件
        mBtnDoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEdtUser.getText()) ||
                        TextUtils.isEmpty(mEdtPwd.getText())) {
                    Toast.makeText(mCtx, R.string.login_error_empty_user_pwd, Toast.LENGTH_SHORT).show();
                } else {
                    login();
                }
            }
        });
    }

    /**
     * 登录
     */
    private void login() {
        String user = mEdtUser.getText().toString().trim();
        String pwd = mEdtPwd.getText().toString().trim();
        HttpHelper.login(mCtx, user, pwd,
                new HttpHelper.JsonResponseHandler(mCtx) {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String json, Throwable throwable) {
                        RespBody<EnfordSystemUser> resp = FastJSONHelper.deserializeAny(json, new TypeReference<RespBody<EnfordSystemUser>>() {});
                        if (resp != null) {
                            Toast.makeText(mCtx, resp.getMsg(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mCtx, R.string.login_failed, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String json) {
                        RespBody<EnfordSystemUser> resp = FastJSONHelper.deserializeAny(json, new TypeReference<RespBody<EnfordSystemUser>>() {});
                        Toast.makeText(mCtx, resp.getMsg(), Toast.LENGTH_SHORT).show();
                        if (resp.getCode().equals(SUCCESS)) {
                            Intent intent = new Intent(mCtx, ResearchListActivity.class);
                            mCtx.startActivity(intent);
                            finish();
                        }
                    }
        });
    }

    /**
     * 当用户在文本框输入文本时，显示清空文本按钮，如果文本框没有文本，则不显示
     * @param edt
     */
    private void setEdtEditorListener(final EditText edt) {
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt.getText().length() > 0) {
                    if (edt.getCompoundDrawables()[DRAWABLE_RIGHT] == null) {
                        showEdtClearBtn(edt);
                    }
                } else {
                    edt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && edt.getText().length() > 0) {
                    if (edt.getCompoundDrawables()[DRAWABLE_RIGHT] == null) {
                        showEdtClearBtn(edt);
                    }
                } else {
                    edt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                }
            }
        });
    }

    /**
     * 显示文本框清空文本按钮
     *
     * @param edt
     */
    private void showEdtClearBtn(EditText edt) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.clear);
        Drawable drawable = new BitmapDrawable(getResources(), bmp);
        edt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null);
    }

    /**
     * 设置清空文本按钮的点击事件
     *
     * @param edt
     */
    private void setEdtRightButton(final EditText edt) {
        edt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        Drawable drawableRight = edt.
                                getCompoundDrawables()[DRAWABLE_RIGHT];
                        if (drawableRight != null && event.getRawX() >=
                                (edt.getRight() - drawableRight.getBounds().width())) {
                            edt.setText("");
                            return true;
                        }
                        break;
                }
                return false;
            }
        });
    }

}
