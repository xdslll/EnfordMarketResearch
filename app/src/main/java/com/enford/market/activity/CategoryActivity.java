package com.enford.market.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.TypeReference;
import com.enford.market.R;
import com.enford.market.activity.scan.ScanCaptureActivity;
import com.enford.market.adapter.CategoryAdapter;
import com.enford.market.helper.FastJSONHelper;
import com.enford.market.helper.HttpHelper;
import com.enford.market.model.EnfordApiMarketResearch;
import com.enford.market.model.EnfordProductCategory;
import com.enford.market.model.RespBody;
import com.enford.market.util.LogUtil;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/9.
 */
public class CategoryActivity extends BaseUserActivity {

    TextView mTxtTitle;
    ImageButton mBtnScan;
    GridView mGridCategory;
    CategoryAdapter mAdapter;

    EnfordApiMarketResearch mResearch;
    List<EnfordProductCategory> mCategoryList;

    CategoryHandler mHandler = new CategoryHandler(this);
    private static final int REQUEST_ROOT_CATEGORY = 0x01;

    private static class CategoryHandler extends MyHandler {

        public CategoryHandler(Activity activity) {
            super(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            CategoryActivity activity = (CategoryActivity) getActivityWeakReference().get();
            switch (msg.what) {
                case REQUEST_ROOT_CATEGORY:
                    activity.handleRootCategory(msg);
                    break;
            }
        }
    }

    private void handleRootCategory(Message msg) {
        String json = (String) msg.obj;
        RespBody<EnfordProductCategory> resp =
                FastJSONHelper.deserializeAny(json,
                        new TypeReference<RespBody<EnfordProductCategory>>() {});
        if (resp != null) {
            if (resp.getCode().equals(SUCCESS)) {
                //获取分类数据
                mCategoryList = resp.getDatas();
                //获取分类数量
                int count = resp.getTotalnum();
                mTxtTitle.setText(R.string.category_title);
                mTxtTitle.append("(" + count + ")");
                if (mAdapter == null) {
                    //填充GridView数据
                    mAdapter = new CategoryAdapter(mCtx, mCategoryList);
                    mGridCategory.setAdapter(mAdapter);
                } else {
                    mAdapter.updateData(mCategoryList);
                    mAdapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(mCtx, resp.getMsg(), Toast.LENGTH_SHORT).show();
                LogUtil.d(resp.getMsg());
            }
        } else {
            Toast.makeText(mCtx, R.string.unknown_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);

        mResearch = getIntent().getParcelableExtra("research");

        mGridCategory = (GridView) findViewById(R.id.category_list_grid);
        mTxtTitle = (TextView) findViewById(R.id.title);
        mBtnScan = (ImageButton) findViewById(R.id.research_detail_scan);

        initBackButton();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getRootCategory();
    }

    private void initListener() {
        mGridCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mCtx, ResearchDetailActivity.class);
                intent.putExtra("user", mUser);
                intent.putExtra("research", mResearch);
                intent.putExtra("category", mCategoryList.get(position));
                startActivity(intent);
            }
        });

        mBtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, ScanCaptureActivity.class);
                intent.putExtra("user", mUser);
                intent.putExtra("research", mResearch);
                startActivity(intent);
            }
        });
    }

    /**
     * 获取分类数据
     */
    private void getRootCategory() {
        HttpHelper.getRootCategory(mCtx,
                String.valueOf(mResearch.getResearch().getId()),
                String.valueOf(mResearch.getDept().getId()),
                new HttpHelper.JsonResponseHandler(mCtx) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Message msg = Message.obtain(mHandler, REQUEST_ROOT_CATEGORY, responseString);
                        msg.sendToTarget();
                    }
                });
    }
}
