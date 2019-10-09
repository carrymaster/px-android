package com.mercadopago.android.px.internal.features.uicontrollers;

import android.graphics.Typeface;
import java.util.HashMap;

public class FontCache {

    public static final String CUSTOM_REGULAR_FONT = "custom_regular";
    public static final String CUSTOM_LIGHT_FONT = "custom_light";
    public static final String CUSTOM_MONO_FONT = "custom_mono";

    public static final String FONT_ROBOTO = "Roboto";
    public static final String FONT_ROBOTO_MONO = "Roboto Mono";

    private static final HashMap<String, Typeface> fontCache = new HashMap<>();

    public static void setTypeface(final String fontName, final Typeface typeFace) {
        fontCache.put(fontName, typeFace);
    }

    public static Typeface getTypeface(final String fontName) {
        return fontCache.get(fontName);
    }

    public static boolean hasTypeface(final String fontName) {
        return fontCache.containsKey(fontName);
    }
}