package com.mercadopago.android.px.internal.datasource.cache;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.callbacks.MPCall;

public interface Cache<T> {
    @NonNull
    MPCall<T> get();

    void put(@NonNull final T groups);

    void evict();

    boolean isCached();
}