package com.kalaconnect;

import android.app.Application;
import android.content.Context;

import com.kalaconnect.utils.LocaleHelper;

public class KalaConnectApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocaleHelper.onAttach(this);
    }
}
