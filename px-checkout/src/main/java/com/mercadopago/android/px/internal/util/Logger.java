package com.mercadopago.android.px.internal.util;

import android.support.annotation.NonNull;
import android.util.Log;
import com.mercadopago.android.px.BuildConfig;

public final class Logger {

    private Logger() {
    }

    public static void debug(@NonNull final String tag, @NonNull final String data) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, data);
        }
    }
}
