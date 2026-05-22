package com.yasirkhan.user.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class LicenseValidator {
    public static String getLicenseStatus(LocalDate licenseExpiry) {

        long daysUntilExpiry =
                ChronoUnit.DAYS.between(LocalDate.now(), licenseExpiry);

        if (daysUntilExpiry < 0) {
            return "EXPIRED";
        } else if (daysUntilExpiry <= 30) {
            return "EXPIRING_SOON";
        } else {
            return "VALID";
        }
    }
}
