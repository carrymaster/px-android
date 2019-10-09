package com.mercadopago.android.px.mocks;

import android.support.annotation.NonNull;

interface JsonStub<T> {
    @NonNull
    T get();

    @NonNull
    String getJson();
}