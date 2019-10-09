package com.mercadopago.android.px.internal.repository;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.internal.PaymentReward;

public interface PaymentRewardRepository {
    void getPaymentReward(@NonNull final IPaymentDescriptor payment, @NonNull final PaymentResult paymentResult,
        @NonNull final PaymentRewardCallback callback);

    interface PaymentRewardCallback {
        void handleResult(@NonNull final IPaymentDescriptor payment, @NonNull final PaymentResult paymentResult,
            @NonNull final PaymentReward paymentReward);
    }
}