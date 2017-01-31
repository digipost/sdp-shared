package no.digipost.api.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static no.digipost.api.util.Choice.choice;
import static no.digipost.api.util.Converters.toDateTimeAtStartOfDay;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class ChoiceTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void throwsExceptionIfBothAreNonNull() {
        expectedException.expect(IllegalArgumentException.class);
        choice(ZonedDateTime.now(), LocalDate.now());
    }


    @Test
    public void bothNullsYieldsDateTimeAsNull() {
        assertThat(choice(null, null), nullValue());
    }


    @Test
    public void firstInstancePresent() {
        assertThat(choice("a", null), is("a"));
    }

    @Test
    public void secondInstancePresent() {
        assertThat(choice(null, "a"), is("a"));
    }


    @Test
    public void convertSecondInstance() {
        LocalDate today = LocalDate.now();
        assertThat(choice((ZonedDateTime) null, today, toDateTimeAtStartOfDay), is(today.atStartOfDay(ZoneId.systemDefault())));
    }
}
