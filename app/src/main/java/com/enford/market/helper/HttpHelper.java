package com.enford.market.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.alibaba.fastjson.TypeReference;
import com.enford.market.R;
import com.enford.market.model.EnfordProductPrice;
import com.enford.market.model.RespBody;
import com.enford.market.util.Consts;
import com.enford.market.util.EncryptUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/8.
 */
public final class HttpHelper implements Consts {

    /**
     * URL编码
     *
     * @param s
     * @return
     */
    public static Bundle decodeUrl(String s) {
        Bundle params = new Bundle();
        if (s != null) {
            String array[] = s.split("&");
            for (String parameter : array) {
                String v[] = parameter.split("=");
                try {
                    params.putString(URLDecoder.decode(v[0], "UTF-8"),
                            URLDecoder.decode(v[1], "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();

                }
            }
        }
        return params;
    }

    /**
     * URL解码
     *
     * @param param
     * @return
     */
    public static String encodeUrl(Map<String, String> param) {
        if (param == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Set<String> keys = param.keySet();
        boolean first = true;

        for (String key : keys) {
            String value = param.get(key);
            //pain...EditMyProfileDao params' values can be empty
            if (value != null || key.equals("description") || key.equals("url")) {
                if (first) {
                    first = false;
                } else {
                    sb.append("&");
                }
                try {
                    sb.append(URLEncoder.encode(key, "UTF-8")).append("=")
                            .append(URLEncoder.encode(param.get(key), "UTF-8"));
                } catch (UnsupportedEncodingException e) {

                }
            }
        }

        return sb.toString();
    }
    
    public static AsyncHttpClient createHttpClient() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(HTTP_TIMEOUT);
        //client.setProxy(HTTP_PROXY_IP, HTTP_PROXY_PORT);
        return client;
    }
    
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
                ex.printStackTrace();
                Toast.makeText(ctx, R.string.unknown_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class ApiHelper {

        /**
         * 登录
         *
         * @return
         */
        public static String createLoginUrl() {
            return API_URL_PREX + API_LOGIN;
        }

        public static RequestParams createLoginParams(String user, String pwd) {
            RequestParams params = new RequestParams();
            params.put("user", user);
            params.put("pwd", EncryptUtil.md5(pwd));
            return params;
        }

        /**
         * 获取市调清单
         *
         * @return
         */
        public static String createResearchDeptUrl() {
            return API_URL_PREX + API_RESEARCH_DEPT;

        }

        public static RequestParams createResearchDeptParams(String exeId) {
            RequestParams params = new RequestParams();
            params.put("exeId", exeId);
            return params;
        }

        /**
         * 获取大分类
         *
         * @return
         */
        public static String createRootCategoryUrl() {
            return API_URL_PREX + API_ROOT_CATEGORY;
        }

        public static RequestParams createRootCategoryParams(String resId, String deptId) {
            RequestParams params = new RequestParams();
            params.put("resId", resId);
            params.put("deptId", deptId);
            return params;
        }

        /**
         * 获取子分类
         * @return
         */
        public static String createGetCategoryUrl() {
            return API_URL_PREX + API_GET_CATEGORY;
        }

        public static RequestParams createGetCategoryParams(String code) {
            RequestParams params = new RequestParams();
            params.put("code", code);
            return params;
        }

        /**
         * 获取分类图标
         *
         * @return
         */
        public static String createCategoryImgUrl() {
            return API_URL_PREX + API_CATEGORY_IMG;
        }

        public static RequestParams createCategoryImgParams(int code) {
            RequestParams params = new RequestParams();
            params.put("code", code);
            return params;
        }

        /**
         * 获取分类下商品和价格信息
         *
         * @return
         */
        public static String createCategoryCommodityUrl() {
            return API_URL_PREX + API_CATEGORY_COMMODITY;
        }

        public static RequestParams createCategoryCommodityParam(String resId, String deptId, String code) {
            RequestParams params = new RequestParams();
            params.put("resId", resId);
            params.put("deptId", deptId);
            params.put("code", code);
            return params;
        }

        /**
         * 新增商品价格
         *
         * @return
         */
        public static String createAddPriceUrl() {
            return API_URL_PREX + API_ADD_PRICE;
        }

        public static RequestParams createAddPriceParam(EnfordProductPrice price) {
            RequestParams params = new RequestParams();
            String priceJson = FastJSONHelper.serialize(price);
            params.put("json", priceJson);
            return params;
        }

        /**
         * 修改商品价格
         *
         * @return
         */
        public static String createUpdatePriceUrl() {
            return API_URL_PREX + API_UPDATE_PRICE;
        }

        public static RequestParams createUpdatePriceParam(EnfordProductPrice price) {
            RequestParams params = new RequestParams();
            String priceJson = FastJSONHelper.serialize(price);
            params.put("json", priceJson);
            return params;
        }

        /**
         * 根据条形码获取商品信息
         *
         * @return
         */
        public static String createGetCommodityUrl() {
            return API_URL_PREX + API_GET_COMMODITY;
        }

        public static RequestParams createGetCommodityParam(int resId, int deptId, String barcode) {
            RequestParams params = new RequestParams();
            params.put("resId", resId);
            params.put("deptId", deptId);
            params.put("barcode", barcode);
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
    public static void login(Context ctx, String user, String pwd, TextHttpResponseHandler handler) {
        AsyncHttpClient client = createHttpClient();
        client.post(ctx,
                ApiHelper.createLoginUrl(),
                ApiHelper.createLoginParams(user, pwd),
                handler);
    }

    /**
     * 获取市调清单
     *
     * @param ctx
     * @param exeId
     * @param handler
     */
    public static void getResearchDept(Context ctx, String exeId, TextHttpResponseHandler handler) {
        AsyncHttpClient client = createHttpClient();
        client.get(ctx,
                ApiHelper.createResearchDeptUrl(),
                ApiHelper.createResearchDeptParams(exeId),
                handler);
    }

    /**
     * 获取所有的大分类
     *
     * @param ctx
     * @param resId
     * @param deptId
     * @param handler
     */
    public static void getRootCategory(Context ctx, String resId, String deptId, TextHttpResponseHandler handler) {
        AsyncHttpClient client = createHttpClient();
        client.get(ctx,
                ApiHelper.createRootCategoryUrl(),
                ApiHelper.createRootCategoryParams(resId, deptId),
                handler);
    }

    public static void getCategory(Context ctx, String code, TextHttpResponseHandler handler) {
        AsyncHttpClient client = createHttpClient();
        client.get(ctx,
                ApiHelper.createGetCategoryUrl(),
                ApiHelper.createGetCategoryParams(code),
                handler);
    }

    /**
     * 获取分类图片
     *
     * @param ctx
     * @param code
     * @param handler
     */
    public static void getCategoryImage(final Context ctx, int code, TextHttpResponseHandler handler) {
        AsyncHttpClient client = createHttpClient();
        client.get(ctx,
                ApiHelper.createCategoryImgUrl(),
                ApiHelper.createCategoryImgParams(code),
                handler);
    }

    /**
     * 生成获取分类图片的URL
     *
     * @param code
     * @return
     */
    public static String getCategoryImageUrl(int code) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("code", String.valueOf(code));
        String paramStr = encodeUrl(param);
        return ApiHelper.createCategoryImgUrl() + "?" + paramStr;
    }

    /**
     * 获取小分类下的所有商品和定价信息
     *
     * @param ctx
     * @param resId
     * @param deptId
     * @param code
     * @param handler
     * @return
     */
    public static void getCategoryCommodity(Context ctx, String resId,
                                                 String deptId, String code,
                                                 TextHttpResponseHandler handler) {
        AsyncHttpClient client = createHttpClient();
        client.get(ctx,
                ApiHelper.createCategoryCommodityUrl(),
                ApiHelper.createCategoryCommodityParam(resId, deptId, code),
                handler);
    }

    /**
     * 新增价格
     *
     * @param ctx
     * @param price
     * @param handler
     */
    public static void postAddPrice(Context ctx, EnfordProductPrice price,
                                            TextHttpResponseHandler handler) {
        AsyncHttpClient client = createHttpClient();
        client.post(ctx,
                ApiHelper.createAddPriceUrl(),
                ApiHelper.createAddPriceParam(price),
                handler);
    }

    /**
     * 修改价格
     *
     * @param ctx
     * @param price
     * @param handler
     */
    public static void postUpdatePrice(Context ctx, EnfordProductPrice price,
                                    TextHttpResponseHandler handler) {
        AsyncHttpClient client = createHttpClient();
        client.post(ctx,
                ApiHelper.createUpdatePriceUrl(),
                ApiHelper.createUpdatePriceParam(price),
                handler);
    }

    public static void getCommodityByBarcode(Context ctx, int resId, int deptId, String barcode,
                                             TextHttpResponseHandler handler) {
        AsyncHttpClient client = createHttpClient();
        client.get(ctx,
                ApiHelper.createGetCommodityUrl(),
                ApiHelper.createGetCommodityParam(resId, deptId, barcode),
                handler);
    }

}
