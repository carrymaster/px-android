package com.mercadopago.android.px.mocks;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.utils.ResourcesUtil;
import java.lang.reflect.Type;
import java.util.List;

public class Issuers {
    private Issuers() {
    }

    public static List<Issuer> getIssuers() {
        String json = ResourcesUtil.getStringResource("issuers.json");
        Type listType = new TypeToken<List<Issuer>>() {
        }.getType();
        return JsonUtil.fromJson(json, listType);
    }

    public static Issuer getIssuerMLA() {
        String json = ResourcesUtil.getStringResource("issuer_MLA.json");
        return JsonUtil.fromJson(json, Issuer.class);
    }

    public static List<Issuer> getOneIssuerListMLA() {
        List<Issuer> issuerList;
        String json = ResourcesUtil.getStringResource("issuer_list_MLA.json");

        try {
            Type listType = new TypeToken<List<Issuer>>() {
            }.getType();
            issuerList = JsonUtil.fromJson(json, listType);
        } catch (Exception ex) {
            issuerList = null;
        }
        return issuerList;
    }

    public static List<Issuer> getIssuersListMLA() {
        List<Issuer> issuerList;
        String json = ResourcesUtil.getStringResource("issuers.json");

        try {
            Type listType = new TypeToken<List<Issuer>>() {
            }.getType();
            issuerList = JsonUtil.fromJson(json, listType);
        } catch (Exception ex) {
            issuerList = null;
        }
        return issuerList;
    }
}
