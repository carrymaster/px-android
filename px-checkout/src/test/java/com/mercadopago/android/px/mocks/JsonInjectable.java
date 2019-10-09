package com.mercadopago.android.px.mocks;

import android.support.annotation.NonNull;

interface JsonInjectable<T> extends JsonStub<T> {
    @NonNull
    default String inject(@NonNull final String jsonContainer) {
        return String.format(jsonContainer, getJson());
    }
}