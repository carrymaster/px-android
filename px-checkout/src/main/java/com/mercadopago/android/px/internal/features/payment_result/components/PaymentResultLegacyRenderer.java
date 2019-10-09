package com.mercadopago.android.px.internal.features.payment_result.components;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.PaymentResultViewModelFactory;
import com.mercadopago.android.px.internal.features.payment_result.props.PaymentResultBodyProps;
import com.mercadopago.android.px.internal.features.payment_result.viewmodel.PaymentResultLegacyViewModel;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.viewmodel.PaymentResultViewModel;
import com.mercadopago.android.px.model.PaymentResult;

public final class PaymentResultLegacyRenderer {

    private PaymentResultLegacyRenderer() {
    }

    public static void render(@NonNull final ViewGroup parent, @NonNull final ActionDispatcher callback,
        @NonNull final PaymentResultLegacyViewModel viewModel) {
        if (hasBodyComponent(viewModel.model.getPaymentResult())) {
            getBodyComponent(viewModel, callback).render(parent);
        } else {
            parent.findViewById(R.id.body).setVisibility(View.GONE);
        }

        parent.addView(new FooterPaymentResult(viewModel.model.getPaymentResult(), callback).render(parent));
    }

    private static boolean hasBodyComponent(@NonNull final PaymentResult paymentResult) {
        final PaymentResultViewModel paymentResultViewModel =
            PaymentResultViewModelFactory.createPaymentResultViewModel(paymentResult);
        return paymentResultViewModel.isApprovedSuccess() || paymentResultViewModel.hasBodyError();
    }

    private static Body getBodyComponent(@NonNull final PaymentResultLegacyViewModel viewModel,
        @NonNull final ActionDispatcher callback) {
        final PaymentResultBodyProps bodyProps =
            new PaymentResultBodyProps.Builder(viewModel.configuration)
                .setPaymentResult(viewModel.model.getPaymentResult())
                .setInstruction(viewModel.instruction)
                .setCurrencyId(viewModel.model.getCurrencyId())
                .build();
        return new Body(bodyProps, callback);
    }
}