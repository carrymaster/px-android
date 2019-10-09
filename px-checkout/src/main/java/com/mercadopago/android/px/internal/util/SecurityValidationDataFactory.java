package com.mercadopago.android.px.internal.util;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.addons.model.EscValidationData;
import com.mercadopago.android.px.addons.model.SecurityValidationData;
import com.mercadopago.android.px.internal.core.ProductIdProvider;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.internal.ExpressPaymentMethod;
import java.util.HashMap;

public final class SecurityValidationDataFactory {

    private SecurityValidationDataFactory() {
    }

    public static SecurityValidationData create(@NonNull final ProductIdProvider productIdProvider, @NonNull final
    PaymentSettingRepository paymentSettingRepository, @NonNull final ExpressPaymentMethod expressMetadata) {
        final String productId = productIdProvider.getProductId();
        final String customOptionId = expressMetadata.getCustomOptionId();
        final boolean isCard = expressMetadata.isCard();
        final boolean escEnable = paymentSettingRepository.getAdvancedConfiguration().isEscEnabled();
        final EscValidationData escValidationData = new EscValidationData.Builder(customOptionId, isCard, escEnable)
            .build();
        return new SecurityValidationData.Builder(productId).setEscValidationData(escValidationData).build();
    }

    public static SecurityValidationData create(@NonNull final ProductIdProvider productIdProvider, @NonNull final
    PaymentSettingRepository paymentSettingRepository, @NonNull final UserSelectionRepository userSelectionRepository) {
        final PaymentMethod paymentMethod = userSelectionRepository.getPaymentMethod();
        final String productId = productIdProvider.getProductId();
        final String customOptionId = userSelectionRepository.getCard() != null ?
            userSelectionRepository.getCard().getId() : null;
        final boolean isCard = PaymentTypes.isCardPaymentType(paymentMethod.getPaymentTypeId());
        final boolean escEnable = paymentSettingRepository.getAdvancedConfiguration().isEscEnabled();
        final EscValidationData escValidationData = new EscValidationData.Builder(customOptionId, isCard, escEnable)
            .build();
        return new SecurityValidationData.Builder(productId).setEscValidationData(escValidationData).build();
    }
}