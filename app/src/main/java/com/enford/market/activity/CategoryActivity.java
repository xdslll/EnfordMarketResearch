package com.enford.market.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.TypeReference;
import com.enford.market.R;
import com.enford.market.activity.scan.ScanCaptureActivity;
import com.enford.market.helper.FastJSONHelper;
import com.enford.market.helper.HttpHelper;
import com.enford.market.helper.ImageHelper;
import com.enford.market.model.EnfordApiMarketResearch;
import com.enford.market.model.EnfordProductCategory;
import com.enford.market.model.RespBody;
import com.enford.market.util.LogUtil;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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
        RespBody<List<EnfordProductCategory>> resp =
                FastJSONHelper.deserializeAny(json,
                        new TypeReference<RespBody<List<EnfordProductCategory>>>() {
                        });
        if (resp != null) {
            if (resp.getCode().equals(SUCCESS)) {
                //获取分类数据
                mCategoryList = resp.getData();
                //获取分类数量
                int count = resp.getTotalnum();
                mTxtTitle.append("(" + count + ")");
                //填充GridView数据
                mAdapter = new CategoryAdapter(mCtx, mCategoryList);
                mGridCategory.setAdapter(mAdapter);
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

    public class CategoryAdapter extends BaseAdapter {

        List<EnfordProductCategory> categoryList;
        Context ctx;

        public CategoryAdapter(Context ctx, List<EnfordProductCategory> categoryList) {
            this.ctx = ctx;
            this.categoryList = categoryList;
        }

        @Override
        public int getCount() {
            return categoryList.size();
        }

        @Override
        public EnfordProductCategory getItem(int position) {
            return categoryList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final CategoryViewHolder holder;
            if (convertView == null) {
                holder = new CategoryViewHolder();
                LayoutInflater inflater = LayoutInflater.from(ctx);
                convertView = inflater.inflate(R.layout.category_list_item, null);
                holder.categoryIcon = (ImageView) convertView.findViewById(R.id.category_list_item_icon);
                holder.categoryName = (TextView) convertView.findViewById(R.id.category_list_item_name);
                holder.categoryCode = (TextView) convertView.findViewById(R.id.category_list_item_code);
                holder.categoryFinishPercent = (TextView) convertView.findViewById(R.id.category_list_item_finish_percent);
                convertView.setTag(holder);
            } else {
                holder = (CategoryViewHolder) convertView.getTag();
            }
            EnfordProductCategory category = getItem(position);
            holder.categoryName.setText(category.getName());
            holder.categoryName.append(String.format(getString(R.string.count),
                    category.getCodCount() == null ? 0 : category.getCodCount()));
            holder.categoryCode.setText(String.format(getString(R.string.category_code), category.getCode()));
            holder.categoryFinishPercent.setText(String.format(getString(R.string.have_finished),
                    category.getHaveFinished() == null ? 0 : category.getHaveFinished()));
            String imgUrl = HttpHelper.getCategoryImageUrl(category.getCode());
            ImageHelper.getInstance(ctx).loadImage(imgUrl, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.categoryIcon.setImageBitmap(loadedImage);
                }
            });
            return convertView;
        }
    }

    public static class CategoryViewHolder {
        public ImageView categoryIcon;
        public TextView categoryName, categoryCode, categoryFinishPercent;
    }
}
