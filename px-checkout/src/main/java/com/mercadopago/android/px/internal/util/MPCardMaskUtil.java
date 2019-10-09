package com.mercadopago.android.px.internal.util;

import com.mercadopago.android.px.model.IdentificationType;

public final class MPCardMaskUtil {

    private static final String BASE_FRONT_SECURITY_CODE = "••••";
    private static final int CPF_SEPARATOR_AMOUNT = 3;
    private static final int CNPJ_SEPARATOR_AMOUNT = 4;
    private static final int LAST_DIGITS_LENGTH = 4;
    private static final char HIDDEN_NUMBER_CHAR = "•".charAt(0);

    public static final int CARD_NUMBER_MAX_LENGTH = 16;
    private static final int CARD_NUMBER_AMEX_LENGTH = 15;
    private static final int CARD_NUMBER_DINERS_LENGTH = 14;
    private static final int CARD_NUMBER_MAESTRO_SETTING_1_LENGTH = 18;
    private static final int CARD_NUMBER_MAESTRO_SETTING_2_LENGTH = 19;

    private static final String IDENTIFICATION_TYPE_CPF = "CPF";
    private static final String IDENTIFICATION_TYPE_CNPJ = "CNPJ";

    private MPCardMaskUtil() {
    }

    public static String getCardNumberHidden(final int cardNumberLength, final String lastFourDigits) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cardNumberLength - LAST_DIGITS_LENGTH; i++) {
            sb.append(HIDDEN_NUMBER_CHAR);
        }
        sb.append(lastFourDigits);
        return buildNumberWithMask(cardNumberLength, sb.toString());
    }

    public static String buildNumberWithMask(final int cardLength, final String number) {
        String result = "";
        if (cardLength == CARD_NUMBER_AMEX_LENGTH) {
            final StringBuffer mask = new StringBuffer();
            for (int i = 0; i < 4; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            mask.append(" ");
            for (int i = 4; i < 10; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            mask.append(" ");
            for (int i = 10; i < CARD_NUMBER_AMEX_LENGTH; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            result = mask.toString();
        } else if (cardLength == CARD_NUMBER_DINERS_LENGTH) {
            final StringBuffer mask = new StringBuffer();
            for (int i = 0; i < 4; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            mask.append(" ");
            for (int i = 4; i < 10; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            mask.append(" ");
            for (int i = 10; i < CARD_NUMBER_DINERS_LENGTH; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            result = mask.toString();
        } else if (cardLength == CARD_NUMBER_MAESTRO_SETTING_1_LENGTH) {
            final StringBuffer mask = new StringBuffer();
            for (int i = 0; i < 10; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            mask.append(" ");
            for (int i = 10; i < 15; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            mask.append(" ");
            for (int i = 15; i < CARD_NUMBER_MAESTRO_SETTING_1_LENGTH; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            result = mask.toString();
        } else if (cardLength == CARD_NUMBER_MAESTRO_SETTING_2_LENGTH) {
            final StringBuffer mask = new StringBuffer();
            for (int i = 0; i < 9; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            mask.append(" ");
            for (int i = 9; i < CARD_NUMBER_MAESTRO_SETTING_2_LENGTH; i++) {
                final char c = getCharOfCard(number, i);
                mask.append(c);
            }
            result = mask.toString();
        } else {
            final StringBuffer mask = new StringBuffer();
            for (int i = 1; i <= cardLength; i++) {
                mask.append(getCharOfCard(number, i - 1));
                if (i % 4 == 0) {
                    mask.append(" ");
                }
            }
            result = mask.toString();
        }
        return result;
    }

    public static char getCharOfCard(final String number, final int i) {
        if (i < number.length()) {
            return number.charAt(i);
        }
        return "•".charAt(0);
    }

    public static String buildIdentificationNumberWithMask(final CharSequence number,
        final IdentificationType identificationType) {
        if (identificationType != null && identificationType.getId() != null) {
            final String type = identificationType.getId();
            if (type.equals(IDENTIFICATION_TYPE_CPF)) {
                return buildIdentificationNumberOfTypeCPF(number, identificationType.getMaxLength());
            } else if (type.equals(IDENTIFICATION_TYPE_CNPJ)) {
                return buildIdentificationNumberOfTypeCNPJ(number, identificationType.getMaxLength());
            }
        }
        return buildIdentificationNumberWithDecimalSeparator(number);
    }

    public static String buildIdentificationNumberWithDecimalSeparator(final CharSequence number) {
        if (number.length() == 0) {
            return number.toString();
        }
        return getMaskedNumberWithDecimalSymbols(number.toString());
    }

    private static String getMaskedNumberWithDecimalSymbols(final String idNumber) {
        final StringBuilder maskBuilder = new StringBuilder();

        final String nonNumericRegex = "(\\D(.*))";
        final String decimalsSymbolRegex = "(\\d)(?=(\\d{3})+$)";

        final String onlyNumbers = idNumber.replaceAll(nonNumericRegex, "");

        //Add decimal symbol to numbers
        maskBuilder.append(onlyNumbers.replaceAll(decimalsSymbolRegex, "$1."));

        //Append anything after numeric prefix
        maskBuilder.append(idNumber.replace(onlyNumbers, ""));

        return maskBuilder.toString();
    }

    public static String buildIdentificationNumberOfTypeCPF(final CharSequence number, final int maxLength) {
        String result = "";
        final StringBuffer identificationNumber = new StringBuffer();
        for (int i = 0; i < (maxLength + CPF_SEPARATOR_AMOUNT) && i < number.length(); i++) {
            if (i == 3 || i == 6) {
                identificationNumber.append(".");
            } else if (i == 9) {
                identificationNumber.append("-");
            }
            identificationNumber.append(number.charAt(i));
        }
        result = identificationNumber.toString();
        return result;
    }

    public static String buildIdentificationNumberOfTypeCNPJ(final CharSequence number, final int maxLength) {
        String result = "";
        final StringBuffer identificationNumber = new StringBuffer();
        for (int i = 0; i < (maxLength + CNPJ_SEPARATOR_AMOUNT) && i < number.length(); i++) {
            if (i == 2 || i == 5) {
                identificationNumber.append(".");
            } else if (i == 8) {
                identificationNumber.append("/");
            } else if (i == 12) {
                identificationNumber.append("-");
            }
            identificationNumber.append(number.charAt(i));
        }
        result = identificationNumber.toString();
        return result;
    }

    public static String buildSecurityCode(final int securityCodeLength, final String code) {
        final StringBuffer securityCode = new StringBuffer();
        if (code == null || code.isEmpty()) {
            return BASE_FRONT_SECURITY_CODE;
        }
        for (int i = 0; i < securityCodeLength; i++) {
            final char charOfCard = getCharOfCard(code, i);
            securityCode.append(charOfCard);
        }
        return securityCode.toString();
    }

    public static boolean needsMask(final CharSequence currentNumber, final int cardNumberLength) {

        if (cardNumberLength == CARD_NUMBER_MAESTRO_SETTING_1_LENGTH) {

            return currentNumber.length() == 10 || currentNumber.length() == 16;
        } else if (cardNumberLength == CARD_NUMBER_MAESTRO_SETTING_2_LENGTH) {

            return currentNumber.length() == 9;
        } else if (cardNumberLength == CARD_NUMBER_AMEX_LENGTH || cardNumberLength == CARD_NUMBER_DINERS_LENGTH) {
            return currentNumber.length() == 4 || currentNumber.length() == 11;
        } else {
            return currentNumber.length() == 4 || currentNumber.length() == 9 || currentNumber.length() == 14;
        }
    }

    public static boolean isDefaultSpaceErasable(final int currentNumberLength) {
        return currentNumberLength < 6;
    }
}
