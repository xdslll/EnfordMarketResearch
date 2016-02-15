package com.enford.market.activity.scan;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.TypeReference;
import com.enford.market.R;
import com.enford.market.activity.BaseUserActivity;
import com.enford.market.activity.PricePopupWindowHandler;
import com.enford.market.adapter.ResearchDetailAdapter;
import com.enford.market.helper.FastJSONHelper;
import com.enford.market.helper.HttpHelper;
import com.enford.market.model.EnfordApiMarketResearch;
import com.enford.market.model.EnfordProductCategory;
import com.enford.market.model.EnfordProductCommodity;
import com.enford.market.model.RespBody;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ScanResultActivity extends BaseUserActivity {

	private EnfordApiMarketResearch mResearch;
	private List<EnfordProductCommodity> mCommodity;
	private String mBarcode;

	private PullToRefreshListView mList;
	private ResearchDetailAdapter mAdapter = null;
	private PricePopupWindowHandler mPricePopupHandler;

	public static final int SCAN_COMMODITY_SUCCESS = 0x01;
	public static final int REQUEST_ADD_PRICE = 0x02;
	public static final int REQUEST_UPDATE_PRICE = 0x03;

	int mPage = 1;
	int mPageSize = 10;

	List<EnfordProductCategory> mCatList = new ArrayList<EnfordProductCategory>();

	private ScanResultHandler mHandler = new ScanResultHandler(this);

	private static class ScanResultHandler extends MyHandler {

		public ScanResultHandler(Activity activity) {
			super(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			ScanResultActivity activity = (ScanResultActivity) getActivityWeakReference().get();
			switch (msg.what) {
				case SCAN_COMMODITY_SUCCESS:
					activity.handleGetCommodity(msg);
					break;
				case REQUEST_ADD_PRICE:
					activity.handlePriceResponse(msg);
					break;
				case REQUEST_UPDATE_PRICE:
					activity.handlePriceResponse(msg);
					break;
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_result);

		Bundle extras = getIntent().getExtras();

		mBarcode = extras.getString("result");
		mResearch = extras.getParcelable("research");

		initTitleBar(String.format(getString(R.string.barcode), mBarcode));

		mList = (PullToRefreshListView) findViewById(R.id.scan_result_list);
		mList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
		mList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				mPage++;
				requestGetCommodity(mBarcode);
			}
		});

		View view = getLayoutInflater().inflate(R.layout.common_empty_view, null);
		mList.setEmptyView(view);

		mPricePopupHandler = new PricePopupWindowHandler(mCtx, mHandler);
	}

	@Override
	protected void onResume() {
		super.onResume();

		requestGetCommodity(mBarcode);
	}

	private void requestGetCommodity(String barcode) {
		int resId = mResearch.getResearch().getId();
		int deptId = mResearch.getDept().getId();
		HttpHelper.getCommodityByBarcode(mCtx,
				resId, deptId, barcode, mPage, mPageSize,
				new HttpHelper.JsonResponseHandler(mCtx) {
					@Override
					public void onSuccess(int statusCode, Header[] headers, String responseString) {
						Message msg = Message.obtain(mHandler, SCAN_COMMODITY_SUCCESS, responseString);
						msg.sendToTarget();
					}
				});
	}

	private void handleGetCommodity(Message msg) {
		String json = (String) msg.obj;
		RespBody<EnfordProductCommodity> resp =
				FastJSONHelper.deserializeAny(json,
						new TypeReference<RespBody<EnfordProductCommodity>>() {
						});
		if (resp != null) {
			if (resp.getCode().equals(SUCCESS)) {
				if (mAdapter == null) {
					mCommodity = resp.getDatas();
					appenpTitleText(String.format(getString(R.string.count), resp.getTotalnum()));
					initAdapter();
				} else {
					mCommodity.addAll(resp.getDatas());
					mAdapter.updateData(mCommodity);
					mAdapter.notifyDataSetChanged();
					mList.onRefreshComplete();
				}

			} else {
				Toast.makeText(mCtx, resp.getMsg(), Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(mCtx, R.string.unknown_error, Toast.LENGTH_SHORT).show();
		}
	}

	private void initAdapter() {
		EnfordProductCategory cat = new EnfordProductCategory();
		cat.setName("");
		cat.setCommodityList(mCommodity);
		mCatList.add(cat);
		mAdapter = new ResearchDetailAdapter(mCtx, mCatList);
		mAdapter.setAddPriceListener(new ResearchDetailAdapter.OnAddPriceListener() {

			@Override
			public void onAddPriceListener(int position, EnfordProductCommodity cod) {
				mPricePopupHandler.setPosition(position);
				mPricePopupHandler.setResearchData(mResearch);
				mPricePopupHandler.setCommodityData(cod);
				mPricePopupHandler.setUser(mUser);
				mPricePopupHandler.showPopupWindow(mList);
			}
		});
		mList.setAdapter(mAdapter);
	}

	private void handlePriceResponse(Message msg) {
		String json = (String) msg.obj;
		RespBody resp = FastJSONHelper.deserializeAny(
				json, new TypeReference<RespBody>() {
				});
		if (resp != null) {
			if (resp.getCode().equals(SUCCESS)) {
				mPricePopupHandler.dismissPopupWindow();
				Toast.makeText(mCtx, resp.getMsg(), Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(mCtx, resp.getMsg(), Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(mCtx, R.string.unknown_error, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mPricePopupHandler.dismissPopupWindow();
	}
}
