package com.mercadopago.android.px.mocks;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.internal.InitResponse;
import com.mercadopago.android.px.utils.ResourcesUtil;
import java.util.Arrays;

public enum InitResponseStub implements JsonStub<InitResponse> {
    FULL(ResponseSiteStub.MLA, CheckoutPreferenceStub.DEFAULT, PaymentMethodStub.values(),
        PaymentMethodSearchItemStub.values(), CustomSearchItemStub.values(), ExpressMetadataStub.values());

    @NonNull private String json = ResourcesUtil.getStringResource("init_response_template.json");

    @SuppressWarnings("TypeMayBeWeakened")
    InitResponseStub(@NonNull final ResponseSiteStub responseSiteStub,
        @NonNull final CheckoutPreferenceStub checkoutPreferenceStub,
        @NonNull final PaymentMethodStub[] paymentMethodStubs,
        @NonNull final PaymentMethodSearchItemStub[] paymentMethodSearchItemStubs,
        @NonNull final CustomSearchItemStub[] customSearchItemStubs,
        @NonNull final ExpressMetadataStub[] expressMetadataStubs) {

        json = responseSiteStub.inject(json);
        json = checkoutPreferenceStub.inject(json);
        json = ListJsonInjector.injectAll(Arrays.asList(paymentMethodStubs).iterator(), json);
        json = ListJsonInjector.injectAll(Arrays.asList(paymentMethodSearchItemStubs).iterator(), json);
        json = ListJsonInjector.injectAll(Arrays.asList(customSearchItemStubs).iterator(), json);
        json = ListJsonInjector.injectAll(Arrays.asList(expressMetadataStubs).iterator(), json);
    }

    @NonNull
    @Override
    public InitResponse get() {
        return JsonUtil.getInstance().fromJson(json, InitResponse.class);
    }

    @NonNull
    @Override
    public String getJson() {
        return json;
    }
}