package com.enford.market.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.TypeReference;
import com.enford.market.R;
import com.enford.market.activity.scan.ScanCaptureActivity;
import com.enford.market.adapter.ResearchDetailAdapter;
import com.enford.market.helper.FastJSONHelper;
import com.enford.market.helper.HttpHelper;
import com.enford.market.model.EnfordApiMarketResearch;
import com.enford.market.model.EnfordProductCategory;
import com.enford.market.model.EnfordProductCommodity;
import com.enford.market.model.RespBody;
import com.enford.market.util.LogUtil;

import java.util.List;
import java.util.WeakHashMap;

import cz.msebera.android.httpclient.Header;
import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/9.
 */
public class ResearchDetailActivity extends BaseUserActivity {

    private ExpandableStickyListHeadersListView mListResearchDetail;
    ResearchDetailAdapter mAdapter;
    private WeakHashMap<View,Integer> mOriginalViewHeightPool = new WeakHashMap<View, Integer>();

    private ImageButton mBtnScan;
    private TextView mTxtTitle;

    //private PopupWindow mPopupAddPrice;
    private PricePopupWindow mPricePopup;

    EnfordApiMarketResearch mResearch;
    EnfordProductCategory mRootCategory;
    List<EnfordProductCategory> mCategoryList;

    ResearchDetailHandler mHandler = new ResearchDetailHandler(this);
    public static final int REQUEST_GET_CATEGORY = 0x01;
    public static final int REQUEST_ADD_PRICE = 0x02;
    public static final int REQUEST_UPDATE_PRICE = 0x03;

    private static class ResearchDetailHandler extends MyHandler {

        public ResearchDetailHandler(Activity activity) {
            super(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ResearchDetailActivity activity = (ResearchDetailActivity) getActivityWeakReference().get();
            switch (msg.what) {
                case REQUEST_GET_CATEGORY:
                    activity.handleGetCategory(msg);
                    break;
                case REQUEST_ADD_PRICE:

                    break;
                case REQUEST_UPDATE_PRICE:
                    activity.handleUpdatePrice(msg);
                    break;
            }
        }
    }

    private void handleUpdatePrice(Message msg) {
        String json = (String) msg.obj;
        RespBody resp = FastJSONHelper.deserializeAny(
                json, new TypeReference<RespBody>() {});
        if (resp.getCode().equals(SUCCESS)) {
            Toast.makeText(mCtx, resp.getMsg(), Toast.LENGTH_SHORT).show();
            mPricePopup.dismissPopupWindow();
        } else {
            Toast.makeText(mCtx, resp.getMsg(), Toast.LENGTH_SHORT).show();
            LogUtil.d(resp.getMsg());
        }
    }

    private void handleGetCategory(Message msg) {
        String json = (String) msg.obj;
        RespBody<List<EnfordProductCategory>> resp =
                FastJSONHelper.deserializeAny(json,
                        new TypeReference<RespBody<List<EnfordProductCategory>>>() {});
        if (resp.getCode().equals(SUCCESS)) {
            //获取分类数据
            mCategoryList = resp.getData();
            //填充ExpandableListView数据
            mAdapter = new ResearchDetailAdapter(mCtx, mCategoryList);
            mAdapter.setAddPriceListener(new ResearchDetailAdapter.OnAddPriceListener() {

                @Override
                public void onAddPriceListener(int position, EnfordProductCommodity cod, int tag) {
                    mPricePopup.setData(cod);
                    mPricePopup.setTag(tag);
                    mPricePopup.setUser(mUser);
                    mPricePopup.showPopupWindow(mListResearchDetail);
                }
            });

            mListResearchDetail.setAdapter(mAdapter);

        } else {
            Toast.makeText(mCtx, resp.getMsg(), Toast.LENGTH_SHORT).show();
            LogUtil.d(resp.getMsg());
        }
    }

    private void requestCategory() {
        HttpHelper.getCategoryCommodity(mCtx,
                String.valueOf(mResearch.getResearch().getId()),
                String.valueOf(mResearch.getDept().getId()),
                String.valueOf(mRootCategory.getCode()),
                new HttpHelper.JsonResponseHandler(mCtx) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Message msg = Message.obtain(mHandler, REQUEST_GET_CATEGORY, responseString);
                        msg.sendToTarget();
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mResearch = getIntent().getParcelableExtra("research");
        mRootCategory = getIntent().getParcelableExtra("category");

        setContentView(R.layout.research_detail);

        mTxtTitle = (TextView) findViewById(R.id.title);
        mListResearchDetail = (ExpandableStickyListHeadersListView) findViewById(R.id.research_detail_list);
        mBtnScan = (ImageButton) findViewById(R.id.research_detail_scan);

        mPricePopup = new PricePopupWindow(mCtx, mHandler);

        initBackButton();
        initListener();
        initData();

        requestCategory();
    }

    private void initData() {
        mTxtTitle.setText(mRootCategory.getName());
        mTxtTitle.append(String.format(getString(R.string.count), mRootCategory.getCodCount()));
    }

    private void initListener() {
        mListResearchDetail.setAnimExecutor(new AnimationExecutor());
        mListResearchDetail.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if (mListResearchDetail.isHeaderCollapsed(headerId)) {
                    mListResearchDetail.expand(headerId);
                } else {
                    mListResearchDetail.collapse(headerId);
                }
            }
        });

        mBtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mCtx, ScanCaptureActivity.class));
            }
        });

        mTxtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mCtx)
                        .setItems(new String[]{"全部", "未完成", "已完成"},
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Toast.makeText(ResearchDetailActivity.this, "点击了: " + which, Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .show();
            }
        });
    }

    //animation executor
    class AnimationExecutor implements ExpandableStickyListHeadersListView.IAnimationExecutor {

        @Override
        public void executeAnim(final View target, final int animType) {
            if(ExpandableStickyListHeadersListView.ANIMATION_EXPAND==animType&&target.getVisibility()==View.VISIBLE){
                return;
            }
            if(ExpandableStickyListHeadersListView.ANIMATION_COLLAPSE==animType&&target.getVisibility()!=View.VISIBLE){
                return;
            }
            if(mOriginalViewHeightPool.get(target)==null){
                mOriginalViewHeightPool.put(target,target.getHeight());
            }
            final int viewHeight = mOriginalViewHeightPool.get(target);
            float animStartY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? 0f : viewHeight;
            float animEndY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? viewHeight : 0f;
            final ViewGroup.LayoutParams lp = target.getLayoutParams();
            ValueAnimator animator = ValueAnimator.ofFloat(animStartY, animEndY);
            animator.setDuration(200);
            target.setVisibility(View.VISIBLE);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND) {
                        target.setVisibility(View.VISIBLE);
                    } else {
                        target.setVisibility(View.GONE);
                    }
                    target.getLayoutParams().height = viewHeight;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    lp.height = ((Float) valueAnimator.getAnimatedValue()).intValue();
                    target.setLayoutParams(lp);
                    target.requestLayout();
                }
            });
            animator.start();
        }
    }

    /*private void showPopupWindow() {
        if (mPopupAddPrice != null && !mPopupAddPrice.isShowing()) {
            mPopupAddPrice.showAtLocation(mListResearchDetail, Gravity.BOTTOM, 0, 0);
        } else {
            dismissPopupWindow();
        }
    }

    private void initPopupWindow() {
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

        Button btnConfirm = (Button) view.findViewById(R.id.add_price_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopupWindow();
            }
        });
    }

    private void dismissPopupWindow() {
        if (mPopupAddPrice != null && mPopupAddPrice.isShowing()) {
            mPopupAddPrice.dismiss();
        }
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPricePopup.dismissPopupWindow();
    }
}
