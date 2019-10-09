package com.mercadopago.android.px.addons.validator.internal;

import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.addons.model.EscValidationData;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

abstract class BaseSecurityRulesTest {

    private static final String CARD_ID = "123";
    private static final String ESC = "123";
    static final String EMPTY_STRING = "";

    @Mock ESCManagerBehaviour escManagerBehaviour;
    @Mock EscValidationData escValidationData;

    void setUp() {
        isEscEnableReturnTrue();
        when(escValidationData.getCardId()).thenReturn(CARD_ID);
        when(escManagerBehaviour.getESC(anyString(), nullable(String.class), nullable(String.class))).thenReturn(ESC);
    }

    private void isEscEnableReturnTrue() {
        when(escValidationData.isEscEnable()).thenReturn(true);
    }

    void isEscEnableReturnFalse() {
        when(escValidationData.isEscEnable()).thenReturn(false);
    }
}
