package com.mercadopago.android.px.tracking.internal.events;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.tracking.internal.views.ResultViewTrack;

public final class ScoreEvent extends EventTracker {

    private final String eventPath;

    public ScoreEvent(@NonNull final ResultViewTrack resultViewTrack) {
        eventPath = resultViewTrack.getViewPath() + "/tap_score";
    }

    @NonNull
    @Override
    public String getEventPath() {
        return eventPath;
    }
}