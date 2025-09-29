package com.gondo.map.component.util;

import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static final class FORMATTER {
        public static final String YEAR_TO_MILLI_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    }

    public static DateTimeFormatter getDefaultFormatter() {
        return DateTimeFormatter.ofPattern(FORMATTER.YEAR_TO_MILLI_DEFAULT);
    }
}
