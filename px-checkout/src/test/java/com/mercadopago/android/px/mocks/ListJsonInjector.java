package com.mercadopago.android.px.mocks;

import android.support.annotation.NonNull;
import java.util.Iterator;

/* default */ final class ListJsonInjector {

    private static final String STRING_TEMPLATE = "%1$s";
    private static final String SEPARATOR = ",";
    private static final String EMPTY_JSON_LIST = "[]";
    private static final String NON_EMPTY_JSON_LIST = "[" + STRING_TEMPLATE + "]";

    private ListJsonInjector() {
    }

    @NonNull
    /* default */ static String injectAll(final Iterator<? extends JsonInjectable> iterator,
        final String jsonContainer) {
        if (!iterator.hasNext()) {
            return String.format(jsonContainer, EMPTY_JSON_LIST);
        }
        String temporalContainer = NON_EMPTY_JSON_LIST;
        while (iterator.hasNext()) {
            final JsonInjectable value = iterator.next();
            if (iterator.hasNext()) {
                temporalContainer = String.format(jsonContainer, value.getJson() + SEPARATOR + STRING_TEMPLATE);
            } else {
                temporalContainer = value.inject(jsonContainer);
            }
        }
        return String.format(jsonContainer, temporalContainer);
    }
}