package com.mercadopago.android.px.addons;

import com.mercadopago.android.px.addons.model.SecurityValidationData;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class SecurityDefaultBehaviourTest {

    private static final String DUMMY_FLOW_ID = "DUMMY_FLOW_ID";
    private final SecurityValidationData data =
        new SecurityValidationData.Builder(DUMMY_FLOW_ID).build();

    @Test
    public void testDefaultBehaviourIsSecurityEnabled_returnsFalse() {
        assertFalse(BehaviourProvider.getSecurityBehaviour().isSecurityEnabled(data));
    }
}