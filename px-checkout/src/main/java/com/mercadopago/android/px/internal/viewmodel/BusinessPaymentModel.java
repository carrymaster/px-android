package com.mercadopago.android.px.internal.viewmodel;

import android.os.Parcel;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.internal.PaymentReward;

public class BusinessPaymentModel extends PaymentModel {

    private final BusinessPayment businessPayment;

    public BusinessPaymentModel(@NonNull final BusinessPayment businessPayment, @NonNull final PaymentResult paymentResult,
        @NonNull final PaymentReward paymentReward, @NonNull final String currencyId) {
        super(null, paymentResult, paymentReward, currencyId);
        this.businessPayment = businessPayment;
    }

    @NonNull
    @Override
    public BusinessPayment getPayment() {
        return businessPayment;
    }

    /* default */ BusinessPaymentModel(final Parcel in) {
        super(in);
        businessPayment = in.readParcelable(BusinessPayment.class.getClassLoader());
    }

    public static final Creator<BusinessPaymentModel> CREATOR = new Creator<BusinessPaymentModel>() {
        @Override
        public BusinessPaymentModel createFromParcel(final Parcel in) {
            return new BusinessPaymentModel(in);
        }

        @Override
        public BusinessPaymentModel[] newArray(final int size) {
            return new BusinessPaymentModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(businessPayment, flags);
    }
}