package com.enford.market.util;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 16/2/8.
 */
public interface Consts {

    public static final String API_URL_PREX = "http://192.168.0.101:8080";
    //public static final String API_URL_PREX = "http://192.168.43.175:8080";
    public static final String API_LOGIN = "/api/login/";
    public static final String API_RESEARCH_DEPT = "/api/market/dept/get";

    public static final String SUCCESS = "0";
    public static final String FAILED = "-1";

    public static final int RESEARCH_STATE_NOT_PUBLISH = 0;
    public static final int RESEARCH_STATE_HAVE_PUBLISHED = 1;
    public static final int RESEARCH_STATE_HAVE_FINISHED = 2;
    public static final int RESEARCH_STATE_CANCELED = 3;
    public static final int RESEARCH_STATE_HAVE_STARTED = 4;
}
