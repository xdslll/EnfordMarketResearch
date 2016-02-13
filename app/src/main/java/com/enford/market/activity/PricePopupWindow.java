package com.enford.market.activity;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.enford.market.R;
import com.enford.market.helper.HttpHelper;
import com.enford.market.model.EnfordProductCommodity;
import com.enford.market.model.EnfordProductPrice;
import com.enford.market.model.EnfordSystemUser;
import com.enford.market.util.Consts;

import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/13.
 */
public class PricePopupWindow implements Consts {

    private PopupWindow mPopupAddPrice;

    private Context mCtx;
    private BaseActivity.MyHandler mHandler;

    private EnfordProductCommodity mCod;
    private EnfordSystemUser mUser;
    private int mPriceTag;

    private Button mBtnConfirm;
    private TextView mTxtCancel, mTxtConfirm, mTxtTitle;
    private EditText mEdtPromptPrice, mEdtOriginPrice, mEdtRemark;
    private CheckBox mCbkMissTag;

    public PricePopupWindow(Context ctx, BaseActivity.MyHandler handler) {
        this.mCtx = ctx;
        this.mHandler = handler;
        initPopupWindow();
    }

    public void showPopupWindow(View view) {
        if (mPopupAddPrice != null && !mPopupAddPrice.isShowing()) {
            switch (mPriceTag) {
                case PRICE_ADD_TAG:
                    mTxtCancel.setTextColor(mCtx.getResources().getColorStateList(R.drawable.btn_text_color));
                    mTxtConfirm.setTextColor(mCtx.getResources().getColorStateList(R.drawable.btn_text_color));
                    mBtnConfirm.setBackground(mCtx.getResources().getDrawable(R.drawable.btn_bg));
                    mBtnConfirm.setTextColor(mCtx.getResources().getColorStateList(R.drawable.btn_text_color));
                    mEdtPromptPrice.setBackground(mCtx.getResources().getDrawable(R.drawable.edittext_bg2));
                    mEdtOriginPrice.setBackground(mCtx.getResources().getDrawable(R.drawable.edittext_bg2));
                    mEdtRemark.setBackground(mCtx.getResources().getDrawable(R.drawable.edittext_bg2));
                    break;
                case PRICE_UPDATE_TAG:
                    mTxtCancel.setTextColor(mCtx.getResources().getColorStateList(R.drawable.btn_text_color_orange));
                    mTxtConfirm.setTextColor(mCtx.getResources().getColorStateList(R.drawable.btn_text_color_orange));
                    mBtnConfirm.setBackground(mCtx.getResources().getDrawable(R.drawable.btn_bg_orange));
                    mBtnConfirm.setTextColor(mCtx.getResources().getColorStateList(R.drawable.btn_text_color_orange));
                    mEdtPromptPrice.setBackground(mCtx.getResources().getDrawable(R.drawable.edittext_bg2_orange));
                    mEdtOriginPrice.setBackground(mCtx.getResources().getDrawable(R.drawable.edittext_bg2_orange));
                    mEdtRemark.setBackground(mCtx.getResources().getDrawable(R.drawable.edittext_bg2_orange));
                    EnfordProductPrice price = mCod.getPrice();
                    if (price != null) {
                        if (price.getPromptPrice() != null
                                && price.getPromptPrice() != 0) {
                            mEdtPromptPrice.setText(String.valueOf(price.getPromptPrice()));
                        }
                        if (price.getRetailPrice() != null
                                && price.getRetailPrice() != 0) {
                            mEdtOriginPrice.setText(String.valueOf(price.getRetailPrice()));
                        }
                        if (price.getRemark() != null) {
                            mEdtRemark.setText(String.valueOf(price.getRemark()));
                        }
                        if (price.getMiss() != null
                                && price.getMiss() == MISS_TAG_MISS) {
                            mCbkMissTag.setChecked(true);
                        } else {
                            mCbkMissTag.setChecked(false);
                        }
                    }
                    break;
                default:
            }
            mTxtTitle.setText(mCod.getpName());
            mPopupAddPrice.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        } else {
            dismissPopupWindow();
        }
    }



    public void initPopupWindow() {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.add_price, null);
        mPopupAddPrice = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupAddPrice.setTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        mPopupAddPrice.setBackgroundDrawable(dw);
        mPopupAddPrice.setOutsideTouchable(true);
        mPopupAddPrice.setAnimationStyle(R.style.PopupWindowAnimationStyle);

        mBtnConfirm = (Button) view.findViewById(R.id.add_price_confirm);
        mTxtCancel = (TextView) view.findViewById(R.id.add_price_cancel);
        mTxtConfirm = (TextView) view.findViewById(R.id.add_price_submit);
        mTxtTitle = (TextView) view.findViewById(R.id.add_price_title);
        mEdtOriginPrice = (EditText) view.findViewById(R.id.add_price_origin_price);
        mEdtPromptPrice = (EditText) view.findViewById(R.id.add_price_prompt_price);
        mEdtRemark = (EditText) view.findViewById(R.id.add_price_remark);
        mCbkMissTag = (CheckBox) view.findViewById(R.id.add_price_miss_tag);

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sumbit();
            }
        });

        mTxtConfirm.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               sumbit();
           }
        });

        mTxtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopupWindow();
            }
        });
    }

    public void sumbit() {
        if (mCod != null && mCod.getPrice() != null &&
                mUser != null && mPriceTag == PRICE_UPDATE_TAG) {
            EnfordProductPrice price = mCod.getPrice();
            try {
                Float promptPrice = Float.valueOf(mEdtPromptPrice.getText().toString());
                Float retailPrice = Float.valueOf(mEdtOriginPrice.getText().toString());
                String remark = mEdtRemark.getText().toString().trim();
                boolean miss = mCbkMissTag.isChecked();
                if (miss) {
                    price.setMiss(MISS_TAG_MISS);
                } else {
                    price.setMiss(MISS_TAG_NOT_MISS);
                }
                price.setPromptPrice(promptPrice);
                price.setRetailPrice(retailPrice);
                price.setRemark(remark);
                price.setUploadBy(mUser.getId());
                price.setUploadDt(new Date());
                requestPriceUpdate(price);
            } catch (Exception ex) {
                Toast.makeText(mCtx, R.string.add_price_input_error, Toast.LENGTH_SHORT).show();
            }
        } else if (mUser != null && mPriceTag == PRICE_ADD_TAG) {

        } else {
            Toast.makeText(mCtx, R.string.add_price_error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 请求更新价格信息
     *
     * @param price
     */
    private void requestPriceUpdate(EnfordProductPrice price) {
        HttpHelper.postUpdatePrice(mCtx, price,
                new HttpHelper.JsonResponseHandler(mCtx) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Message msg = Message.obtain(mHandler, ResearchDetailActivity.REQUEST_UPDATE_PRICE, responseString);
                        msg.sendToTarget();
                    }
                });
    }

    public void dismissPopupWindow() {
        if (mPopupAddPrice != null && mPopupAddPrice.isShowing()) {
            mPopupAddPrice.dismiss();
        }
    }

    public void setData(EnfordProductCommodity cod) {
        this.mCod = cod;
    }

    public void setTag(int tag) {
        this.mPriceTag = tag;
    }

    public void setUser(EnfordSystemUser user) {
        this.mUser = user;
    }
}
