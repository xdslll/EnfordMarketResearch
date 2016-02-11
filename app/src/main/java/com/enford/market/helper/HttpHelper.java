package com.enford.market.helper;

import android.app.ProgressDialog;
import android.content.Context;

import com.enford.market.util.Consts;
import com.enford.market.util.EncryptUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

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
    }

    public static void login(final Context ctx, String user, String pwd, TextHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ctx,
                ApiHelper.createLoginUrl(),
                ApiHelper.createLoginParams(user, pwd),
                handler);
    }
}
