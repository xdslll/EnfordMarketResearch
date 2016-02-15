package com.enford.market.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.enford.market.R;
import com.enford.market.helper.HttpHelper;
import com.enford.market.helper.ImageHelper;
import com.enford.market.model.EnfordProductCategory;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {

    List<EnfordProductCategory> mCategoryList;
    Context mCtx;

    public CategoryAdapter(Context ctx, List<EnfordProductCategory> categoryList) {
        this.mCtx = ctx;
        this.mCategoryList = categoryList;
    }

    @Override
    public int getCount() {
        return mCategoryList.size();
    }

    @Override
    public EnfordProductCategory getItem(int position) {
        return mCategoryList.get(position);
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
            LayoutInflater inflater = LayoutInflater.from(mCtx);
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
        //holder.categoryName.append(String.format(mCtx.getString(R.string.count), category.getCodCount() == null ? 0 : category.getCodCount()));
        holder.categoryCode.setText(String.format(mCtx.getString(R.string.category_code), category.getCode()));
        holder.categoryFinishPercent.setText(String.format(mCtx.getString(R.string.have_finished),
                category.getHaveFinished() == null ? 0 : category.getHaveFinished()));
        holder.categoryFinishPercent.append(String.format(mCtx.getString(R.string.finish_percent),
                category.getFinishCount(), category.getCodCount()));
        String imgUrl = HttpHelper.getCategoryImageUrl(category.getCode());
        ImageHelper.getInstance(mCtx).loadImage(imgUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.categoryIcon.setImageBitmap(loadedImage);
            }
        });
        return convertView;
    }

    public void updateData(List<EnfordProductCategory> categoryList) {
        this.mCategoryList = categoryList;
    }
}

class CategoryViewHolder {
    public ImageView categoryIcon;
    public TextView categoryName, categoryCode, categoryFinishPercent;
}
