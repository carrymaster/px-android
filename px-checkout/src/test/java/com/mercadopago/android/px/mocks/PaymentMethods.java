package com.mercadopago.android.px.mocks;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.utils.ResourcesUtil;
import java.lang.reflect.Type;
import java.util.List;

public class PaymentMethods {

    private static String doNotFindPaymentMethodsException =
        "{\"message\":\"doesn't find payment methods\",\"error\":\"payment methods not found error\",\"cause\":[]}";

    private PaymentMethods() {

    }

    public static PaymentMethod getPaymentMethodWithWrongSecurityCodeSettings() {
        final String json = ResourcesUtil.getStringResource("payment_method_security_code_length_0.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethod.class);
    }

    public static ApiException getDoNotFindPaymentMethodsException() {
        return JsonUtil.getInstance().fromJson(doNotFindPaymentMethodsException, ApiException.class);
    }

    public static PaymentMethod getPaymentMethodWithIdNotRequired() {
        final String json = ResourcesUtil.getStringResource("payment_method_id_not_required.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethod.class);
    }

    public static PaymentMethod getPaymentMethodOnDebit() {
        final String json = ResourcesUtil.getStringResource("pm_visa_debito.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethod.class);
    }

    public static PaymentMethod getPaymentMethodOnVisa() {
        final String json = ResourcesUtil.getStringResource("payment_method_visa.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethod.class);
    }

    public static PaymentMethod getPaymentMethodOnMaster() {
        final String json = ResourcesUtil.getStringResource("payment_method_on_master.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethod.class);
    }

    public static PaymentMethod getPaymentMethodOff() {
        final String json = ResourcesUtil.getStringResource("pm_pagofacil_off.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethod.class);
    }

    public static List<PaymentMethod> getPaymentMethodListMLA() {
        List<PaymentMethod> paymentMethodList;
        final String json = ResourcesUtil.getStringResource("payment_methods.json");

        try {
            final Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            paymentMethodList = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            paymentMethodList = null;
        }
        return paymentMethodList;
    }

    public static List<PaymentMethod> getPaymentMethodListWithoutCreditCardMLA() {
        List<PaymentMethod> paymentMethodList;
        final String json = ResourcesUtil.getStringResource("payment_methods_credit_card_excluded.json");

        try {
            final Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            paymentMethodList = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            paymentMethodList = null;
        }
        return paymentMethodList;
    }

    public static List<PaymentMethod> getCardPaymentMethodListMLA() {
        List<PaymentMethod> cardPaymentMethods;
        final String json = ResourcesUtil.getStringResource("card_payment_methods.json");

        try {
            final Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            cardPaymentMethods = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            cardPaymentMethods = null;
        }
        return cardPaymentMethods;
    }

    public static List<PaymentMethod> getPaymentMethodListWithTwoOptions() {
        List<PaymentMethod> paymentMethodList;
        final String json = ResourcesUtil.getStringResource("payment_methods_two_options.json");

        try {
            final Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            paymentMethodList = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            paymentMethodList = null;
        }
        return paymentMethodList;
    }

    public static List<PaymentMethod> getPaymentMethodListMLM() {
        List<PaymentMethod> paymentMethodList;
        final String json = ResourcesUtil.getStringResource("payment_methods_mlm.json");

        try {
            final Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            paymentMethodList = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            paymentMethodList = null;
        }
        return paymentMethodList;
    }
}
