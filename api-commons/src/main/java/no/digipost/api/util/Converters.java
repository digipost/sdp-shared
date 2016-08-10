package no.digipost.api.util;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

public final class Converters {

    public static final Converter<LocalDate, DateTime> toDateTimeAtStartOfDay = new Converter<LocalDate, DateTime>() {
        @Override
        public DateTime apply(LocalDate date) {
            return date.toDateTimeAtStartOfDay();
        }
    };
    @SuppressWarnings("rawtypes")
    private static final Converter NOP = new Converter() {
        @Override
        public Object apply(Object value) {
            return value;
        }
    };

    private Converters() {
    }

    @SuppressWarnings("unchecked")
    public static <T> Converter<T, T> nop() {
        return NOP;
    }

    public static final Converter<LocalDate, DateTime> toDateTimeAfterStartOfDay(final Duration duration) {
        return new Converter<LocalDate, DateTime>() {
            @Override
            public DateTime apply(LocalDate date) {
                return date.toDateTimeAtStartOfDay().plus(duration);
            }
        };
    }

}
