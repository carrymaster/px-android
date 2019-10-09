package com.mercadopago.android.px.tracking.internal.events;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.tracking.internal.views.ResultViewTrack;

public final class CrossSellingEvent extends EventTracker {

    private final String eventPath;

    public CrossSellingEvent(@NonNull final ResultViewTrack resultViewTrack) {
        eventPath = resultViewTrack.getViewPath() + "/tap_cross_selling";
    }

    @NonNull
    @Override
    public String getEventPath() {
        return eventPath;
    }
}