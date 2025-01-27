package com.mercadopago.android.px.mocks;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.BankDeal;
import com.mercadopago.android.px.utils.ResourcesUtil;
import java.lang.reflect.Type;
import java.util.List;

public final class BankDealsUtils {

    private BankDealsUtils() {

    }

    public static List<BankDeal> getBankDealsListMLA() {
        List<BankDeal> bankDealsList;
        String json = ResourcesUtil.getStringResource("bank_deals.json");

        try {
            Type listType = new TypeToken<List<BankDeal>>() {
            }.getType();
            bankDealsList = JsonUtil.fromJson(json, listType);
        } catch (Exception ex) {
            bankDealsList = null;
        }
        return bankDealsList;
    }
}
