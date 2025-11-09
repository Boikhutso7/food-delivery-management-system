package com.foodDelivery.util;

import java.util.regex.Pattern;

public class InputValidator {

    // Allows letters, spaces, hyphens, and apostrophes. Must be at least 2 chars.
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} .'-]{2,}$");

    // Allows 10 digits
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");

    // Simple check for an address, ensuring it's not just whitespace and has some length
    private static final int MIN_ADDRESS_LENGTH = 5;

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        return address.trim().length() >= MIN_ADDRESS_LENGTH;
    }
}
