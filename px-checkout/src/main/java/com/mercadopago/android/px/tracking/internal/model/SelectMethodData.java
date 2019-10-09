package com.mercadopago.android.px.tracking.internal.model;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings("unused")
@Keep
public class SelectMethodData extends TrackingMapModel {

    @NonNull private final List<AvailableMethod> availableMethods;
    @NonNull private final List<ItemInfo> items;
    @NonNull private final BigDecimal preferenceAmount;
    private final int availableMethodsQuantity;

    public SelectMethodData(@NonNull final List<AvailableMethod> availableMethods,
        @NonNull final List<ItemInfo> items,
        @NonNull final BigDecimal totalAmount) {
        this.availableMethods = availableMethods;
        this.items = items;
        preferenceAmount = totalAmount;
        availableMethodsQuantity = availableMethods.size();
    }
}
