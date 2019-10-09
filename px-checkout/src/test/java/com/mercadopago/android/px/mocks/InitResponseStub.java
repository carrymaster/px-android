package com.mercadopago.android.px.mocks;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.internal.InitResponse;
import com.mercadopago.android.px.utils.ResourcesUtil;
import java.util.Collection;

public enum InitResponseStub {
    FULL(ResponseSiteStub.MLA);

    @NonNull private final Injectable[] injectableStubs;

    InitResponseStub(@NonNull final Injectable... injectableStubs) {
        this.injectableStubs = injectableStubs;
    }

    public InitResponse get() {
        String json = ResourcesUtil.getStringResource("init_response_template.json");
        for (final Injectable stub : injectableStubs) {
            json = stub.inject(json);
        }
        final InitResponse initResponse = JsonUtil.getInstance().fromJson(json, InitResponse.class);
        populatePaymentMethods(initResponse.getPaymentMethods());
        return initResponse;
    }

    private void populatePaymentMethods(@NonNull final Collection<PaymentMethod> paymentMethods) {
        paymentMethods.addAll(PaymentMethodStub.getAll(null));
    }
}