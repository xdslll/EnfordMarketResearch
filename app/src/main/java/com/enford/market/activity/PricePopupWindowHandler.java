package com.enford.market.activity;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.enford.market.R;
import com.enford.market.helper.HttpHelper;
import com.enford.market.model.EnfordApiMarketResearch;
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
public class PricePopupWindowHandler implements Consts {

    private PopupWindow mPopupAddPrice;
    private PopupWindow mPopupCommodity;

    private Context mCtx;
    private Handler mHandler;
    private ViewHolder mCommodityViewHolder;

    private EnfordApiMarketResearch mResearch;
    private EnfordProductCommodity mCommodity;
    private EnfordSystemUser mUser;
    private int mPriceTag;

    private Button mBtnConfirm;
    private TextView mTxtCancel, mTxtConfirm, mTxtTitle;
    private EditText mEdtPromptPrice, mEdtOriginPrice, mEdtRemark;
    private CheckBox mCbkMissTag;
    private int mPosition = -1;

    public PricePopupWindowHandler(Context ctx, Handler handler) {
        this.mCtx = ctx;
        this.mHandler = handler;
        initPricePopupWindow();
    }

    public void showCommodityPopupWindow(View view) {
        if (mPopupCommodity != null && !mPopupCommodity.isShowing()) {
            EnfordProductPrice price = mCommodity.getPrice();
            mCommodityViewHolder.name.setText(mCommodity.getpName());
            mCommodityViewHolder.size.setText(String.format(
                    mCtx.getString(R.string.size),
                    mCommodity.getpSize(),
                    mCommodity.getUnit()));
            mCommodityViewHolder.barcode.setText(String.format(
                    mCtx.getString(R.string.barcode),
                    mCommodity.getBarCode()));
            mCommodityViewHolder.categoryName.setText(mCommodity.getCategoryName());
            /*if (price != null) {
                if (price.getRetailPrice() == null
                        || price.getRetailPrice() == 0) {
                    mCommodityViewHolder.orgPrice.setVisibility(View.GONE);
                } else {
                    mCommodityViewHolder.orgPrice.setVisibility(View.VISIBLE);
                    mCommodityViewHolder.orgPrice.setText(String.format(
                            mCtx.getString(R.string.origin_price),
                            String.valueOf(price.getRetailPrice() == null ?
                                    "0.0" : price.getRetailPrice())));
                }
                if (price.getPromptPrice() == null
                        || price.getPromptPrice() == 0) {
                    mCommodityViewHolder.proPrice.setVisibility(View.GONE);
                } else {
                    mCommodityViewHolder.proPrice.setVisibility(View.VISIBLE);
                    mCommodityViewHolder.proPrice.setText(String.format(
                            mCtx.getString(R.string.prompt_price),
                            String.valueOf(price.getPromptPrice() == null ?
                                    "0.0" : price.getPromptPrice())));
                }
                if (price.getMiss() == MISS_TAG_MISS) {
                    mCommodityViewHolder.missTag.setVisibility(View.VISIBLE);
                    mCommodityViewHolder.missTag.setText(R.string.add_price_miss_text);
                } else {
                    mCommodityViewHolder.missTag.setVisibility(View.GONE);
                }
            } else {
                mCommodityViewHolder.proPrice.setVisibility(View.GONE);
                mCommodityViewHolder.orgPrice.setVisibility(View.GONE);
                mCommodityViewHolder.missTag.setVisibility(View.GONE);
            }*/
            mPopupCommodity.showAtLocation(view, Gravity.TOP, 0, 50);
        }
    }

    public void showPopupWindow(View view) {
        if (mPopupAddPrice != null && !mPopupAddPrice.isShowing()) {
            //清空所有数据
            mEdtOriginPrice.setText("");
            mEdtPromptPrice.setText("");
            mEdtRemark.setText("");
            mCbkMissTag.setChecked(false);
            if (mCommodity.getPrice() != null) {
                setTag(PRICE_UPDATE_TAG);
            } else {
                setTag(PRICE_ADD_TAG);
            }
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
                    EnfordProductPrice price = mCommodity.getPrice();
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
            mTxtTitle.setText(mCommodity.getpName());
            mPopupAddPrice.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        } else {
            dismissPopupWindow();
        }
    }

    public void initCommodityPopupWindow() {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.scan_commodity, null);
        mPopupCommodity = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupCommodity.setTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        mPopupCommodity.setBackgroundDrawable(dw);
        mPopupCommodity.setOutsideTouchable(true);
        mPopupCommodity.setAnimationStyle(R.style.PopupWindowAnimationStyle2);

        mCommodityViewHolder = new ViewHolder();
        mCommodityViewHolder.name = (TextView) view.findViewById(R.id.research_detail_list_item_name);
        mCommodityViewHolder.size = (TextView) view.findViewById(R.id.research_detail_list_item_size);
        mCommodityViewHolder.barcode = (TextView) view.findViewById(R.id.research_detail_list_item_barcode);
        mCommodityViewHolder.orgPrice = (TextView) view.findViewById(R.id.research_detail_list_item_origin_price);
        mCommodityViewHolder.proPrice = (TextView) view.findViewById(R.id.research_detail_list_item_prompt_price);
        mCommodityViewHolder.missTag = (TextView) view.findViewById(R.id.research_detail_list_item_miss_tag);
        mCommodityViewHolder.categoryName = (TextView) view.findViewById(R.id.research_detail_list_item_category);
    }

    class ViewHolder {
        TextView name, size, barcode, orgPrice, proPrice, missTag, categoryName;
    }

    public void initPricePopupWindow() {
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
                sumbitPrice();
            }
        });

        mTxtConfirm.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               sumbitPrice();
           }
        });

        mTxtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopupWindow();
            }
        });

        //选中缺货状态，则不能填写促销价和原价
        mCbkMissTag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEdtOriginPrice.setEnabled(false);
                    mEdtPromptPrice.setEnabled(false);
                    mEdtPromptPrice.setText("");
                    mEdtOriginPrice.setText("");
                } else {
                    mEdtOriginPrice.setEnabled(true);
                    mEdtPromptPrice.setEnabled(true);
                }
            }
        });
    }

    public void sumbitPrice() {
        if (mUser != null) {
            EnfordProductPrice price = null;
            if (mCommodity != null && mCommodity.getPrice() != null && mPriceTag == PRICE_UPDATE_TAG) {
                price = mCommodity.getPrice();
            } else if (mPriceTag == PRICE_ADD_TAG) {
                price = new EnfordProductPrice();
                price.setComId(mCommodity.getId());
                price.setDeptId(mResearch.getDept().getId());
                price.setResId(mResearch.getResearch().getId());
                price.setCompId(mResearch.getComp().getId());
            } else {
                Toast.makeText(mCtx, R.string.add_price_error, Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                boolean miss = mCbkMissTag.isChecked();
                if (miss) {
                    price.setMiss(MISS_TAG_MISS);
                } else {
                    price.setMiss(MISS_TAG_NOT_MISS);
                    Float promptPrice = Float.valueOf(mEdtPromptPrice.getText().toString());
                    Float retailPrice = Float.valueOf(mEdtOriginPrice.getText().toString());
                    price.setPromptPrice(promptPrice);
                    price.setRetailPrice(retailPrice);
                }
                String remark = mEdtRemark.getText().toString().trim();
                price.setRemark(remark);
                price.setUploadBy(mUser.getId());
                price.setUploadDt(new Date());
                if (mPriceTag == PRICE_UPDATE_TAG) {
                    requestPriceUpdate(price);
                } else if (mPriceTag == PRICE_ADD_TAG) {
                    requestPriceAdd(price);
                }
            } catch (Exception ex) {
                Toast.makeText(mCtx, R.string.add_price_input_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mCtx, R.string.add_price_error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 请求新增价格信息
     *
     * @param price
     */
    private void requestPriceAdd(final EnfordProductPrice price) {
        HttpHelper.postAddPrice(mCtx, price,
                new HttpHelper.JsonResponseHandler(mCtx) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Message msg = Message.obtain(mHandler, ResearchDetailActivity.REQUEST_ADD_PRICE, responseString);
                        Bundle param = new Bundle();
                        param.putParcelable("price", price);
                        param.putInt("position", mPosition);
                        msg.setData(param);
                        msg.sendToTarget();
                    }
                });
    }

    /**
     * 请求更新价格信息
     *
     * @param price
     */
    private void requestPriceUpdate(final EnfordProductPrice price) {
        HttpHelper.postUpdatePrice(mCtx, price,
                new HttpHelper.JsonResponseHandler(mCtx) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Message msg = Message.obtain(mHandler, ResearchDetailActivity.REQUEST_ADD_PRICE, responseString);
                        Bundle param = new Bundle();
                        param.putParcelable("price", price);
                        param.putInt("position", mPosition);
                        msg.setData(param);
                        msg.sendToTarget();
                    }
                });
    }

    public void dismissPopupWindow() {
        if (mPopupAddPrice != null) {
            mPopupAddPrice.dismiss();
        }
        if (mPopupCommodity != null) {
            mPopupCommodity.dismiss();
        }
    }

    public void setCommodityData(EnfordProductCommodity cod) {
        this.mCommodity = cod;
    }

    public void setTag(int tag) {
        this.mPriceTag = tag;
    }

    public void setUser(EnfordSystemUser user) {
        this.mUser = user;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public void setResearchData(EnfordApiMarketResearch research) {
        this.mResearch = research;
    }
}
