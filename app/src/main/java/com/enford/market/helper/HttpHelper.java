package com.enford.market.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.TypeReference;
import com.enford.market.R;
import com.enford.market.model.RespBody;
import com.enford.market.util.Consts;
import com.enford.market.util.EncryptUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/8.
 */
public final class HttpHelper implements Consts {


    public abstract static class JsonResponseHandler extends TextHttpResponseHandler {

        Context ctx;

        public JsonResponseHandler(Context ctx) {
            this.ctx = ctx;
        }

        ProgressDialog dlg;

        @Override
        public void onStart() {
            dlg = ProgressDialog.show(ctx, "", "加载中");
        }

        @Override
        public void onFinish() {
            if (dlg != null) {
                dlg.cancel();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String json, Throwable throwable) {
            try {
                RespBody resp = FastJSONHelper.deserializeAny(json, new TypeReference<RespBody>() {});
                if (resp != null) {
                    Toast.makeText(ctx, resp.getMsg(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ctx, R.string.request_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                Toast.makeText(ctx, R.string.unknown_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class ApiHelper {

        public static String createLoginUrl() {
            return API_URL_PREX + API_LOGIN;
        }

        public static RequestParams createLoginParams(String user, String pwd) {
            RequestParams params = new RequestParams();
            params.put("user", user);
            params.put("pwd", EncryptUtil.md5(pwd));
            return params;
        }

        public static String createResearchDeptUrl() {
            return API_URL_PREX + API_RESEARCH_DEPT;

        }

        public static RequestParams createResearchDeptParams(String exeId) {
            RequestParams params = new RequestParams();
            params.put("exeId", exeId);
            return params;
        }
    }

    /**
     * 登录接口
     *
     * @param ctx
     * @param user
     * @param pwd
     * @param handler
     */
    public static void login(final Context ctx, String user, String pwd, TextHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ctx,
                ApiHelper.createLoginUrl(),
                ApiHelper.createLoginParams(user, pwd),
                handler);
    }


    public static void getResearchDept(final Context ctx, String exeId, TextHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ctx,
                ApiHelper.createResearchDeptUrl(),
                ApiHelper.createResearchDeptParams(exeId),
                handler);
    }
}
