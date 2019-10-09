package com.mercadopago.android.px.internal.util;

import android.support.annotation.Nullable;
import java.util.Collection;

public final class ListUtil {

    private ListUtil() {
        throw new AssertionError("Util classes shouldn't be instantiated.");
    }

    public static boolean isEmpty(@Nullable final Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(@Nullable final Collection collection) {
        return !isEmpty(collection);
    }
}