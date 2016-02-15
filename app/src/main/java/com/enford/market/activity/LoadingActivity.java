package com.enford.market.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.TypeReference;
import com.enford.market.R;
import com.enford.market.helper.FastJSONHelper;
import com.enford.market.helper.HttpHelper;
import com.enford.market.helper.settinghelper.SettingUtility;
import com.enford.market.model.EnfordSystemUser;
import com.enford.market.model.RespBody;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import cz.msebera.android.httpclient.Header;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/12.
 */
public class LoadingActivity extends BaseActivity {

    RelativeLayout mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        mMainLayout = (RelativeLayout) findViewById(R.id.main);
        mMainLayout.setEnabled(false);
        mMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoLogin();
            }
        });

        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case UpdateStatus.Yes:
                        UmengUpdateAgent.showUpdateDialog(mCtx, updateInfo);
                        break;
                    case UpdateStatus.No:
                        autoJump();
                        break;
                    case UpdateStatus.Timeout:
                        autoJump();
                        break;
                }
            }
        });
        UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {
            @Override
            public void onClick(int status) {
                switch (status) {
                    case UpdateStatus.NotNow:
                        autoJump();
                        break;
                    case UpdateStatus.Ignore:
                        autoJump();
                        break;
                }
            }
        });
        UmengUpdateAgent.update(mCtx);
    }

    private void autoJump() {
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
            //login(user.getUsername(), user.getPassword());
            //跳转到主界面
            finish();
            Intent intent = new Intent(mCtx, LoginActivity.class);
            intent.putExtra("user", user);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(R.anim.fade, R.anim.hold);
        } else {
            gotoLogin();
        }
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
                            try {
                                EnfordSystemUser enfordUser = resp.getData();
                                if (enfordUser != null
                                        && enfordUser.getOrgId() != null
                                        && enfordUser.getDeptId() != null) {
                                    //跳转到主界面
                                    finish();
                                    Intent intent = new Intent(mCtx, LoginActivity.class);
                                    intent.putExtra("user", enfordUser);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fade, R.anim.hold);
                                } else {
                                    Toast.makeText(mCtx, R.string.login_failed_none_dept, Toast.LENGTH_SHORT).show();
                                    gotoLogin();
                                }
                            } catch (Exception ex) {
                                Toast.makeText(mCtx, R.string.auto_login_error, Toast.LENGTH_SHORT).show();
                                gotoLogin();
                            }
                        } else {
                            Toast.makeText(mCtx, resp.getMsg(), Toast.LENGTH_SHORT).show();
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
