package com.enford.market.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.enford.market.R;
import com.enford.market.model.EnfordSystemUser;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/11.
 */
public class BaseUserActivity extends BaseActivity {

    protected EnfordSystemUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkUser();
    }

    /**
     * 检查用户的登录状态
     */
    protected void checkUser() {
        mUser = getIntent().getParcelableExtra("user");
        if (mUser == null) {
            Toast.makeText(mCtx, R.string.need_login, Toast.LENGTH_SHORT).show();
            logout();
        }
    }
}
