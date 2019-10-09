package com.mercadopago.android.px.addons.validator.internal;

import com.mercadopago.android.px.addons.model.SecurityValidationData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SecurityRulesTest extends BaseSecurityRulesTest {

    @Mock private SecurityValidationData securityValidationData;
    private SecurityRules securityRulesTest;

    @Before
    public void setUp() {
        super.setUp();
        when(securityValidationData.getEscValidationData()).thenReturn(escValidationData);
        when(escValidationData.isCard()).thenReturn(false);
        securityRulesTest = new SecurityRules(escManagerBehaviour);
    }

    @Test
    public void testEscValidationDataValid_rulesTrue() {
        Assert.assertTrue(securityRulesTest.apply(securityValidationData));
    }

    @Test
    public void testEscValidationDataNull_rulesTrue() {
        when(securityValidationData.getEscValidationData()).thenReturn(null);
        Assert.assertTrue(securityRulesTest.apply(securityValidationData));
    }

    @Test
    public void testEscValidationDataNotNullAndIsNotCardFalse_rulesFalse() {
        when(escValidationData.isCard()).thenReturn(false);
        Assert.assertTrue(securityRulesTest.apply(securityValidationData));
    }
}