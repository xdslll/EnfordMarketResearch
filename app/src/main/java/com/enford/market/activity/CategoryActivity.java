package com.enford.market.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.enford.market.R;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/9.
 */
public class CategoryActivity extends BaseActivity {

    GridView mGridCategory;
    CategoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);

        mGridCategory = (GridView) findViewById(R.id.category_list_grid);
        mAdapter = new CategoryAdapter(mCtx);
        mGridCategory.setAdapter(mAdapter);
        mGridCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mCtx, ResearchDetailActivity.class);
                startActivity(intent);
            }
        });

        initBackButton();
    }

    public class CategoryAdapter extends BaseAdapter {

        final String[] titles = {"酒水", "酒水", "酒水", "酒水", "酒水", "酒水", "酒水", "酒水", "酒水", "酒水", "酒水", "酒水", "酒水", "酒水", "酒水", "酒水", "酒水", "酒水"};
        Context ctx;

        public CategoryAdapter(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Object getItem(int position) {
            return titles[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CategoryViewHolder holder;
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
            holder.categoryName.setText(titles[position]);
            holder.categoryCode.setText("分类码: 201");
            holder.categoryFinishPercent.setText("已完成10%");
            return convertView;
        }
    }

    public static class CategoryViewHolder {
        public ImageView categoryIcon;
        public TextView categoryName, categoryCode, categoryFinishPercent;
    }
}
