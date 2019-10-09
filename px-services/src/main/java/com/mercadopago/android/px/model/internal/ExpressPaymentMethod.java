package com.mercadopago.android.px.model.internal;

import com.mercadopago.android.px.model.CardMetadata;

public interface ExpressPaymentMethod {

    String getPaymentMethodId();
    String getPaymentTypeId();
    CardMetadata getCard();
    boolean isCard();
    String getCustomOptionId();
}