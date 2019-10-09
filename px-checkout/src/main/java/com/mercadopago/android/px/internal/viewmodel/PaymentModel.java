package com.mercadopago.android.px.internal.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.internal.PaymentReward;

public class PaymentModel implements Parcelable {

    public static final Creator<PaymentModel> CREATOR = new Creator<PaymentModel>() {
        @Override
        public PaymentModel createFromParcel(final Parcel in) {
            return new PaymentModel(in);
        }

        @Override
        public PaymentModel[] newArray(final int size) {
            return new PaymentModel[size];
        }
    };

    private final IPaymentDescriptor payment;
    private final PaymentResult paymentResult;
    private final PaymentReward paymentReward;
    private final String currencyId;

    public PaymentModel(@Nullable final IPaymentDescriptor payment, @NonNull final PaymentResult paymentResult,
        @NonNull final PaymentReward paymentReward, @NonNull final String currencyId) {
        this.payment = payment;
        this.paymentResult = paymentResult;
        this.paymentReward = paymentReward;
        this.currencyId = currencyId;
    }

    @Nullable
    public IPaymentDescriptor getPayment() {
        return payment;
    }

    @NonNull
    public PaymentResult getPaymentResult() {
        return paymentResult;
    }

    @NonNull
    public String getCurrencyId() {
        return currencyId;
    }

    @NonNull
    public PaymentReward getPaymentReward() {
        return paymentReward;
    }

    /* default */ PaymentModel(final Parcel in) {
        payment = (IPaymentDescriptor) in.readSerializable();
        paymentResult = (PaymentResult) in.readSerializable();
        paymentReward = in.readParcelable(PaymentReward.class.getClassLoader());
        currencyId = in.readString();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeSerializable(payment);
        dest.writeSerializable(paymentResult);
        dest.writeParcelable(paymentReward, flags);
        dest.writeString(currencyId);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}