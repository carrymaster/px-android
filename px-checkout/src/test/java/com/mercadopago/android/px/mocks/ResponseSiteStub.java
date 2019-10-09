package com.mercadopago.android.px.mocks;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.model.internal.InitResponse;
import com.mercadopago.android.px.utils.ResourcesUtil;

public enum ResponseSiteStub implements Injectable {
    MLA(Sites.ARGENTINA.getId()),
    MLB(Sites.BRASIL.getId());

    @NonNull private final String siteId;

    ResponseSiteStub(@NonNull final String siteId) {
        this.siteId = siteId;
    }

    public InitResponse.ResponseSite get() {
        final String json = getJson();
        return JsonUtil.getInstance().fromJson(json, InitResponse.ResponseSite.class);
    }

    @Override
    public String getJson() {
        return String.format(ResourcesUtil.getStringResource("response_site_template.json"), siteId);
    }
}