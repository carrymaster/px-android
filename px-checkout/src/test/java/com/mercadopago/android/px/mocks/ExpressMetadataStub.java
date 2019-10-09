package com.mercadopago.android.px.mocks;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.ExpressMetadata;

public enum ExpressMetadataStub implements JsonInjectable<ExpressMetadata> {
    DEFAULT;

    @NonNull
    @Override
    public ExpressMetadata get() {
        return null;
    }

    @NonNull
    @Override
    public String getJson() {
        return "";
    }
}