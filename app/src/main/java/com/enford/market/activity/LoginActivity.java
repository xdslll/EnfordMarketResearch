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
import com.enford.market.helper.settinghelper.SettingUtility;
import com.enford.market.model.EnfordSystemUser;
import com.enford.market.model.RespBody;
import com.enford.market.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Type;

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
    EnfordSystemUser mUser;

    private static final int DRAWABLE_RIGHT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        mEdtUser = (EditText) findViewById(R.id.login_edt_user);
        mEdtPwd = (EditText) findViewById(R.id.login_edt_pwd);
        mBtnDoLogin = (Button) findViewById(R.id.login_dologin);

        mUser = getIntent().getParcelableExtra("user");
        if (mUser != null) {
            mEdtUser.setText(mUser.getUsername());
            mEdtPwd.setText(mUser.getPassword());
        }

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
                String user = mEdtUser.getText().toString().trim();
                String pwd = mEdtPwd.getText().toString().trim();
                if (TextUtils.isEmpty(user) ||
                        TextUtils.isEmpty(pwd)) {
                    Toast.makeText(mCtx, R.string.login_error_empty_user_pwd, Toast.LENGTH_SHORT).show();
                } else {
                    login(user, pwd);
                }
            }
        });
    }

    /**
     * 登录
     */
    private void login(final String user, final String pwd) {
        HttpHelper.login(mCtx, user, pwd,
                new HttpHelper.JsonResponseHandler(mCtx) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String json) {
                        LogUtil.e(json);
                        RespBody<EnfordSystemUser> resp = FastJSONHelper.deserializeAny(json, new TypeReference<RespBody<EnfordSystemUser>>() {});
                        if (resp != null) {
                            parseLogin(resp, json, user, pwd);
                        } else {
                            Toast.makeText(mCtx, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void parseLogin(RespBody<EnfordSystemUser> resp, String json, String user, String pwd) {
        try {
            if (resp.getCode().equals(SUCCESS)) {
                //保存登录用户信息
                SettingUtility.setDefaultUser(user, pwd);
                EnfordSystemUser enfordUser = resp.getData();
                //写入友盟统计
                MobclickAgent.onProfileSignIn(user);
                //跳转到主界面
                gotoMainActivity(enfordUser);
            } else {
                Toast.makeText(mCtx, resp.getMsg(), Toast.LENGTH_SHORT).show();
            }
        } catch (ClassCastException ex) {
            Type type = new TypeToken<RespBody<EnfordSystemUser>>(){}.getType();
            resp = new Gson().fromJson(json, type);
            parseLogin(resp, json, user, pwd);
        } catch (Exception ex) {
            Toast.makeText(mCtx, R.string.unknown_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void gotoMainActivity(EnfordSystemUser enfordUser) {
        //跳转到主界面
        finish();
        Intent intent = new Intent(mCtx, ResearchListActivity.class);
        intent.putExtra("user", enfordUser);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
