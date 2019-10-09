package com.mercadopago.android.px.tracking.internal.events;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.tracking.internal.views.ResultViewTrack;

public final class DownloadAppEvent extends EventTracker {

    private final String eventPath;

    public DownloadAppEvent(@NonNull final ResultViewTrack resultViewTrack) {
        eventPath = resultViewTrack.getViewPath() + "/tap_download_app";
    }

    @NonNull
    @Override
    public String getEventPath() {
        return eventPath;
    }
}