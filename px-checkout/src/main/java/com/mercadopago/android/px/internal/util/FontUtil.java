package com.mercadopago.android.px.internal.util;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.provider.FontRequest;
import android.support.v4.provider.FontsContractCompat;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.uicontrollers.FontCache;

public final class FontUtil {
    private static Handler handler;

    private static final String QUERY_PARAM_NAME = "name=";
    private static final String QUERY_PARAM_WIDTH = "&width=";
    private static final String QUERY_PARAM_WEIGHT = "&weight=";
    private static final String QUERY_PARAM_ITALIC = "&italic=";
    private static final String QUERY_PARAM_BESTEFFORT = "&besteffort=";

    private static final int WIDTH_DEFAULT = 100;
    private static final int WEIGHT_DEFAULT = 400;
    private static final int WEIGHT_LIGHT = 300;
    private static final float ITALIC_DEFAULT = 0f;

    private FontUtil() {
    }

    public static void fetchFonts(@NonNull final Context context) {
        if (!FontCache.hasTypeface(FontCache.CUSTOM_REGULAR_FONT)) {
            fetchRegularFont(context);
        }
        if (!FontCache.hasTypeface(FontCache.CUSTOM_MONO_FONT)) {
            fetchMonoFont(context);
        }
        if (!FontCache.hasTypeface(FontCache.CUSTOM_LIGHT_FONT)) {
            fetchLightFont(context);
        }
    }

    private static void fetchRegularFont(@NonNull final Context context) {
        final FontsContractCompat.FontRequestCallback regularFontCallback = new FontsContractCompat
            .FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(final Typeface typeface) {
                FontCache.setTypeface(FontCache.CUSTOM_REGULAR_FONT, typeface);
            }

            @Override
            public void onTypefaceRequestFailed(final int reason) {
                //Do nothing
            }
        };
        FontsContractCompat.requestFont(context,
            getFontRequest(FontCache.FONT_ROBOTO, WIDTH_DEFAULT, WEIGHT_DEFAULT, ITALIC_DEFAULT),
            regularFontCallback,
            getHandlerThreadHandler());
    }

    private static void fetchLightFont(@NonNull final Context context) {
        final FontsContractCompat.FontRequestCallback lightFontCallback = new FontsContractCompat
            .FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(final Typeface typeface) {
                FontCache.setTypeface(FontCache.CUSTOM_LIGHT_FONT, typeface);
            }

            @Override
            public void onTypefaceRequestFailed(final int reason) {
                //Do nothing
            }
        };
        FontsContractCompat.requestFont(context,
            getFontRequest(FontCache.FONT_ROBOTO, WIDTH_DEFAULT, WEIGHT_LIGHT, ITALIC_DEFAULT),
            lightFontCallback,
            getHandlerThreadHandler());
    }

    private static void fetchMonoFont(@NonNull final Context context) {
        final FontsContractCompat.FontRequestCallback monoFontCallback = new FontsContractCompat
            .FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(final Typeface typeface) {
                FontCache.setTypeface(FontCache.CUSTOM_MONO_FONT, typeface);
            }

            @Override
            public void onTypefaceRequestFailed(final int reason) {
                //Do nothing
            }
        };
        FontsContractCompat.requestFont(context,
            getFontRequest(FontCache.FONT_ROBOTO_MONO, WIDTH_DEFAULT, WEIGHT_DEFAULT, ITALIC_DEFAULT),
            monoFontCallback,
            getHandlerThreadHandler());
    }

    private static FontRequest getFontRequest(@NonNull final String fontName, final int width, final int weight,
        final float italic) {

        final String query = QUERY_PARAM_NAME + fontName +
            QUERY_PARAM_WEIGHT + weight +
            QUERY_PARAM_WIDTH + width +
            QUERY_PARAM_ITALIC + italic +
            QUERY_PARAM_BESTEFFORT + true;

        return new FontRequest(
            "com.google.android.gms.fonts", "com.google.android.gms", query,
            R.array.com_google_android_gms_fonts_certs);
    }

    private static Handler getHandlerThreadHandler() {
        if (handler == null) {
            final HandlerThread handlerThread = new HandlerThread("fonts");
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
        }
        return handler;
    }
}
