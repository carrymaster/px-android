package com.mercadopago.android.px.internal.features.payment_result.props;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.model.Instruction;
import com.mercadopago.android.px.model.PaymentResult;

public class PaymentResultBodyProps {

    public final Instruction instruction;
    public final String currencyId;
    public final PaymentResultScreenConfiguration configuration;
    public final PaymentResult paymentResult;

    public PaymentResultBodyProps(@NonNull final Builder builder) {
        paymentResult = builder.paymentResult;
        instruction = builder.instruction;
        currencyId = builder.currencyId;
        configuration = builder.configuration;
    }

    public static class Builder {
        public Instruction instruction;
        public String currencyId;
        public PaymentResultScreenConfiguration configuration;
        public PaymentResult paymentResult;

        public Builder(@NonNull final PaymentResultScreenConfiguration configuration) {
            this.configuration = configuration;
        }

        public Builder setInstruction(final Instruction instruction) {
            this.instruction = instruction;
            return this;
        }

        public Builder setCurrencyId(final String currencyId) {
            this.currencyId = currencyId;
            return this;
        }

        public Builder setPaymentResult(final PaymentResult paymentResult) {
            this.paymentResult = paymentResult;
            return this;
        }

        public PaymentResultBodyProps build() {
            return new PaymentResultBodyProps(this);
        }
    }
}