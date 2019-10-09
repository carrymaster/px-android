package com.mercadopago.android.px.addons.validator.internal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EscRulesTest extends BaseSecurityRulesTest {

    private EscRules escRules;

    @Before
    public void setUp() {
        super.setUp();
        escRules = new EscRules(escManagerBehaviour);
    }

    @Test
    public void testValidData_rulesTrue() {
        Assert.assertTrue(escRules.apply(escValidationData));
    }

    @Test
    public void testEscEnableFalse_rulesFalse() {
        isEscEnableReturnFalse();
        Assert.assertFalse(escRules.apply(escValidationData));
    }

    @Test
    public void testCardIdNull_rulesFalse() {
        when(escValidationData.getCardId()).thenReturn(null);
        Assert.assertFalse(escRules.apply(escValidationData));
    }

    @Test
    public void testCardIdEmpty_rulesFalse() {
        when(escValidationData.getCardId()).thenReturn(EMPTY_STRING);
        Assert.assertFalse(escRules.apply(escValidationData));
    }

    @Test
    public void testHasEscFalse_rulesFalse() {
        when(escManagerBehaviour.getESC(anyString(), nullable(String.class), nullable(String.class))).thenReturn(null);
        Assert.assertFalse(escRules.apply(escValidationData));
    }
}