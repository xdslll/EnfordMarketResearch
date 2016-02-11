package com.enford.market.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.enford.market.R;
import com.enford.market.activity.scan.ScanActivity;
import com.enford.market.adapter.ResearchDetailAdapter;
import com.enford.market.util.LogUtil;

import java.util.WeakHashMap;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/9.
 */
public class ResearchDetailActivity extends BaseActivity {

    ExpandableStickyListHeadersListView mListResearchDetail;
    ResearchDetailAdapter mAdapter;
    WeakHashMap<View,Integer> mOriginalViewHeightPool = new WeakHashMap<View, Integer>();

    ImageButton mBtnScan;
    TextView mTxtTitle;

    PopupWindow mPopupAddPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.research_detail);

        initBackButton();
        initPopupWindow();

        mListResearchDetail = (ExpandableStickyListHeadersListView) findViewById(R.id.research_detail_list);
        mListResearchDetail.setAnimExecutor(new AnimationExecutor());
        mAdapter = new ResearchDetailAdapter(mCtx);
        mAdapter.setAddPriceListener(new ResearchDetailAdapter.OnAddPriceListener() {
            @Override
            public void onAddPriceListener() {
                showPopupWindow();
            }
        });

        mListResearchDetail.setAdapter(mAdapter);
        mListResearchDetail.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                LogUtil.d("mListResearchDetail.isHeaderCollapsed(headerId)=" + mListResearchDetail.isHeaderCollapsed(headerId));
                if (mListResearchDetail.isHeaderCollapsed(headerId)) {
                    mListResearchDetail.expand(headerId);
                } else {
                    mListResearchDetail.collapse(headerId);
                }
            }
        });

        mBtnScan = (ImageButton) findViewById(R.id.research_detail_scan);
        mBtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mCtx, ScanActivity.class));
            }
        });

        mTxtTitle = (TextView) findViewById(R.id.research_detail_title);
        mTxtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mCtx)
                        .setItems(new String[]{"全部", "未完成", "已完成"},
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(ResearchDetailActivity.this, "点击了: " + which, Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < mAdapter.getCount(); i++) {
            long headerId = mAdapter.getHeaderId(i);
            mListResearchDetail.collapse(headerId);
        }
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

    private void showPopupWindow() {
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismissPopupWindow();
    }
}
