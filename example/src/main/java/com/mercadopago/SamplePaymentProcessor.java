package com.mercadopago;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.IPayment;
import com.mercadopago.android.px.model.internal.IParcelablePaymentDescriptor;
import com.mercadopago.android.px.preferences.CheckoutPreference;

public class SamplePaymentProcessor extends SamplePaymentProcessorNoView {

    public static final Creator<SamplePaymentProcessor> CREATOR = new Creator<SamplePaymentProcessor>() {
        @Override
        public SamplePaymentProcessor createFromParcel(final Parcel in) {
            return new SamplePaymentProcessor(in);
        }

        @Override
        public SamplePaymentProcessor[] newArray(final int size) {
            return new SamplePaymentProcessor[size];
        }
    };

    public SamplePaymentProcessor(final IPayment payment) {
        super(payment);
    }

    public SamplePaymentProcessor(final IParcelablePaymentDescriptor payment) {
        super(payment);
    }

    public SamplePaymentProcessor(final BusinessPayment businessPayment) {
        super(businessPayment);
    }

    /* default */ SamplePaymentProcessor(final Parcel in) {
        super(in);
    }

    @Override
    public void startPayment(@NonNull final Context context, @NonNull final CheckoutData data,
        @NonNull final OnPaymentListener paymentListener) {
        throw new IllegalStateException("This will never be called because shouldShowFragmentOnPayment is hardcoded");
    }

    @Override
    public boolean shouldShowFragmentOnPayment(@NonNull final CheckoutPreference checkoutPreference) {
        return true;
    }

    @Nullable
    @Override
    public Fragment getFragment(@NonNull final CheckoutData data, @NonNull final Context context) {
        return SamplePaymentProcessorFragment.with(payment != null ? payment : businessPayment);
    }
}