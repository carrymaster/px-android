package com.mercadopago.android.px.internal.features.payment_result.viewmodel;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.view.PaymentResultBody;
import com.mercadopago.android.px.internal.view.PaymentResultHeader;

public class PaymentResultViewModel {

    public final PaymentResultHeader.Model headerModel;
    public final PaymentResultBody.Model bodyModel;
    public final PaymentResultLegacyViewModel legacyViewModel;

    public PaymentResultViewModel(@NonNull final PaymentResultHeader.Model headerModel,
        @NonNull final PaymentResultBody.Model bodyModel,
        @NonNull final PaymentResultLegacyViewModel legacyViewModel) {
        this.headerModel = headerModel;
        this.bodyModel = bodyModel;
        this.legacyViewModel = legacyViewModel;
    }
}