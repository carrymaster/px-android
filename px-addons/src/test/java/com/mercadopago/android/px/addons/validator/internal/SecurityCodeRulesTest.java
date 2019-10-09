package com.mercadopago.android.px.addons.validator.internal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SecurityCodeRulesTest extends BaseSecurityRulesTest {

    private SecurityCodeRules securityCodeRules;

    @Before
    public void setUp() {
        super.setUp();
        when(escValidationData.isCard()).thenReturn(false);
        securityCodeRules = new SecurityCodeRules(escManagerBehaviour);
    }

    @Test
    public void testEscValidationDataValid_rulesTrue() {
        Assert.assertTrue(securityCodeRules.apply(escValidationData));
    }

    @Test
    public void testEscValidationDataIsNotCardTrueAndEscRulesFalse_rulesTrue() {
        isEscEnableReturnFalse();
        Assert.assertTrue(securityCodeRules.apply(escValidationData));
    }

    @Test
    public void testEscValidationDataIsNotCardFalseAndEscRulesTrue_rulesTrue() {
        when(escValidationData.isCard()).thenReturn(true);
        Assert.assertTrue(securityCodeRules.apply(escValidationData));
    }

    @Test
    public void testEscValidationDataIsNotCardFalseAndEscRulesFalse_rulesFalse() {
        isEscEnableReturnFalse();
        when(escValidationData.isCard()).thenReturn(true);
        Assert.assertFalse(securityCodeRules.apply(escValidationData));
    }
}