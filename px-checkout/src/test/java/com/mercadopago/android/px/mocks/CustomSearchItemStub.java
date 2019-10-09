package com.mercadopago.android.px.mocks;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.CustomSearchItem;

public enum CustomSearchItemStub implements JsonInjectable<CustomSearchItem> {
    DEFAULT;

    @NonNull
    @Override
    public CustomSearchItem get() {
        return null;
    }

    @NonNull
    @Override
    public String getJson() {
        return "";
    }
}