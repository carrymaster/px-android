package com.mercadopago.android.px.internal.core;

import android.content.Context;
import android.net.TrafficStats;
import android.support.annotation.NonNull;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class StrictModeInterceptor implements Interceptor {

    private static final int TAG = 1904;
    @NonNull private final Context context;

    public StrictModeInterceptor(@NonNull final Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(@NonNull final Chain chain) throws IOException {
        TrafficStats.setThreadStatsTag(TAG);
        return chain.proceed(chain.request());
    }
}