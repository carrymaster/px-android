package com.mercadopago.android.px.mocks;

import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.model.internal.InitResponse;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.utils.ResourcesUtil;

import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubBuilderOneItemAndPayer;

public final class PaymentMethodSearchs {

    private PaymentMethodSearchs() {
    }

    public static InitResponse getInitResponse() {
        String json = ResourcesUtil.getStringResource("complete_payment_method_search_MLA.json");
        return getPaymentMethodSearch(json);
    }

    public static InitResponse getPaymentMethodSearchWithSavedCardsMLA() {
        String json = ResourcesUtil.getStringResource("saved_cards_payment_method_search_MLA.json");
        return getPaymentMethodSearch(json);
    }

    public static InitResponse getPaymentMethodSearchWithPaymentMethodOnTop() {
        String json = ResourcesUtil.getStringResource("payment_method_on_top.json");
        return getPaymentMethodSearch(json);
    }

    public static InitResponse getPaymentMethodWithoutCustomOptionsMLA() {
        String json = ResourcesUtil.getStringResource("not_cards_nor_account_money_MLA.json");
        return getPaymentMethodSearch(json);
    }

    public static InitResponse getPaymentMethodSearchWithOnlyCreditCardMLA() {
        String json = ResourcesUtil.getStringResource("only_credit_card_MLA.json");
        return getPaymentMethodSearch(json);
    }

    public static InitResponse getPaymentMethodSearchWithOnlyTicketMLA() {
        String json = ResourcesUtil.getStringResource("only_ticket_MLA.json");
        return getPaymentMethodSearch(json);
    }

    public static InitResponse getPaymentMethodSearchWithOnlyAccountMoneyMLA() {
        String json = ResourcesUtil.getStringResource("only_account_money_MLA.json");
        return getPaymentMethodSearch(json);
    }

    public static InitResponse getPaymentMethodSearchWithOnlyMercadoCredito() {
        String json = ResourcesUtil.getStringResource("mercado_credito_mock.json");
        return getPaymentMethodSearch(json);
    }

    public static InitResponse getPaymentMethodSearchWithOnlyCreditCardAndOneCardMLA() {
        String json = ResourcesUtil.getStringResource("only_credit_card_and_one_card_MLA.json");
        return getPaymentMethodSearch(json);
    }

    public static InitResponse getPaymentMethodSearchWithOnlyCreditCardAndAccountMoneyMLA() {
        String json = ResourcesUtil.getStringResource("only_credit_card_and_account_money_MLA.json");
        return getPaymentMethodSearch(json);
    }

    public static InitResponse getPaymentMethodSearchWithOnlyOneOffTypeAndAccountMoneyMLA() {
        String json = ResourcesUtil.getStringResource("only_one_off_type_and_account_money_MLA.json");
        return getPaymentMethodSearch(json);
    }

    public static InitResponse getPaymentMethodSearchWithOnlyBolbradescoMLB() {
        String json = ResourcesUtil.getStringResource("only_bolbradesco_payment_method_search_MLB.json");
        return getPaymentMethodSearch(json);
    }

    private static InitResponse getPaymentMethodSearch(final String json) {
        final String siteJson =
            String.format(ResourcesUtil.getStringResource("response_site_template.json"), Sites.ARGENTINA.getId());
        final String preferenceJson = JsonUtil.getInstance().toJson(stubExpiredPreference());
        final String initJson = String.format(json, siteJson, preferenceJson);
        return JsonUtil.getInstance().fromJson(initJson, InitResponse.class);
    }

    private static CheckoutPreference stubExpiredPreference() {
        return stubBuilderOneItemAndPayer().build();
    }
}