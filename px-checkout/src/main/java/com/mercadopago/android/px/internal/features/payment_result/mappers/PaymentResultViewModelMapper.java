package com.mercadopago.android.px.internal.features.payment_result.mappers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.internal.features.payment_result.viewmodel.PaymentResultLegacyViewModel;
import com.mercadopago.android.px.internal.features.payment_result.viewmodel.PaymentResultViewModel;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.internal.viewmodel.mappers.Mapper;
import com.mercadopago.android.px.model.Instruction;

public class PaymentResultViewModelMapper extends Mapper<PaymentModel, PaymentResultViewModel> {

    private final PaymentResultScreenConfiguration configuration;
    private final Instruction instruction;

    public PaymentResultViewModelMapper(@NonNull final PaymentResultScreenConfiguration configuration,
        @Nullable final Instruction instruction) {
        this.configuration = configuration;
        this.instruction = instruction;
    }

    @Override
    public PaymentResultViewModel map(@NonNull final PaymentModel model) {
        final PaymentResultLegacyViewModel legacyViewModel = new PaymentResultLegacyViewModel(
            model, configuration, instruction);
        return new PaymentResultViewModel(
            new PaymentResultHeaderModelMapper(configuration, instruction).map(model.getPaymentResult()),
            new PaymentResultBodyModelMapper(configuration).map(model), legacyViewModel);
    }
}