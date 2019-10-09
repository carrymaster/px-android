package com.mercadopago.android.px.mocks;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.utils.ResourcesUtil;

public enum PaymentMethodStub implements JsonInjectable<PaymentMethod> {
    AMEX_CREDIT("pm_amex_credito.json", PaymentTypes.CREDIT_CARD),
    VISA_CREDIT("pm_visa_credito.json", PaymentTypes.CREDIT_CARD),
    MASTER_CREDIT("pm_master_credito.json", PaymentTypes.CREDIT_CARD),
    VISA_DEBIT("pm_visa_debito.json", PaymentTypes.DEBIT_CARD),
    MAESTRO_DEBIT("pm_maestro_debito.json", PaymentTypes.DEBIT_CARD),
    RAPIPAGO_OFF("pm_pagofacil_off.json", PaymentTypes.TICKET),
    PAGOFACIL_OFF("pm_rapipago_off.json", PaymentTypes.TICKET);

    @NonNull private final String fileName;
    @NonNull private final String paymentTypeId;

    PaymentMethodStub(@NonNull final String fileName, @NonNull final String paymentTypeId) {
        this.fileName = fileName;
        this.paymentTypeId = paymentTypeId;
    }

    @NonNull
    public PaymentMethod get() {
        return JsonUtil.getInstance().fromJson(getJson(), PaymentMethod.class);
    }

    @NonNull
    @Override
    public String getJson() {
        return ResourcesUtil.getStringResource(fileName);
    }
}