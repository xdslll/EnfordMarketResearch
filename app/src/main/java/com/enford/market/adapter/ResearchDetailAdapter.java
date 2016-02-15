package com.enford.market.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.enford.market.R;
import com.enford.market.model.EnfordProductCategory;
import com.enford.market.model.EnfordProductCommodity;
import com.enford.market.model.EnfordProductPrice;
import com.enford.market.util.Consts;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class ResearchDetailAdapter extends BaseAdapter implements
        StickyListHeadersAdapter, Consts {

    private final Context mCtx;
    //private String[] mCountries;
    //private int[] mSectionIndices;
    //private Character[] mSectionLetters;
    private LayoutInflater mInflater;
    private OnAddPriceListener mAddPriceListener;
    private List<EnfordProductCategory> mCategoryList;
    private List<EnfordProductCommodity> mCommodityList;

    public void updateData(int position, EnfordProductPrice price) {
        mCommodityList.get(position).setPrice(price);
    }

    public void updateData(List<EnfordProductCommodity> commodityList) {
        this.mCommodityList = commodityList;
    }

    public void updateDatas(List<EnfordProductCategory> categoryList) {
        this.mCategoryList = categoryList;
    }

    public void showAll() {
        parseAll();
        notifyDataSetChanged();
    }

    public void showIncomplete() {
        parseComplete(false);
        notifyDataSetChanged();
    }

    public void showComplete() {
        parseComplete(true);
        notifyDataSetChanged();
    }

    public void parseAll() {
        mCommodityList.clear();

        for (int i = 0; i < mCategoryList.size(); i++) {
            int count = 0;
            EnfordProductCategory category = mCategoryList.get(i);
            List<EnfordProductCommodity> commodityList = category.getCommodityList();
            for (int j = 0; j < commodityList.size(); j++) {
                EnfordProductCommodity commodity = commodityList.get(j);
                commodity.setCategoryCode(category.getCode());
                commodity.setCategoryName(category.getName());
                mCommodityList.add(commodity);
                count++;
            }
            category.setCodCount(count);
        }
    }

    public void parseComplete(boolean complete) {
        mCommodityList.clear();
        for (int i = 0; i < mCategoryList.size(); i++) {
            int count = 0;
            EnfordProductCategory category = mCategoryList.get(i);
            List<EnfordProductCommodity> commodityList = category.getCommodityList();
            for (int j = 0; j < commodityList.size(); j++) {
                EnfordProductCommodity commodity = commodityList.get(j);
                if (complete) {
                    if (commodity.getPrice() != null) {
                        mCommodityList.add(commodity);
                        count++;
                    }
                } else {
                    if (commodity.getPrice() == null) {
                        mCommodityList.add(commodity);
                        count++;
                    }
                }
            }
            category.setCodCount(count);
        }
    }

    public interface OnAddPriceListener {
        void onAddPriceListener(int position, EnfordProductCommodity cod);
    }

    public void setAddPriceListener(OnAddPriceListener addPriceListener) {
        this.mAddPriceListener = addPriceListener;
    }

    public ResearchDetailAdapter(Context context, List<EnfordProductCategory> categoryList) {
        mCtx = context;
        mInflater = LayoutInflater.from(context);
        mCategoryList = categoryList;
        mCommodityList = new ArrayList<EnfordProductCommodity>();
        parseAll();
    }

    @Override
    public int getCount() {
        return mCommodityList.size();
    }

    @Override
    public EnfordProductCommodity getItem(int position) {
        return mCommodityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.research_detail_list_item, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.research_detail_list_item_name);
            holder.size = (TextView) convertView.findViewById(R.id.research_detail_list_item_size);
            holder.barcode = (TextView) convertView.findViewById(R.id.research_detail_list_item_barcode);
            holder.orgPrice = (TextView) convertView.findViewById(R.id.research_detail_list_item_origin_price);
            holder.proPrice = (TextView) convertView.findViewById(R.id.research_detail_list_item_prompt_price);
            holder.addPrice = (Button) convertView.findViewById(R.id.research_detail_list_item_add_price);
            holder.editPrice = (Button) convertView.findViewById(R.id.research_detail_list_item_edit_price);
            holder.addPriceLayout = (LinearLayout) convertView.findViewById(R.id.research_detail_list_item_add_price_layout);
            holder.editPriceLayout = (LinearLayout) convertView.findViewById(R.id.research_detail_list_item_edit_price_layout);
            holder.missTag = (TextView) convertView.findViewById(R.id.research_detail_list_item_miss_tag);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        EnfordProductCommodity commodity = getItem(position);
        EnfordProductPrice price = commodity.getPrice();
        holder.name.setText(commodity.getpName());
        holder.size.setText(String.format(
                mCtx.getString(R.string.size),
                commodity.getpSize(),
                commodity.getUnit()));
        holder.barcode.setText(String.format(
                mCtx.getString(R.string.barcode),
                commodity.getBarCode()));
        if (price != null) {
            if (price.getRetailPrice() == null
                    || price.getRetailPrice() == 0) {
                holder.orgPrice.setVisibility(View.GONE);
            } else {
                holder.orgPrice.setVisibility(View.VISIBLE);
                holder.orgPrice.setText(String.format(
                        mCtx.getString(R.string.origin_price),
                        String.valueOf(price.getRetailPrice() == null ?
                                "0.0" : price.getRetailPrice())));
            }
            if (price.getPromptPrice() == null
                    || price.getPromptPrice() == 0) {
                holder.proPrice.setVisibility(View.GONE);
            } else {
                holder.proPrice.setVisibility(View.VISIBLE);
                holder.proPrice.setText(String.format(
                        mCtx.getString(R.string.prompt_price),
                        String.valueOf(price.getPromptPrice() == null ?
                                "0.0" : price.getPromptPrice())));
            }
            if (price.getMiss() == MISS_TAG_MISS) {
                holder.missTag.setVisibility(View.VISIBLE);
                holder.missTag.setText(R.string.add_price_miss_text);
            } else {
                holder.missTag.setVisibility(View.GONE);
            }
            holder.editPriceLayout.setVisibility(View.VISIBLE);
            holder.addPriceLayout.setVisibility(View.GONE);
        } else {
            holder.editPriceLayout.setVisibility(View.GONE);
            holder.addPriceLayout.setVisibility(View.VISIBLE);
        }
        holder.editPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAddPriceListener != null) {
                    mAddPriceListener.onAddPriceListener(position, getItem(position));
                }
            }
        });
        holder.addPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAddPriceListener != null) {
                    mAddPriceListener.onAddPriceListener(position, getItem(position));
                }
            }
        });

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.research_detail_list_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set research_detail_list_header text as first char in name
        //CharSequence headerChar = mCountries[position].subSequence(0, 1);
        holder.text.setText(getItem(position).getCategoryName());
        int code = getItem(position).getCategoryCode();
        for (int i = 0; i < mCategoryList.size(); i++) {
            if (mCategoryList.get(i).getCode() == code) {
                holder.text.append(String.format(
                        mCtx.getString(R.string.count),
                        mCategoryList.get(i).getCodCount()));
                break;
            }
        }

        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return
     * the same Id that is.
     */
    @Override
    public long getHeaderId(int position) {
        return getItem(position).getCategoryCode();
    }

    public void clear() {
        /*mCountries = new String[0];
        mSectionIndices = new int[0];
        mSectionLetters = new Character[0];*/
        notifyDataSetChanged();
    }

    public void restore() {
        /*mCountries = mCtx.getResources().getStringArray(R.array.countries);
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();*/
        notifyDataSetChanged();
    }

    class HeaderViewHolder {
        TextView text;
    }

    class ViewHolder {
        TextView name, size, barcode, orgPrice, proPrice, missTag;
        Button addPrice, editPrice;
        LinearLayout addPriceLayout, editPriceLayout;
    }

}
