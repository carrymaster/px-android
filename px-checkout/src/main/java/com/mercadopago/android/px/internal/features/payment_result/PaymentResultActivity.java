package com.mercadopago.android.px.internal.features.payment_result;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.mercadolibre.android.ui.widgets.MeliSnackbar;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.base.PXActivity;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.payment_result.components.PaymentResultLegacyRenderer;
import com.mercadopago.android.px.internal.features.payment_result.viewmodel.PaymentResultViewModel;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.internal.view.BusinessActions;
import com.mercadopago.android.px.internal.view.PaymentResultBody;
import com.mercadopago.android.px.internal.view.PaymentResultHeader;
import com.mercadopago.android.px.internal.viewmodel.ChangePaymentMethodPostPaymentAction;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.internal.viewmodel.RecoverPaymentPostPaymentAction;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

import static com.mercadopago.android.px.internal.features.Constants.RESULT_ACTION;
import static com.mercadopago.android.px.internal.features.Constants.RESULT_CUSTOM_EXIT;
import static com.mercadopago.android.px.internal.util.MercadoPagoUtil.getSafeIntent;

public class PaymentResultActivity extends PXActivity<PaymentResultPresenter> implements
    PaymentResultContract.View {

    private static final int CONGRATS_REQUEST_CODE = 16;
    private static final int INSTRUCTIONS_REQUEST_CODE = 14;
    private static final int REJECTION_REQUEST_CODE = 9;
    private static final int PENDING_REQUEST_CODE = 8;
    private static final int CALL_FOR_AUTHORIZE_REQUEST_CODE = 7;
    private static final String EXTRA_PAYMENT_MODEL = "extra_payment_model";
    public static final String EXTRA_RESULT_CODE = "extra_result_code";

    public static Intent getIntent(@NonNull final Context context, @NonNull final PaymentModel paymentModel) {
        final Intent intent = new Intent(context, PaymentResultActivity.class);
        intent.putExtra(EXTRA_PAYMENT_MODEL, paymentModel);
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
    private PaymentResultPresenter createPresenter() {
        final PaymentModel paymentModel = getIntent().getParcelableExtra(EXTRA_PAYMENT_MODEL);
        final Session session = Session.getInstance();

        return new PaymentResultPresenter(session.getConfigurationModule().getPaymentSettings(),
            session.getInstructionsRepository(), paymentModel);
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void configureViews(@NonNull final PaymentResultViewModel model, @NonNull final BusinessActions callback) {
        findViewById(R.id.loading).setVisibility(View.GONE);
        final PaymentResultHeader header = findViewById(R.id.header);
        header.setModel(model.headerModel);
        final PaymentResultBody body = findViewById(R.id.body);
        body.init(model.bodyModel, callback);
        //TODO migrate
        PaymentResultLegacyRenderer.render(findViewById(R.id.container), callback, model.legacyViewModel);
    }

    @Override
    public void showApiExceptionError(@NonNull final ApiException exception, @NonNull final String requestOrigin) {
        ErrorUtil.showApiExceptionError(this, exception, requestOrigin);
    }

    @Override
    public void showInstructionsError() {
        ErrorUtil.startErrorActivity(this,
            new MercadoPagoError(getString(R.string.px_standard_error_message), false));
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == CONGRATS_REQUEST_CODE) {
            finishWithOkResult(resultCode, data);
        } else if (requestCode == PENDING_REQUEST_CODE) {
            resolveRequest(resultCode, data);
        } else if (requestCode == REJECTION_REQUEST_CODE) {
            resolveRequest(resultCode, data);
        } else if (requestCode == CALL_FOR_AUTHORIZE_REQUEST_CODE) {
            resolveRequest(resultCode, data);
        } else if (requestCode == INSTRUCTIONS_REQUEST_CODE) {
            finishWithOkResult(resultCode, data);
        } else {
            finishWithCancelResult(data);
        }
    }

    @Override
    public void onBackPressed() {
        presenter.onAbort();
    }

    private void resolveRequest(final int resultCode, final Intent data) {
        if (resultCode == RESULT_CANCELED && data != null) {
            finishWithCancelResult(data);
        } else {
            finishWithOkResult(resultCode, data);
        }
    }

    private void finishWithCancelResult(final Intent data) {
        setResult(RESULT_CANCELED, data);
        finish();
    }

    private void finishWithOkResult(final int resultCode, final Intent data) {
        setResult(resultCode, data);
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
    public void openLink(@NonNull final String url) {
        try {
            final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (final Exception e) {

        }
    }

    @Override
    public void finishWithResult(final int resultCode) {
        final Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT_CODE, resultCode);
        setResult(RESULT_CUSTOM_EXIT, intent);
        finish();
    }

    @Override
    public void changePaymentMethod() {
        final Intent returnIntent = new Intent();
        new ChangePaymentMethodPostPaymentAction().addToIntent(returnIntent);
        setResult(RESULT_ACTION, returnIntent);
        finish();
    }

    @Override
    public void recoverPayment() {
        final Intent returnIntent = new Intent();
        new RecoverPaymentPostPaymentAction().addToIntent(returnIntent);
        setResult(RESULT_ACTION, returnIntent);
        finish();
    }

    @SuppressLint("Range")
    @Override
    public void copyToClipboard(@NonNull final String content) {
        final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clip = ClipData.newPlainText("", content);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            MeliSnackbar.make(findViewById(R.id.container),
                getString(R.string.px_copied_to_clipboard_ack),
                Snackbar.LENGTH_SHORT, MeliSnackbar.SnackbarType.SUCCESS).show();
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