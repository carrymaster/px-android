package com.mercadopago.android.px.internal.util;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.Payment;

public final class StatusHelper {

    private StatusHelper() {
    }

    public static boolean isSuccess(@NonNull final String statusCode, @NonNull final String statusDetail) {
        return Payment.StatusCodes.STATUS_APPROVED.equals(statusCode) ||
            (Payment.StatusCodes.STATUS_PENDING.equals(statusCode) &&
                Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT.equals(statusDetail));
    }

    public static boolean isSuccess(@NonNull final IPaymentDescriptor payment) {
        return isSuccess(payment.getPaymentStatus(), payment.getPaymentStatusDetail());
    }
}