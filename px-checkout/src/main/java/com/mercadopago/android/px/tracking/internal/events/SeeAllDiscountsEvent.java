package com.mercadopago.android.px.tracking.internal.events;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.tracking.internal.views.ResultViewTrack;

public final class SeeAllDiscountsEvent extends EventTracker {

    private final String eventPath;

    public SeeAllDiscountsEvent(@NonNull final ResultViewTrack resultViewTrack) {
        eventPath = resultViewTrack.getViewPath() + "/tap_see_all_discounts";
    }

    @NonNull
    @Override
    public String getEventPath() {
        return eventPath;
    }
}