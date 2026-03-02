package com.ai.tester.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public final class DateUtil {

    private DateUtil() {
    }

    public static String epochMillisToDateString(long epochMillis) {
        return Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .toString();
    }

    public static long dateStringToEpochMillis(String dateString) {
        return LocalDate.parse(dateString)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli();
    }
}

