package no.digipost.api.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class Converters {

    public static final Converter<LocalDate, ZonedDateTime> toDateTimeAtStartOfDay = date -> date.atStartOfDay(ZoneId.systemDefault());

    @SuppressWarnings("rawtypes")
    private static final Converter NOP = value -> value;

    @SuppressWarnings("unchecked")
    public static <T> Converter<T, T> nop() {
        return NOP;
    }

    public static final Converter<LocalDate, ZonedDateTime> toDateTimeAfterStartOfDay(Duration duration) {
        return date -> date.atStartOfDay(ZoneId.systemDefault()).plus(duration);
    }

    private Converters() { }

}
