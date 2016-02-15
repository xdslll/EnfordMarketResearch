/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.enford.market.activity.scan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.enford.market.R;
import com.enford.market.activity.BaseUserActivity;
import com.enford.market.model.EnfordApiMarketResearch;
import com.enford.market.qrcode.camera.CameraManager;
import com.enford.market.qrcode.decode.BitmapDecoder;
import com.enford.market.qrcode.decode.DecodeThread;
import com.enford.market.qrcode.utils.BeepManager;
import com.enford.market.qrcode.utils.BitmapUtils;
import com.enford.market.qrcode.utils.CaptureActivityHandler;
import com.enford.market.qrcode.utils.InactivityTimer;
import com.enford.market.util.Consts;
import com.google.zxing.Result;
import com.google.zxing.client.result.ResultParser;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class ScanCaptureActivity extends BaseUserActivity
		implements SurfaceHolder.Callback, OnClickListener, Consts {

	private static final String TAG = ScanCaptureActivity.class.getSimpleName();
	
	private static final int REQUEST_CODE = 100;
	private static final int PARSE_BARCODE_FAIL = 300;
	private static final int PARSE_BARCODE_SUC = 200;
	//public static final int SCAN_COMMODITY_SUCCESS = 400;
	//public static final int REQUEST_ADD_PRICE = 500;
	//public static final int REQUEST_UPDATE_PRICE = 600;

	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private InactivityTimer inactivityTimer;
	private BeepManager beepManager;

	private SurfaceView scanPreview = null;
	private RelativeLayout scanContainer;
	private RelativeLayout scanCropView;
	private ImageView scanLine;

	private Rect mCropRect = null;
	
	private boolean isFlashlightOpen;

	private EnfordApiMarketResearch mResearch;
	/**
	 * 图片的路径
	 */
	private String photoPath;

	private Handler mHandler = new MyHandler(this);

	static class MyHandler extends Handler {

		private WeakReference<Activity> activityReference;

		public MyHandler(Activity activity) {
			activityReference = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final ScanCaptureActivity activity = ((ScanCaptureActivity) activityReference.get());
			switch (msg.what) {
				case PARSE_BARCODE_SUC: // 解析图片成功
					/*Toast.makeText(activityReference.get(),
							"解析成功，结果为：" + msg.obj, Toast.LENGTH_SHORT).show();*/
					String barcode = (String) msg.obj;
                    activity.gotoScanResult(barcode);
					//activity.requestGetCommodity(barcode, activity);
					break;
				case PARSE_BARCODE_FAIL:// 解析图片失败
					Toast.makeText(activityReference.get(), "解析图片失败",
							Toast.LENGTH_SHORT).show();
					break;
				/*case SCAN_COMMODITY_SUCCESS:
					activity.handleGetCommodity(msg);
					break;
				case REQUEST_ADD_PRICE:
					activity.handlePriceResponse(msg);
					break;
				case REQUEST_UPDATE_PRICE:
					activity.handlePriceResponse(msg);
					break;*/
				default:
					break;
			}
			super.handleMessage(msg);
		}
	}

	public Handler getHandler() {
		return handler;
	}

	/*private void requestGetCommodity(String barcode, final ScanCaptureActivity activity) {
		int resId = activity.mResearch.getResearch().getId();
		int deptId = activity.mResearch.getDept().getId();
		HttpHelper.getCommodityByBarcode(activity.mCtx,
				resId, deptId, barcode,
				new HttpHelper.JsonResponseHandler(activity.mCtx) {
					@Override
					public void onSuccess(int statusCode, Header[] headers, String responseString) {
						Message msg = Message.obtain(activity.mHandler, ScanCaptureActivity.SCAN_COMMODITY_SUCCESS, responseString);
						msg.sendToTarget();
					}
				});
	}

	private void handlePriceResponse(Message msg) {
		String json = (String) msg.obj;
		RespBody resp = FastJSONHelper.deserializeAny(
				json, new TypeReference<RespBody>() {});
		if (resp != null) {
			if (resp.getCode().equals(SUCCESS)) {
				Toast.makeText(mCtx, resp.getMsg(), Toast.LENGTH_SHORT).show();
				//mPricePopupHandler.dismissPopupWindow();
			} else {
				Toast.makeText(mCtx, resp.getMsg(), Toast.LENGTH_SHORT).show();
				LogUtil.d(resp.getMsg());
			}
		} else {
			Toast.makeText(mCtx, R.string.unknown_error, Toast.LENGTH_SHORT).show();
		}
	}

	private void handleGetCommodity(Message msg) {
		String json = (String) msg.obj;
		RespBody<EnfordProductCommodity> resp =
				FastJSONHelper.deserializeAny(json,
						new TypeReference<RespBody<EnfordProductCommodity>>() {});
		if (resp != null) {
			if (resp.getCode().equals(SUCCESS)) {
				mCommodity = resp.getData();
				mPricePopupHandler.initCommodityPopupWindow();
				mPricePopupHandler.setResearchData(mResearch);
				mPricePopupHandler.setCommodityData(mCommodity);
				mPricePopupHandler.setUser(mUser);
				mPricePopupHandler.showPopupWindow(scanPreview);
				mPricePopupHandler.showCommodityPopupWindow(scanContainer);
			} else {
				Toast.makeText(mCtx, resp.getMsg(), Toast.LENGTH_SHORT).show();
				LogUtil.d(resp.getMsg());
			}
		} else {
			Toast.makeText(mCtx, R.string.unknown_error, Toast.LENGTH_SHORT).show();
		}
	}*/

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	private boolean isHasSurface = false;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		mResearch = getIntent().getParcelableExtra("research");

		if (getActionBar() != null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.scan_capture);

		scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
		scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
		scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
		scanLine = (ImageView) findViewById(R.id.capture_scan_line);

		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);

		TranslateAnimation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.9f);
		animation.setDuration(3000);
		animation.setRepeatCount(-1);
		animation.setRepeatMode(Animation.RESTART);
		scanLine.startAnimation(animation);
		
		findViewById(R.id.capture_flashlight).setOnClickListener(this);
		findViewById(R.id.capture_scan_photo).setOnClickListener(this);

		initBackButton();

	}

	@Override
	protected void onResume() {
		super.onResume();

		// CameraManager must be initialized here, not in onCreate(). This is
		// necessary because we don't
		// want to open the camera driver and measure the screen size if we're
		// going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the
		// wrong size and partially
		// off screen.
		cameraManager = new CameraManager(getApplication());

		handler = null;

		if (isHasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(scanPreview.getHolder());
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			scanPreview.getHolder().addCallback(this);
		}

		inactivityTimer.onResume();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		beepManager.close();
		cameraManager.closeDriver();
		if (!isHasSurface) {
			scanPreview.getHolder().removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!isHasSurface) {
			isHasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isHasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	/**
	 * A valid barcode has been found, so give an indication of success and show
	 * the results.
	 * 
	 * @param rawResult
	 *            The contents of the barcode.
	 * 
	 * @param bundle
	 *            The extras
	 */
	public void handleDecode(Result rawResult, Bundle bundle) {
		inactivityTimer.onActivity();
		beepManager.playBeepSoundAndVibrate();

		/*bundle.putInt("width", mCropRect.width());
		bundle.putInt("height", mCropRect.height());
		bundle.putString("result", rawResult.getText());*/

		//startActivity(new Intent(ScanCaptureActivity.this, ScanResultActivity.class).putExtras(bundle));
		//mPricePopupHandler.showPopupWindow(scanPreview);
		//requestGetCommodity(rawResult.getText(), this);
        gotoScanResult(rawResult.getText());
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
			}

			initCrop();
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		// camera error
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage("相机打开出错，请稍后重试");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}

		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
	}

	public Rect getCropRect() {
		return mCropRect;
	}

	/**
	 * 初始化截取的矩形区域
	 */
	private void initCrop() {
		int cameraWidth = cameraManager.getCameraResolution().y;
		int cameraHeight = cameraManager.getCameraResolution().x;

		/** 获取布局中扫描框的位置信息 */
		int[] location = new int[2];
		scanCropView.getLocationInWindow(location);

		int cropLeft = location[0];
		int cropTop = location[1] - getStatusBarHeight();

		int cropWidth = scanCropView.getWidth();
		int cropHeight = scanCropView.getHeight();

		/** 获取布局容器的宽高 */
		int containerWidth = scanContainer.getWidth();
		int containerHeight = scanContainer.getHeight();

		/** 计算最终截取的矩形的左上角顶点x坐标 */
		int x = cropLeft * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的左上角顶点y坐标 */
		int y = cropTop * cameraHeight / containerHeight;

		/** 计算最终截取的矩形的宽度 */
		int width = cropWidth * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的高度 */
		int height = cropHeight * cameraHeight / containerHeight;

		/** 生成最终的截取的矩形 */
		mCropRect = new Rect(x, y, width + x, height + y);
	}

	private int getStatusBarHeight() {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			return getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (resultCode == RESULT_OK) {
			final ProgressDialog progressDialog;
			switch (requestCode) {
				case REQUEST_CODE:

					//获取图片路径
					final Uri imgUri = intent.getData();
					if (imgUri != null) {
						progressDialog = new ProgressDialog(this);
						progressDialog.setMessage("正在扫描...");
						progressDialog.setCancelable(false);
						progressDialog.show();

						new Thread(new Runnable() {

							@Override
							public void run() {

								Bitmap img = BitmapUtils
										.getCompressedBitmap(imgUri.getPath());

								BitmapDecoder decoder = new BitmapDecoder(
										ScanCaptureActivity.this);
								Result result = decoder.getRawResult(img);

								if (result != null) {
									Message m = mHandler.obtainMessage();
									m.what = PARSE_BARCODE_SUC;
									m.obj = ResultParser.parseResult(result)
											.toString();
									mHandler.sendMessage(m);
								} else {
									Message m = mHandler.obtainMessage();
									m.what = PARSE_BARCODE_FAIL;
									mHandler.sendMessage(m);
								}
								progressDialog.dismiss();
							}
						}).start();
					} else {
						Toast.makeText(ScanCaptureActivity.this, "获取图片路径失败，请重试！", Toast.LENGTH_SHORT).show();
					}

					break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.capture_scan_photo: // 图片识别
			// 打开手机中的相册
			Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
			innerIntent.setType("image/*");
			Intent wrapperIntent = Intent.createChooser(innerIntent,
					"选择二维码图片");
			this.startActivityForResult(wrapperIntent, REQUEST_CODE);
			break;

		case R.id.capture_flashlight:
			if (isFlashlightOpen) {
				cameraManager.setTorch(false); // 关闭闪光灯
				isFlashlightOpen = false;
			}
			else {
				cameraManager.setTorch(true); // 打开闪光灯
				isFlashlightOpen = true;
			}
			break;
		default:
			break;
		}
	}

	private void gotoScanResult(String result) {
        Intent intent = new Intent(mCtx, ScanResultActivity.class);
        intent.putExtra("user", mUser);
        intent.putExtra("research", mResearch);
        intent.putExtra("result", result);
        startActivity(intent);
    }

}