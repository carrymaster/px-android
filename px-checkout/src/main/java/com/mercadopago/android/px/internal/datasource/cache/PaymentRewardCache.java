package com.mercadopago.android.px.internal.datasource.cache;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.model.PaymentMethodSearch;

public interface PaymentRewardCache {
    @NonNull
    MPCall<PaymentMethodSearch> get();

    void put(@NonNull final PaymentMethodSearch groups);

    void evict();

    boolean isCached();
}