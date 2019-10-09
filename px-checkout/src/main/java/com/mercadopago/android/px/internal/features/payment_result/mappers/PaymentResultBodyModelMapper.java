package com.mercadopago.android.px.internal.features.payment_result.mappers;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.internal.features.business_result.PaymentRewardMapper;
import com.mercadopago.android.px.internal.view.PaymentResultBody;
import com.mercadopago.android.px.internal.view.PaymentResultMethod;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.internal.viewmodel.mappers.Mapper;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentResult;
import java.util.ArrayList;
import java.util.List;

public class PaymentResultBodyModelMapper extends Mapper<PaymentModel, PaymentResultBody.Model> {

    private final PaymentResultScreenConfiguration configuration;

    public PaymentResultBodyModelMapper(@NonNull final PaymentResultScreenConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public PaymentResultBody.Model map(@NonNull final PaymentModel model) {
        final PaymentResult paymentResult = model.getPaymentResult();
        final List<PaymentResultMethod.Model> methodModels = new ArrayList<>();
        for (final PaymentData paymentData : paymentResult.getPaymentDataList()) {
            methodModels.add(PaymentResultMethod.Model.with(paymentData, model.getCurrencyId()));
        }

        return new PaymentResultBody.Model.Builder()
            .setMethodModels(methodModels)
            .setRewardViewModel(new PaymentRewardMapper().map(model.getPaymentReward()))
            .setReceiptId(String.valueOf(paymentResult.getPaymentId()))
            .setTopFragment(configuration.getTopFragment())
            .setBottomFragment(configuration.getBottomFragment())
            .build();
    }
}