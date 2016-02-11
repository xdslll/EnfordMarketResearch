package com.enford.market.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.enford.market.R;
import com.enford.market.view.HorizontalListView;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/8.
 */
public class ResearchListActivity extends BaseActivity {

    HorizontalListView mListSelectCommity;
    Button mBtnStartResearch;

    int mTabPosition = 0;

    ResearchTabAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.research_list);

        mListSelectCommity = (HorizontalListView) findViewById(R.id.research_list_select_commity);
        mAdapter = new ResearchTabAdapter(mCtx);
        mListSelectCommity.setAdapter(mAdapter);
        mListSelectCommity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTabPosition = position;
                mAdapter.notifyDataSetChanged();
            }
        });

        mBtnStartResearch = (Button) findViewById(R.id.research_list_start);
        mBtnStartResearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, CategoryActivity.class);
                startActivity(intent);
            }
        });
    }

    public class ResearchTabAdapter extends BaseAdapter {

        final String[] titles = {"察哈尔路社区店", "萨家湾社区店", "滨江新城社区店", "迈皋桥社区店", "晓庄社区店", "和燕路社区店"};
        Context ctx;

        public ResearchTabAdapter(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public String getItem(int position) {
            return titles[position];
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
            holder.txt.setText(titles[position]);
            if (position == mTabPosition) {
                holder.border.setVisibility(View.VISIBLE);
                holder.txt.setTextColor(getResources().getColor(R.color.main_green_color));
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
