package com.mercadopago.android.px.model;

import android.support.test.runner.AndroidJUnit4;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.checkout.CheckoutActivity;
import com.mercadopago.android.px.model.exceptions.CardTokenException;
import com.mercadopago.android.px.model.exceptions.ExceptionHandler;
import com.mercadopago.android.px.test.BaseTest;
import com.mercadopago.android.px.test.StaticMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class CardTokenTest extends BaseTest<CheckoutActivity> {

    public CardTokenTest() {
        setup(CheckoutActivity.class);
    }

    @Test
    public void testConstructor() {
        final CardToken cardToken = StaticMock.getCardToken();
        assertEquals(StaticMock.DUMMY_CARD_NUMBER, cardToken.getCardNumber());
        assertEquals(StaticMock.DUMMY_EXPIRATION_MONTH, (int) cardToken.getExpirationMonth());
        assertEquals(StaticMock.DUMMY_EXPIRATION_YEAR_LONG, (int) cardToken.getExpirationYear());
        assertEquals(StaticMock.DUMMY_SECURITY_CODE, cardToken.getSecurityCode());
        assertEquals(StaticMock.DUMMY_CARDHOLDER_NAME, cardToken.getCardholder().getName());
        assertEquals(StaticMock.DUMMY_IDENTIFICATION_TYPE, cardToken.getCardholder().getIdentification().getType());
        assertEquals(StaticMock.DUMMY_IDENTIFICATION_NUMBER, cardToken.getCardholder().getIdentification().getNumber());
    }

    @Test
    public void testValidate() {
        final CardToken cardToken = StaticMock.getCardToken();
        assertTrue(cardToken.validate(true));
    }

    @Test
    public void testValidateNoSecurityCode() {
        final CardToken cardToken = StaticMock.getCardToken();
        assertTrue(cardToken.validate(false));
    }

    // * Card number
    @Test
    public void testCardNumber() {
        final CardToken cardToken = StaticMock.getCardToken();
        assertTrue(cardToken.validateCardNumber());
    }

    @Test
    public void testCardNumberEmpty() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("");

        assertFalse(cardToken.validateCardNumber());
    }

    @Test
    public void testCardNumberMinLength() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("4444");

        assertFalse(cardToken.validateCardNumber());
    }

    @Test
    public void testCardNumberMaxLength() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("44440000444400004444");

        assertFalse(cardToken.validateCardNumber());
    }

    @Test
    public void testCardNumberWithPaymentMethod() {
        final CardToken cardToken = StaticMock.getCardToken();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethod(getApplicationContext());

        try {
            cardToken.validateCardNumber(paymentMethod);
        } catch (final CardTokenException ex) {
            fail("Failed on validate card number with payment.json method:" + ex.getMessage());
        }
    }

    @Test
    public void testCardNumberWithPaymentMethodEmptyCardNumber() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("");
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethod(getApplicationContext());

        try {
            cardToken.validateCardNumber(paymentMethod);
            fail("Should have failed on empty card number");
        } catch (final CardTokenException ex) {
            assertEquals(CardTokenException.INVALID_EMPTY_CARD, ex.getErrorCode());
            final String message = ExceptionHandler.getErrorMessage(getApplicationContext(), ex);
            final String expectedMessage = getApplicationContext().getString(R.string.px_invalid_empty_card);
            assertEquals(message, expectedMessage);
        }
    }

    @Test
    public void testCardNumberWithPaymentMethodInvalidBin() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("5300888800009999");
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethod(getApplicationContext());

        try {
            cardToken.validateCardNumber(paymentMethod);
            fail("Should have failed on invalid bin");
        } catch (CardTokenException ex) {
            assertEquals(CardTokenException.INVALID_CARD_BIN, ex.getErrorCode());
            final String message = ExceptionHandler.getErrorMessage(getApplicationContext(), ex);
            final String expectedMessage = getApplicationContext().getString(R.string.px_invalid_card_bin);
            assertEquals(message, expectedMessage);
        }
    }

    @Test
    public void testCardNumberWithPaymentMethodInvalidLength() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("466057001125");
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethod(getApplicationContext());

        try {
            cardToken.validateCardNumber(paymentMethod);
            fail("Should have failed on invalid card length");
        } catch (final CardTokenException ex) {
            assertEquals(CardTokenException.INVALID_CARD_LENGTH, ex.getErrorCode());
            final String message = ExceptionHandler.getErrorMessage(getApplicationContext(), ex);
            final String expectedMessage =
                getApplicationContext().getString(R.string.px_invalid_card_length, String.valueOf(16));
            assertEquals(message, expectedMessage);
        }
    }

    @Test
    public void testCardNumberWithPaymentMethodInvalidLuhn() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("4660888888888888");
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethod(getApplicationContext());

        try {
            cardToken.validateCardNumber(paymentMethod);
            fail("Should have failed on invalid luhn");
        } catch (final CardTokenException ex) {
            assertEquals(CardTokenException.INVALID_CARD_LUHN, ex.getErrorCode());
            final String message = ExceptionHandler.getErrorMessage(getApplicationContext(), ex);
            final String expectedMessage = getApplicationContext().getString(R.string.px_invalid_card_luhn);
            assertEquals(message, "");
        }
    }

    // * Security code
    @Test
    public void testSecurityCode() {
        final CardToken cardToken = StaticMock.getCardToken();

        assertTrue(cardToken.validateSecurityCode());
    }

    @Test
    public void testSecurityCodeEmpty() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setSecurityCode("");

        assertFalse(cardToken.validateSecurityCode());
    }

    @Test
    public void testSecurityCodeMinLength() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setSecurityCode("4");

        assertFalse(cardToken.validateSecurityCode());
    }

    @Test
    public void testSecurityCodeMaxLength() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setSecurityCode("44444");

        assertFalse(cardToken.validateSecurityCode());
    }

    @Test
    public void testSecurityCodeWithPaymentMethod() {
        final CardToken cardToken = StaticMock.getCardToken();
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethod(getApplicationContext());

        try {
            cardToken.validateSecurityCode(paymentMethod);
        } catch (final CardTokenException ex) {
            fail("Failed on validate security code with payment.json method:" + ex.getMessage());
        }
    }

    @Test
    public void testSecurityCodeWithPaymentMethodInvalidBin() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardNumber("5300888800009999");
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethod(getApplicationContext());

        try {
            cardToken.validateSecurityCode(paymentMethod);
            fail("Should have failed on invalid bin");
        } catch (final CardTokenException ex) {
            assertEquals(CardTokenException.INVALID_FIELD, ex.getErrorCode());
            final String message = ExceptionHandler.getErrorMessage(getApplicationContext(), ex);
            final String expectedMessage = getApplicationContext().getString(R.string.px_invalid_field);
            assertEquals(message, expectedMessage);
        }
    }

    @Test
    public void testSecurityCodeWithPaymentMethodInvalidLength() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setSecurityCode("4444");
        final PaymentMethod paymentMethod = StaticMock.getPaymentMethod(getApplicationContext());

        try {
            cardToken.validateSecurityCode(paymentMethod);
            fail("Should have failed on invalid security code length");
        } catch (final CardTokenException ex) {
            assertEquals(CardTokenException.INVALID_CVV_LENGTH, ex.getErrorCode());
            final String message = ExceptionHandler.getErrorMessage(getApplicationContext(), ex);
            final String expectedMessage =
                getApplicationContext().getString(R.string.px_invalid_cvv_length, String.valueOf(3));
            assertEquals(message, expectedMessage);
        }
    }

    // * Expiry date
    @Test
    public void testExpiryDate() {
        final CardToken cardToken = StaticMock.getCardToken();

        assertTrue(cardToken.validateExpiryDate());
    }

    @Test
    public void testExpiryDateShortYear() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setExpirationYear(23);

        assertTrue(cardToken.validateExpiryDate());
    }

    @Test
    public void testExpiryDateNullMonth() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setExpirationMonth(null);

        assertFalse(cardToken.validateExpiryDate());
    }

    @Test
    public void testExpiryDateWrongMonth() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setExpirationMonth(13);

        assertFalse(cardToken.validateExpiryDate());
    }

    @Test
    public void testExpiryDateNullYear() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setExpirationYear(null);

        assertFalse(cardToken.validateExpiryDate());
    }

    @Test
    public void testExpiryDateWrongYear() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setExpirationYear(2000);

        assertFalse(cardToken.validateExpiryDate());
    }

    @Test
    public void testExpiryDateWrongShortYear() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setExpirationYear(10);

        assertFalse(cardToken.validateExpiryDate());
    }

    // * Identification
    @Test
    public void testIdentification() {
        final CardToken cardToken = StaticMock.getCardToken();

        assertTrue(cardToken.validateIdentification());
    }

    @Test
    public void testIdentificationNullCardholder() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardholder(null);

        assertFalse(cardToken.validateIdentification());
    }

    @Test
    public void testIdentificationNullIdentification() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().setIdentification(null);

        assertFalse(cardToken.validateIdentification());
    }

    @Test
    public void testIdentificationEmptyType() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().getIdentification().setType("");

        assertFalse(cardToken.validateIdentification());
    }

    @Test
    public void testIdentificationEmptyNumber() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().getIdentification().setNumber("");

        assertFalse(cardToken.validateIdentification());
    }

    @Test
    public void testIdentificationNumber() {
        final CardToken cardToken = StaticMock.getCardToken();
        final IdentificationType type = StaticMock.getIdentificationType();

        assertTrue(cardToken.validateIdentificationNumber(type));
    }

    @Test
    public void testIdentificationNumberWrongLength() {
        CardToken cardToken;
        IdentificationType type;

        cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().getIdentification().setNumber("123456");
        type = StaticMock.getIdentificationType();
        assertFalse(cardToken.validateIdentificationNumber(type));

        cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().getIdentification().setNumber("12345678901234567890");
        type = StaticMock.getIdentificationType();
        assertFalse(cardToken.validateIdentificationNumber(type));
    }

    @Test
    public void testIdentificationNumberNullIdType() {
        final CardToken cardToken = StaticMock.getCardToken();

        assertTrue(cardToken.validateIdentificationNumber(null));
    }

    @Test
    public void testIdentificationNumberNullCardholderValues() {
        CardToken cardToken;
        IdentificationType type;

        cardToken = StaticMock.getCardToken();
        cardToken.setCardholder(null);
        type = StaticMock.getIdentificationType();
        assertFalse(cardToken.validateIdentificationNumber(type));

        cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().setIdentification(null);
        type = StaticMock.getIdentificationType();
        assertFalse(cardToken.validateIdentificationNumber(type));

        cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().getIdentification().setNumber(null);
        type = StaticMock.getIdentificationType();
        assertFalse(cardToken.validateIdentificationNumber(type));
    }

    @Test
    public void testIdentificationNumberNullMinMaxLength() {
        CardToken cardToken;
        IdentificationType type;

        cardToken = StaticMock.getCardToken();
        type = StaticMock.getIdentificationType();
        type.setMinLength(null);
        assertTrue(cardToken.validateIdentificationNumber(type));

        cardToken = StaticMock.getCardToken();
        type = StaticMock.getIdentificationType();
        type.setMaxLength(null);
        assertTrue(cardToken.validateIdentificationNumber(type));
    }

    // * Cardholder name
    @Test
    public void testCardholderName() {
        final CardToken cardToken = StaticMock.getCardToken();

        assertTrue(cardToken.validateCardholderName());
    }

    @Test
    public void testCardholderNameEmpty() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().setName("");

        assertFalse(cardToken.validateCardholderName());
    }

    @Test
    public void testCardholderNameNull() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.getCardholder().setName(null);

        assertFalse(cardToken.validateCardholderName());
    }

    @Test
    public void testCardholderNameCardholderNull() {
        final CardToken cardToken = StaticMock.getCardToken();
        cardToken.setCardholder(null);

        assertFalse(cardToken.validateCardholderName());
    }

    // * Luhn
    @Test
    public void testLuhn() {
        assertTrue(CardToken.checkLuhn(StaticMock.DUMMY_CARD_NUMBER));
    }

    @Test
    public void testLuhnNullOrEmptyCardNumber() {
        assertFalse(CardToken.checkLuhn(null));
        assertFalse(CardToken.checkLuhn(""));
    }

    @Test
    public void testLuhnWrongCardNumber() {
        assertFalse(CardToken.checkLuhn("1111000000000000"));
    }
}