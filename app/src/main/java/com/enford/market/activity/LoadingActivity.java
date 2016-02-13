package com.enford.market.activity;

import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.TypeReference;
import com.enford.market.R;
import com.enford.market.helper.FastJSONHelper;
import com.enford.market.helper.HttpHelper;
import com.enford.market.helper.settinghelper.SettingUtility;
import com.enford.market.model.EnfordSystemUser;
import com.enford.market.model.RespBody;

import cz.msebera.android.httpclient.Header;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/12.
 */
public class LoadingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        new MyHandler(this).postDelayed(new Runnable() {
            @Override
            public void run() {
                autoLogin();
            }
        }, 3000);
    }

    /**
     * 自动登录
     */
    private void autoLogin() {
        EnfordSystemUser user = SettingUtility.getDefaultUser();
        if (user != null) {
            login(user.getUsername(), user.getPassword());
        } else {
            gotoLogin();
        }
    }

    private void gotoLogin() {
        finish();
        Intent intent = new Intent(mCtx, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * 登录
     */
    private void login(final String user, final String pwd) {
        HttpHelper.login(mCtx, user, pwd,
                new HttpHelper.JsonResponseHandler(mCtx) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String json) {
                        RespBody<EnfordSystemUser> resp = FastJSONHelper.deserializeAny(json, new TypeReference<RespBody<EnfordSystemUser>>() {});
                        if (resp == null) {
                            gotoLogin();
                            return;
                        }
                        if (resp.getCode().equals(SUCCESS)) {
                            //保存登录用户信息
                            SettingUtility.setDefaultUser(user, pwd);
                            EnfordSystemUser enfordUser = resp.getData();
                            //跳转到主界面
                            finish();
                            Intent intent = new Intent(mCtx, ResearchListActivity.class);
                            intent.putExtra("user", enfordUser);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            //Toast.makeText(mCtx, resp.getMsg(), Toast.LENGTH_SHORT).show();
                            gotoLogin();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String json, Throwable throwable) {
                        gotoLogin();
                    }
                });
    }
}
