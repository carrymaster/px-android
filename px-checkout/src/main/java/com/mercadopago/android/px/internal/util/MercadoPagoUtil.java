package com.mercadopago.android.px.internal.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.model.Bin;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.exceptions.BinException;
import java.util.ArrayList;
import java.util.List;

public final class MercadoPagoUtil {

    private static final String SDK_PREFIX = "px_";
    private static final String PACKAGE_NAME_MP = "com.mercadopago.wallet";
    private static final String PLATFORM_MP = "MP";
    private static final String PLATFORM_ML = "ML";
    private static final String INTERNAL = "INTERNAL";

    private MercadoPagoUtil() {
    }

    public static int getPaymentMethodIcon(final Context context, final String paymentMethodId) {
        return getPaymentMethodPicture(context, SDK_PREFIX, paymentMethodId);
    }

    private static int getPaymentMethodPicture(final Context context, final String type, String paymentMethodId) {
        int resource;
        paymentMethodId = type + paymentMethodId;
        try {
            resource = context.getResources().getIdentifier(paymentMethodId, "drawable", context.getPackageName());
        } catch (final Exception e) {
            try {
                resource =
                    context.getResources().getIdentifier(SDK_PREFIX + "bank", "drawable", context.getPackageName());
            } catch (final Exception ex) {
                resource = 0;
            }
        }
        return resource;
    }

    public static int getPaymentMethodSearchItemIcon(final Context context, final String itemId) {
        int resource;
        if (itemId != null && context != null) {
            try {
                resource =
                    context.getResources().getIdentifier(SDK_PREFIX + itemId, "drawable", context.getPackageName());
            } catch (final Exception e) {
                resource = 0;
            }
        } else {
            resource = 0;
        }
        return resource;
    }

    public static boolean isCard(final String paymentTypeId) {
        return (paymentTypeId != null) &&
            (paymentTypeId.equals(PaymentTypes.CREDIT_CARD) || paymentTypeId.equals(PaymentTypes.DEBIT_CARD) ||
                paymentTypeId.equals(PaymentTypes.PREPAID_CARD));
    }

    public static String getAccreditationTimeMessage(final Context context, final int milliseconds) {
        final String accreditationMessage;

        if (milliseconds == 0) {
            accreditationMessage = context.getString(R.string.px_instant_accreditation_time);
        } else {
            final StringBuilder accreditationTimeMessageBuilder = new StringBuilder();
            if (milliseconds >= 1440 && milliseconds < 2880) {

                accreditationTimeMessageBuilder.append(context.getString(R.string.px_accreditation_time));
                accreditationTimeMessageBuilder.append(" 1 ");
                accreditationTimeMessageBuilder.append(context.getString(R.string.px_working_day));
            } else if (milliseconds < 1440) {

                accreditationTimeMessageBuilder.append(context.getString(R.string.px_accreditation_time));
                accreditationTimeMessageBuilder.append(" ");
                accreditationTimeMessageBuilder.append(milliseconds / 60);
                accreditationTimeMessageBuilder.append(" ");
                accreditationTimeMessageBuilder.append(context.getString(R.string.px_hour));
            } else {

                accreditationTimeMessageBuilder.append(context.getString(R.string.px_accreditation_time));
                accreditationTimeMessageBuilder.append(" ");
                accreditationTimeMessageBuilder.append(milliseconds / (60 * 24));
                accreditationTimeMessageBuilder.append(" ");
                accreditationTimeMessageBuilder.append(context.getString(R.string.px_working_days));
            }
            accreditationMessage = accreditationTimeMessageBuilder.toString();
        }
        return accreditationMessage;
    }

    public static List<PaymentMethod> getValidPaymentMethodsForBin(final String bin,
        final Iterable<PaymentMethod> paymentMethods) {
        if (bin.length() == Bin.BIN_LENGTH) {
            final List<PaymentMethod> validPaymentMethods = new ArrayList<>();
            for (final PaymentMethod pm : paymentMethods) {
                if (pm.isValidForBin(bin)) {
                    validPaymentMethods.add(pm);
                }
            }
            return validPaymentMethods;
        }
        throw new BinException(bin.length());
    }

    public static boolean isMPInstalled(final PackageManager packageManager) {
        try {
            return packageManager != null && packageManager.getApplicationInfo(PACKAGE_NAME_MP, 0).enabled;
        } catch (final PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static String getPlatform(final Context context) {
        final String packageName = context.getApplicationInfo().packageName;
        return packageName.contains("com.mercadolibre") ? PLATFORM_ML : PLATFORM_MP;
    }

    public static Intent getSafeIntent(@NonNull final Context context, @NonNull final Uri uri) {
        return getSafeIntent(context).setData(uri);
    }

    public static Intent getSafeIntent(@NonNull final Context context) {
        return new Intent(Intent.ACTION_VIEW).setPackage(context.getPackageName()).putExtra(INTERNAL, true);
    }
}
