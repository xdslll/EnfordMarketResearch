package com.enford.market.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.TypeReference;
import com.enford.market.R;
import com.enford.market.helper.FastJSONHelper;
import com.enford.market.helper.HttpHelper;
import com.enford.market.model.EnfordApiMarketResearch;
import com.enford.market.model.EnfordMarketResearch;
import com.enford.market.model.EnfordProductCompetitors;
import com.enford.market.model.EnfordProductDepartment;
import com.enford.market.model.RespBody;
import com.enford.market.view.HorizontalListView;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/8.
 */
public class ResearchListActivity extends BaseUserActivity {

    HorizontalListView mListSelectCommity;
    Button mBtnStartResearch;
    ImageButton mBtnSettings;
    TextView mTxtTitle, mTxtStartDate, mTxtEndDate,
            mTxtCodCount, mTxtDept, mTxtComp, mTxtHaveFinished;
    SeekBar mSkbFinishPercent;
    LinearLayout mLayoutResearchList;
    RelativeLayout mLayoutNoneResearch;

    int mTabPosition = 0;
    ResearchTabAdapter mAdapter;

    List<EnfordApiMarketResearch> mResearchDeptList = null;

    ResListHandler mHandler = new ResListHandler(this);
    private static final int REQUEST_RES_DEPT = 0x01;

    private static class ResListHandler extends MyHandler {

        public ResListHandler(Activity activity) {
            super(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ResearchListActivity activity = (ResearchListActivity) getActivityWeakReference().get();
            switch (msg.what) {
                case REQUEST_RES_DEPT:
                    activity.handleResDept(msg);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.research_list);

        mListSelectCommity = (HorizontalListView) findViewById(R.id.research_list_select_commity);
        mBtnStartResearch = (Button) findViewById(R.id.research_list_start);
        mBtnSettings = (ImageButton) findViewById(R.id.research_list_settings);
        mTxtTitle = (TextView) findViewById(R.id.title);
        mTxtStartDate = (TextView) findViewById(R.id.research_list_start_date);
        mTxtEndDate = (TextView) findViewById(R.id.research_list_end_date);
        mTxtDept = (TextView) findViewById(R.id.research_list_dept);
        mTxtComp = (TextView) findViewById(R.id.research_list_comp);
        mTxtHaveFinished = (TextView) findViewById(R.id.research_list_finish_text);
        mTxtCodCount = (TextView) findViewById(R.id.research_list_cod_number);
        mSkbFinishPercent = (SeekBar) findViewById(R.id.research_list_finish_seekbar);
        mLayoutResearchList = (LinearLayout) findViewById(R.id.research_list_layout);
        mLayoutNoneResearch = (RelativeLayout) findViewById(R.id.research_list_none_layout);

        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestResearchDept();
    }

    private void initListener() {
        mSkbFinishPercent.setEnabled(false);

        mListSelectCommity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTabPosition = position;
                mAdapter.notifyDataSetChanged();
                showResearchDetail(mResearchDeptList.get(mTabPosition));
            }
        });

        mBtnStartResearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, CategoryActivity.class);
                intent.putExtra("user", mUser);
                intent.putExtra("research", mResearchDeptList.get(mTabPosition));
                startActivity(intent);
            }
        });


        mBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, SettingsActivity.class);
                startActivity(intent);
            }
        });

        mLayoutNoneResearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestResearchDept();
            }
        });
    }


    /**
     * 请求市调清单数据
     */
    private void requestResearchDept() {
        HttpHelper.getResearchDept(mCtx,
                String.valueOf(mUser.getDeptId()),
                new HttpHelper.JsonResponseHandler(mCtx) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Message msg = Message.obtain(mHandler, REQUEST_RES_DEPT, responseString);
                        msg.sendToTarget();
                    }
                });
    }

    /**
     * 处理市调部门数据
     *
     * @param msg
     */
    private void handleResDept(Message msg) {
        String json = (String) msg.obj;
        RespBody<EnfordApiMarketResearch> resp =
                FastJSONHelper.deserializeAny(json,
                        new TypeReference<RespBody<EnfordApiMarketResearch>>(){});
        if (resp != null) {
            if (resp.getCode().equals(SUCCESS)) {
                //获取市调清单数据
                mResearchDeptList = resp.getDatas();
                //获取市调清单数量
                int count = resp.getTotalnum();
                mTxtTitle.setText(R.string.research_list_title);
                mTxtTitle.append("(" + count + ")");
                //显示第一条数据的市调信息
                if (mResearchDeptList != null && mResearchDeptList.size() > 0) {
                    EnfordApiMarketResearch apiResearch = mResearchDeptList.get(0);
                    showResearchDetail(apiResearch);
                    //显示TAB条
                    mAdapter = new ResearchTabAdapter(mCtx, mResearchDeptList);
                    mListSelectCommity.setAdapter(mAdapter);
                    mLayoutResearchList.setVisibility(View.VISIBLE);
                    mLayoutNoneResearch.setVisibility(View.GONE);
                } else {
                    //Toast.makeText(mCtx, R.string.research_list_none, Toast.LENGTH_SHORT).show();
                    mLayoutResearchList.setVisibility(View.GONE);
                    mLayoutNoneResearch.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(mCtx, resp.getMsg(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mCtx, R.string.unknown_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void showResearchDetail(EnfordApiMarketResearch apiResearch) {
        EnfordMarketResearch research = apiResearch.getResearch();
        //市调开始时间
        mTxtStartDate.setText(formatDate(research.getStartDt()));
        //市调结束时间
        mTxtEndDate.setText(formatDate(research.getEndDt()));
        //市调部门信息
        EnfordProductDepartment dept = apiResearch.getDept();
        mTxtDept.setText(dept.getName());
        //市调竞争对手信息
        EnfordProductCompetitors comp = apiResearch.getComp();
        mTxtComp.setText(comp.getName());
        //调研商品总数
        mTxtCodCount.setText(String.valueOf(apiResearch.getCodCount()));
        //完成进度
        mSkbFinishPercent.setProgress(apiResearch.getHaveFinished());
        mTxtHaveFinished.setText(
                String.format(getResources().getString(R.string.have_finished),
                        apiResearch.getHaveFinished()));
    }

    public class ResearchTabAdapter extends BaseAdapter {

        Context ctx;

        List<EnfordApiMarketResearch> list;

        public ResearchTabAdapter(Context ctx, List<EnfordApiMarketResearch> list) {
            this.ctx = ctx;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public EnfordApiMarketResearch getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ResearchTabViewHolder holder;
            if (convertView == null) {
                holder = new ResearchTabViewHolder();
                LayoutInflater inflater = LayoutInflater.from(ctx);
                convertView = inflater.inflate(R.layout.research_list_item, null);
                holder.txt = (TextView) convertView.findViewById(R.id.research_list_item_text);
                holder.border = (TextView) convertView.findViewById(R.id.research_list_item_tag);
                convertView.setTag(holder);
            } else {
                holder = (ResearchTabViewHolder) convertView.getTag();
            }
            EnfordApiMarketResearch research = getItem(position);
            holder.txt.setText(research.getResearch().getName());
            if (position == mTabPosition) {
                holder.border.setVisibility(View.VISIBLE);
                int color = getResources().getColor(R.color.main_green_color);
                switch (research.getResearch().getState()) {
                    case RESEARCH_STATE_HAVE_PUBLISHED:
                        color = getResources().getColor(R.color.orange);
                        mBtnStartResearch.setBackground(getResources().getDrawable(R.drawable.btn_bg_orange));
                        mBtnStartResearch.setTextColor(getResources().getColorStateList(R.drawable.btn_text_color_orange));
                        mBtnStartResearch.setText(getResources().getString(R.string.research_not_started));
                        mBtnStartResearch.setEnabled(false);
                        mSkbFinishPercent.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_process_drawable_orange));
                        mSkbFinishPercent.setThumb(getResources().getDrawable(R.drawable.thumb_bar_orange));
                        break;
                    case RESEARCH_STATE_HAVE_STARTED:
                        mBtnStartResearch.setBackground(getResources().getDrawable(R.drawable.btn_bg));
                        mBtnStartResearch.setTextColor(getResources().getColorStateList(R.drawable.btn_text_color));
                        mBtnStartResearch.setText(getResources().getString(R.string.start_reseach));
                        mBtnStartResearch.setEnabled(true);
                        mSkbFinishPercent.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_process_drawable_green));
                        mSkbFinishPercent.setThumb(getResources().getDrawable(R.drawable.thumb_bar_green));
                        break;
                }
                holder.txt.setTextColor(color);
                holder.border.setBackgroundColor(color);
            } else {
                holder.border.setVisibility(View.INVISIBLE);
                holder.txt.setTextColor(getResources().getColor(R.color.sub_title_color));
            }
            return convertView;
        }
    }

    public static class ResearchTabViewHolder {
        public TextView txt;
        public TextView border;
    }
}
