package com.mercadopago.android.px.internal.datasource.cache;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.internal.PaymentReward;
import com.mercadopago.android.px.services.Callback;

public class PaymentRewardMemCache implements Cache<PaymentReward> {

    private PaymentReward paymentReward;

    @NonNull
    @Override
    public MPCall<PaymentReward> get() {
        return new MPCall<PaymentReward>() {
            @Override
            public void enqueue(final Callback<PaymentReward> callback) {
                resolve(callback);
            }

            @Override
            public void execute(final Callback<PaymentReward> callback) {
                resolve(callback);
            }
        };
    }

    /* default */ void resolve(final Callback<PaymentReward> callback) {
        if (isCached()) {
            callback.success(paymentReward);
        } else {
            callback.failure(new ApiException());
        }
    }

    @Override
    public void put(@NonNull final PaymentReward paymentReward) {
        this.paymentReward = paymentReward;
    }

    @Override
    public void evict() {
        paymentReward = null;
    }

    @Override
    public boolean isCached() {
        return paymentReward != null;
    }
}