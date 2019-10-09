package com.mercadopago.android.px.internal.features.business_result;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.base.PXActivity;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.view.BusinessActions;
import com.mercadopago.android.px.internal.view.PaymentResultBody;
import com.mercadopago.android.px.internal.view.PaymentResultHeader;
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel;
import com.mercadopago.android.px.model.ExitAction;

import static com.mercadopago.android.px.internal.features.Constants.RESULT_CUSTOM_EXIT;
import static com.mercadopago.android.px.internal.util.MercadoPagoUtil.getSafeIntent;

public class BusinessPaymentResultActivity extends PXActivity<BusinessPaymentResultPresenter>
    implements BusinessPaymentResultContract.View {

    private static final String EXTRA_BUSINESS_PAYMENT_MODEL = "extra_business_payment_model";

    public static Intent getIntent(@NonNull final Context context,
        @NonNull final BusinessPaymentModel model) {
        final Intent intent = new Intent(context, BusinessPaymentResultActivity.class);
        intent.putExtra(EXTRA_BUSINESS_PAYMENT_MODEL, model);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.px_activity_payment_result);

        presenter = createPresenter();
        presenter.attachView(this);
        if (savedInstanceState == null) {
            presenter.onFreshStart();
        }
    }

    @NonNull
    private BusinessPaymentResultPresenter createPresenter() {
        final BusinessPaymentModel model = getIntent().getParcelableExtra(EXTRA_BUSINESS_PAYMENT_MODEL);
        return new BusinessPaymentResultPresenter(
            Session.getInstance().getConfigurationModule().getPaymentSettings(), model);
    }

    @Override
    public void configureViews(@NonNull final BusinessPaymentResultViewModel model,
        @NonNull final BusinessActions callback) {
        findViewById(R.id.loading).setVisibility(View.GONE);
        final PaymentResultHeader header = findViewById(R.id.header);
        header.setModel(model.headerModel);
        final PaymentResultBody body = findViewById(R.id.body);
        body.init(model.bodyModel, callback);
        //TODO migrate
        BusinessResultLegacyRenderer.render(findViewById(R.id.container), callback, model);
    }

    @Override
    public void onBackPressed() {
        presenter.onAbort();
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void processCustomExit() {
        processCustomExit(new ExitAction("exit", RESULT_OK));
    }

    @Override
    public void processCustomExit(@NonNull final ExitAction action) {
        final Intent intent = action.toIntent();
        setResult(RESULT_CUSTOM_EXIT, intent);
        finish();
    }

    @Override
    public void setStatusBarColor(@ColorRes final int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, color));
        }
    }

    @Override
    public void processBusinessAction(@NonNull final String deepLink) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink)));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processCrossSellingBusinessAction(@NonNull final String deepLink) {
        try {
            startActivity(getSafeIntent(this, Uri.parse(deepLink)));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}