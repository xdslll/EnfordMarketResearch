package com.enford.market.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.enford.market.R;
import com.enford.market.util.InputMethodUtil;

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

        initListner();
    }

    private void initListner() {
        setEdtFocusListener(mEdtUser);
        setEdtFocusListener(mEdtPwd);
        setEdtRightButton(mEdtUser);
        setEdtRightButton(mEdtPwd);
        setButtonListener();
    }

    private void setButtonListener() {
        mBtnDoLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mBtnDoLogin.setTextColor(Color.parseColor("#bfbfbf"));
                        break;
                    case MotionEvent.ACTION_UP:
                        mBtnDoLogin.setTextColor(Color.parseColor("#7acb4d"));
                        break;
                }
                InputMethodUtil.hideKeyBoard(mCtx);
                return false;
            }
        });
        mBtnDoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEdtUser.getText()) ||
                        TextUtils.isEmpty(mEdtPwd.getText())) {
                    Toast.makeText(mCtx, R.string.login_error_text, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setEdtFocusListener(final EditText edt) {
        edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.clear);
                    Drawable drawable = new BitmapDrawable(getResources(), bmp);
                    edt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null);
                } else {
                    edt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                }
            }
        });
    }

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
