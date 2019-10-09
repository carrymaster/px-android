package com.mercadopago.android.px.mocks;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.utils.ResourcesUtil;

interface Injectable {

    @NonNull
    String getJson();

    @NonNull
    default String inject(@NonNull final String jsonContainer) {
        return String.format(jsonContainer, getJson());
    }

}