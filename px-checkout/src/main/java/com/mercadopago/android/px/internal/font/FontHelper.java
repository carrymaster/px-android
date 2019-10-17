package com.mercadopago.android.px.internal.font;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.provider.FontRequest;
import android.support.v4.provider.FontsContractCompat;
import android.util.SparseArray;
import android.widget.TextView;

import com.mercadolibre.android.ui.font.TypefaceHelper;
import com.mercadopago.android.px.R;

import static android.support.v4.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_FONT_NOT_FOUND;

public final class FontHelper {

    private static final String FONT_ROBOTO_MONO = "Roboto Mono";
    private static final String PROVIDER_AUTHORITY = "com.google.android.gms.fonts";
    private static final String PROVIDER_PACKAGE = "com.google.android.gms";

    /* default */ static final SparseArray<Typeface> CACHE = new SparseArray<>();
    private static final Handler HANDLER;

    static {
        final HandlerThread handlerThread = new HandlerThread("fonts");
        handlerThread.start();
        HANDLER = new Handler(handlerThread.getLooper());
    }

    private FontHelper() {
    }

    public static void init(@NonNull final Context context) {
        fetchFont(context, PxFont.MONOSPACE.id, FONT_ROBOTO_MONO, Typeface.MONOSPACE);
    }

    public static void setFont(@NonNull final CollapsingToolbarLayout toolbar, @NonNull final PxFont font) {
        getFont(toolbar.getContext(), font, new ResourcesCompat.FontCallback() {
            @Override
            public void onFontRetrieved(@NonNull final Typeface typeface) {
                toolbar.setCollapsedTitleTypeface(typeface);
                toolbar.setExpandedTitleTypeface(typeface);
            }

            @Override
            public void onFontRetrievalFailed(final int i) {
                //Nothing to do here
            }
        });
    }

    public static void setFont(@NonNull final TextView view, @NonNull final PxFont font) {
        final Typeface typeface = CACHE.get(font.id);
        if (typeface != null) {
            view.setTypeface(typeface);
        } else if (font.font != null) {
            TypefaceHelper.setTypeface(view, font.font);
        }
    }

    public static void getFont(@NonNull final Context context, @NonNull final PxFont font,
        @NonNull final ResourcesCompat.FontCallback callback) {
        final Typeface cacheTypeface = CACHE.get(font.id);
        if (cacheTypeface != null) {
            callback.onFontRetrieved(cacheTypeface);
        } else {
            findFont(context, font, callback);
        }
    }

    private static void findFont(@NonNull Context context, @NonNull PxFont font, @NonNull ResourcesCompat.FontCallback callback) {
        Typeface typeface = TypefaceHelper.geyFontTypeface(context, font.font);
        if (typeface == null) {
            callback.onFontRetrievalFailed(FAIL_REASON_FONT_NOT_FOUND);
        } else {
            callback.onFontRetrieved(typeface);
        }
    }

    private static void fetchFont(@NonNull final Context context, final int id, @NonNull final String fontName,
        @Nullable final Typeface fallback) {
        final FontsContractCompat.FontRequestCallback callback = new FontsContractCompat.FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(final Typeface typeface) {
                CACHE.put(id, typeface);
            }

            @Override
            public void onTypefaceRequestFailed(final int reason) {
                if (fallback != null) {
                    CACHE.put(id, fallback);
                }
            }
        };
        FontsContractCompat.requestFont(context,
            new FontRequest(PROVIDER_AUTHORITY, PROVIDER_PACKAGE, fontName, R.array.com_google_android_gms_fonts_certs),
            callback, HANDLER);
    }
}