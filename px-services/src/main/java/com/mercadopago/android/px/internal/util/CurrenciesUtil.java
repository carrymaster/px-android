package com.mercadopago.android.px.internal.util;

import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.RelativeSizeSpan;
import com.mercadopago.android.px.internal.util.textformatter.SuperscriptSpanAdjuster;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.Site;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;

public final class CurrenciesUtil {

    private static final String CURRENCY_ARGENTINA = "ARS";
    private static final String CURRENCY_BRAZIL = "BRL";
    private static final String CURRENCY_CHILE = "CLP";
    private static final String CURRENCY_COLOMBIA = "COP";
    private static final String CURRENCY_MEXICO = "MXN";
    private static final String CURRENCY_VENEZUELA = "VES";
    private static final String CURRENCY_USA = "USD";
    private static final String CURRENCY_PERU = "PEN";
    private static final String CURRENCY_URUGUAY = "UYU";
    public static final String ZERO_DECIMAL = "00";

    private CurrenciesUtil() {
    }

    public static Map<String, Currency> currenciesList = new HashMap<String, Currency>() {{
        put(CURRENCY_ARGENTINA,
            new Currency(CURRENCY_ARGENTINA, "Peso argentino", "$", 2, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_BRAZIL, new Currency(CURRENCY_BRAZIL, "Real", "R$", 2, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_CHILE, new Currency(CURRENCY_CHILE, "Peso chileno", "$", 0, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_COLOMBIA,
            new Currency(CURRENCY_COLOMBIA, "Peso colombiano", "$", 0, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_MEXICO, new Currency(CURRENCY_MEXICO, "Peso mexicano", "$", 2, ".".charAt(0), ",".charAt(0)));
        put(CURRENCY_VENEZUELA,
            new Currency(CURRENCY_VENEZUELA, "Bolívares Soberanos", "BsS", 2, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_USA, new Currency(CURRENCY_USA, "Dolar americano", "US$", 2, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_PERU, new Currency(CURRENCY_PERU, "Soles", "S/.", 2, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_URUGUAY, new Currency(CURRENCY_URUGUAY, "Peso Uruguayo", "$", 2, ",".charAt(0), ".".charAt(0)));
    }};

    public static String getLocalizedAmountWithCurrencySymbol(@NonNull final BigDecimal amount,
        @NonNull final String currencyId,
        final boolean shouldAddSpace) {
        // Get currency configuration
        final Currency currency = CurrenciesUtil.currenciesList.get(currencyId);
        final String formattedAmount = getLocalizedAmount(amount, currency);

        // return formatted string
        final StringBuilder builder = new StringBuilder();
        builder.append(currency.getSymbol());
        if (shouldAddSpace) {
            builder.append(" ");
        }
        builder.append(formattedAmount);
        return builder.toString();
    }

    public static String getLocalizedAmountNoDecimals(final BigDecimal truncated, final Currency currency) {
        final DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator(currency.getDecimalSeparator());
        dfs.setGroupingSeparator(currency.getThousandsSeparator());
        final DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(dfs);
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(0);
        return df.format(truncated);
    }

    public static String getLocalizedAmount(@NonNull final BigDecimal amount, final Currency currency) {
        final DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator(currency.getDecimalSeparator());
        dfs.setGroupingSeparator(currency.getThousandsSeparator());
        final DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(dfs);
        df.setMinimumFractionDigits(currency.getDecimalPlaces());
        df.setMaximumFractionDigits(currency.getDecimalPlaces());
        return df.format(amount);
    }

    public static String getLocalizedAmountWithCurrencySymbol(final BigDecimal amount, final Site site) {
        return getLocalizedAmountWithCurrencySymbol(amount, site.getCurrencyId(), true);
    }

    public static String getLocalizedAmountWithCurrencySymbol(final BigDecimal amount, final String currencyId) {
        return getLocalizedAmountWithCurrencySymbol(amount, currencyId, true);
    }

    public static Spanned getSpannedAmountWithCurrencySymbol(final BigDecimal amount, final String currencyId) {
        return CurrenciesUtil.getSpannedString(amount, currencyId, false, true);
    }

    public static String getSymbol(@NonNull final String currencyId) {
        final Currency currency = currenciesList.get(currencyId);
        if (currency == null) {
            throw new IllegalStateException("invalid currencyId");
        }
        return currency.getSymbol();
    }

    public static Character getDecimalSeparator(@NonNull final String currencyId) {
        return CurrenciesUtil.getCurrency(currencyId).getDecimalSeparator();
    }

    public static String getDecimals(final String currencyId, final BigDecimal amount) {
        final Currency currency = CurrenciesUtil.currenciesList.get(currencyId);
        final String localizedAmount = getLocalizedAmount(amount, currency);
        final int decimalDivisionIndex = localizedAmount.indexOf(currency.getDecimalSeparator());
        String decimals = null;
        if (decimalDivisionIndex != -1) {
            decimals = localizedAmount.substring(decimalDivisionIndex + 1, localizedAmount.length());
        }
        return decimals;
    }

    public static Spanned getSpannedString(final BigDecimal amount, final String currencyId, final boolean symbolUp,
        final boolean decimalsUp) {
        String localizedAmount = CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(currencyId, amount);
        SpannableStringBuilder spannableAmount = new SpannableStringBuilder(localizedAmount);
        if (decimalsUp && !CurrenciesUtil.hasZeroDecimals(currencyId, amount)) {
            final int fromDecimals = localizedAmount.indexOf(CurrenciesUtil.getDecimalSeparator(currencyId)) + 1;
            localizedAmount =
                localizedAmount.replace(String.valueOf(CurrenciesUtil.getDecimalSeparator(currencyId)), " ");
            spannableAmount = new SpannableStringBuilder(localizedAmount);
            decimalsUp(currencyId, amount, spannableAmount, fromDecimals);
        }

        if (symbolUp) {
            symbolUp(currencyId, localizedAmount, spannableAmount);
        }

        return new SpannedString(spannableAmount);
    }

    public static boolean isValidCurrency(final String currencyId) {
        return !TextUtil.isEmpty(currencyId) && currenciesList.containsKey(currencyId);
    }

    public static Currency getCurrency(final String currencyKey) {
        return currenciesList.get(currencyKey);
    }

    public static String getLocalizedAmountWithoutZeroDecimals(@NonNull final String currencyId,
        @NonNull final BigDecimal amount) {
        String localized = getLocalizedAmountWithCurrencySymbol(amount, currencyId);
        if (hasZeroDecimals(currencyId, amount)) {
            final Character decimalSeparator = currenciesList.get(currencyId).getDecimalSeparator();
            final int decimalIndex = localized.indexOf(decimalSeparator);
            if (decimalIndex >= 0) {
                localized = localized.substring(0, decimalIndex);
            }
        }
        return localized;
    }

    public static boolean hasZeroDecimals(final String currencyId, final BigDecimal amount) {
        final String decimals = getDecimals(currencyId, amount);
        return ZERO_DECIMAL.equals(decimals) || TextUtil.isEmpty(decimals);
    }

    @NonNull
    public static SpannableStringBuilder getSpannableAmountWithSymbolWithoutZeroDecimals(
        @NonNull final String currencyId,
        @NonNull final BigDecimal amount) {
        String localizedAmount = CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(currencyId, amount);
        SpannableStringBuilder spannableAmount = new SpannableStringBuilder(localizedAmount);

        if (!CurrenciesUtil.hasZeroDecimals(currencyId, amount)) {
            final int fromDecimals = localizedAmount.indexOf(CurrenciesUtil.getDecimalSeparator(currencyId));
            localizedAmount =
                localizedAmount.replace(String.valueOf(CurrenciesUtil.getDecimalSeparator(currencyId)), "");
            spannableAmount = new SpannableStringBuilder(localizedAmount);
            decimalsUp(currencyId, amount, spannableAmount, fromDecimals);
        }

        symbolUp(currencyId, localizedAmount, spannableAmount);

        return spannableAmount;
    }

    private static void symbolUp(@NonNull final String currencyId, final String localizedAmount,
        final SpannableStringBuilder spannableAmount) {
        final int fromSymbolPosition = localizedAmount.indexOf(CurrenciesUtil.getSymbol(currencyId));
        final int toSymbolPosition = fromSymbolPosition + CurrenciesUtil.getSymbol(currencyId).length();
        spannableAmount.setSpan(new RelativeSizeSpan(0.5f), fromSymbolPosition, toSymbolPosition,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableAmount.setSpan(new SuperscriptSpanAdjuster(0.65f), fromSymbolPosition, toSymbolPosition,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static void decimalsUp(final String currencyId, final BigDecimal amount,
        final SpannableStringBuilder spannableAmount, final int fromDecimals) {
        final int toDecimals = fromDecimals + CurrenciesUtil.getDecimals(currencyId, amount).length();
        spannableAmount
            .setSpan(new RelativeSizeSpan(0.5f), fromDecimals, toDecimals, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableAmount.setSpan(new SuperscriptSpanAdjuster(0.7f), fromDecimals, toDecimals,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
