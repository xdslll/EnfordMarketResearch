package com.enford.market.util;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/8.
 */
public interface Consts {

    public static final String HTTP_PROXY_IP = "192.168.31.130";
    public static final int HTTP_PROXY_PORT = 8888;

    //public static final String API_URL_PREX = "http://192.168.1.2:8080";
    //public static final String API_URL_PREX = "http://192.168.31.130:8080";
    //public static final String API_URL_PREX = "http://192.168.43.175:8080";
    public static final String API_URL_PREX = "http://139.196.49.240/sg";
    public static final String API_LOGIN = "/api/login/";
    public static final String API_RESEARCH_DEPT = "/api/market/dept/get";
    public static final String API_ROOT_CATEGORY = "/api/category/root";
    public static final String API_GET_CATEGORY = "/api/category/get";
    public static final String API_CATEGORY_IMG = "/api/category/image";
    public static final String API_CATEGORY_COMMODITY = "/api/category/cod/get";
    public static final String API_ADD_PRICE = "/api/price/add";
    public static final String API_UPDATE_PRICE = "/api/price/update";
    public static final String API_GET_COMMODITY = "/api/cod/get";

    public static final String SUCCESS = "0";
    public static final String FAILED = "-1";

    public static final int RESEARCH_STATE_NOT_PUBLISH = 0;
    public static final int RESEARCH_STATE_HAVE_PUBLISHED = 1;
    public static final int RESEARCH_STATE_HAVE_FINISHED = 2;
    public static final int RESEARCH_STATE_CANCELED = 3;
    public static final int RESEARCH_STATE_HAVE_STARTED = 4;

    public static final int MISS_TAG_NOT_MISS = 0;
    public static final int MISS_TAG_MISS = 1;

    public static final int HTTP_TIMEOUT = 60 * 1000;

    public static final int PRICE_ADD_TAG = 0;
    public static final int PRICE_UPDATE_TAG = 1;

}
