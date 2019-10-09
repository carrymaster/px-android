package com.mercadopago.android.px.tracking.internal.views;

import com.mercadopago.android.px.model.CustomSearchItem;
import com.mercadopago.android.px.model.Item;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SelectMethodViewTest {

    private static final String EXPECTED_PATH = "/px_checkout/payments/select_method";

    private static final String EXPECTED_ONE_CARD_SAVED_WITH_ESC =
        "{available_methods=[{payment_method_id=visa, payment_method_type=credit_card, extra_info={issuer_id=null, card_id=123456, selected_installment=null, has_esc=true}}], available_methods_quantity=1.0, items=[{quantity=1.0, item={id=1234, description=description, price=10.0}}], preference_amount=10.0}";

    private static final String EXPECTED_ONE_CARD_SAVED_NO_ESC =
        "{available_methods=[{payment_method_id=visa, payment_method_type=credit_card, extra_info={issuer_id=null, card_id=123456, selected_installment=null, has_esc=false}}], available_methods_quantity=1.0, items=[{quantity=1.0, item={id=1234, description=description, price=10.0}}], preference_amount=10.0}";

    private static final String EXPECTED_JUST_ACCOUNT_MONEY =
        "{available_methods=[{payment_method_id=account_money, payment_method_type=account_money, extra_info=null}], available_methods_quantity=1.0, items=[{quantity=1.0, item={id=1234, description=description, price=10.0}}], preference_amount=10.0}";

    private static final String EXPECTED_JUST_ONE_GROUP =
        "{available_methods=[{payment_method_id=null, payment_method_type=cards, extra_info=null}], available_methods_quantity=1.0, items=[{quantity=1.0, item={id=1234, description=description, price=10.0}}], preference_amount=10.0}";

    private static final String CARD_ID = "123456";

    @Mock private Set<String> cardsWithEsc;
    @Mock private PaymentMethodSearch paymentMethodSearch;
    @Mock private CustomSearchItem customSearchItem;
    @Mock private PaymentMethodSearchItem pmSearchItem;
    @Mock private CheckoutPreference checkoutPreference;

    private final List<Item> items = new ArrayList<>();

    @Before
    public void setUp() {
        when(checkoutPreference.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(checkoutPreference.getItems()).thenReturn(items);
        items.add(new Item.Builder("title", 1, BigDecimal.TEN)
            .setDescription("description")
            .setId("1234")
            .build());
    }

    @Test
    public void whenGetPathObtainedIsCorrect() {
        final SelectMethodView selectMethodView = new SelectMethodView(paymentMethodSearch, cardsWithEsc,
            checkoutPreference);

        assertEquals(EXPECTED_PATH, selectMethodView.getViewPath());
    }

    @Test
    public void whenGetDataOneSavedCardWithEscReturnFormatIsAsExpected() {
        when(cardsWithEsc.contains(CARD_ID)).thenReturn(true);
        when(paymentMethodSearch.getCustomSearchItems()).thenReturn(Collections.singletonList(customSearchItem));
        when(customSearchItem.getId()).thenReturn(CARD_ID);
        when(customSearchItem.getPaymentMethodId()).thenReturn("visa");
        when(customSearchItem.getType()).thenReturn("credit_card");

        final SelectMethodView selectMethodView = new SelectMethodView(paymentMethodSearch, cardsWithEsc,
            checkoutPreference);

        assertEquals(EXPECTED_ONE_CARD_SAVED_WITH_ESC, selectMethodView.getData().toString());
    }

    @Test
    public void whenGetDataOneSavedCardWithNoEscReturnFormatIsAsExpected() {
        when(cardsWithEsc.contains(CARD_ID)).thenReturn(false);
        when(paymentMethodSearch.getCustomSearchItems()).thenReturn(Collections.singletonList(customSearchItem));
        when(customSearchItem.getId()).thenReturn(CARD_ID);
        when(customSearchItem.getPaymentMethodId()).thenReturn("visa");
        when(customSearchItem.getType()).thenReturn("credit_card");

        final SelectMethodView selectMethodView = new SelectMethodView(paymentMethodSearch, cardsWithEsc,
            checkoutPreference);

        assertEquals(EXPECTED_ONE_CARD_SAVED_NO_ESC, selectMethodView.getData().toString());
    }

    @Test
    public void whenGetDataOnePaymentMethodAccountMoneyReturnFormatIsAsExpected() {
        when(paymentMethodSearch.getCustomSearchItems()).thenReturn(Collections.singletonList(customSearchItem));
        when(customSearchItem.getId()).thenReturn("account_money");
        when(customSearchItem.getType()).thenReturn("account_money");

        final SelectMethodView selectMethodView = new SelectMethodView(paymentMethodSearch, cardsWithEsc,
            checkoutPreference);

        assertEquals(EXPECTED_JUST_ACCOUNT_MONEY, selectMethodView.getData().toString());
    }

    @Test
    public void whenGetDataOnePaymentMethodReturnFormatIsAsExpected() {
        when(paymentMethodSearch.getGroups()).thenReturn(Collections.singletonList(pmSearchItem));
        when(pmSearchItem.getId()).thenReturn("cards");

        final SelectMethodView selectMethodView = new SelectMethodView(paymentMethodSearch, cardsWithEsc,
            checkoutPreference);

        assertEquals(EXPECTED_JUST_ONE_GROUP, selectMethodView.getData().toString());
    }
}