package com.mercadopago.android.px.mocks;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.PaymentMethodSearch;

public enum PaymentMethodSearchItemStub implements JsonInjectable<PaymentMethodSearch> {
    DEFAULT;

    @NonNull
    @Override
    public PaymentMethodSearch get() {
        return null;
    }

    @NonNull
    @Override
    public String getJson() {
        return "";
    }
}