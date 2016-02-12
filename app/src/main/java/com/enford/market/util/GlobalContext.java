package com.enford.market.util;

import android.app.Application;

/**
 * User: Jiang Qi
 * Date: 12-7-27
 */
public final class GlobalContext extends Application {

    //singleton
    private static GlobalContext globalContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        globalContext = this;
    }

    public static GlobalContext getInstance() {
        return globalContext;
    }

}

