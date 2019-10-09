package com.mercadopago.android.px.mocks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentType;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.utils.ResourcesUtil;
import java.util.ArrayList;
import java.util.List;

public enum PaymentMethodStub {
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

    public PaymentMethod get() {
        final String json = ResourcesUtil.getStringResource(fileName);
        return JsonUtil.getInstance().fromJson(json, PaymentMethod.class);
    }

    /**
     * Returns a list of paymentMethods based on given payment type. If payment type is null, returns all available
     * payment methods.
     *
     * @param paymentType payment type to select
     */
    public static List<PaymentMethod> getAll(@Nullable final PaymentType paymentType) {
        final List<PaymentMethod> paymentMethods = new ArrayList<>();
        for (final PaymentMethodStub stub : values()) {
            if (paymentType == null || stub.paymentTypeId.equals(paymentType.getId())) {
                paymentMethods.add(stub.get());
            }
        }
        return paymentMethods;
    }
}
