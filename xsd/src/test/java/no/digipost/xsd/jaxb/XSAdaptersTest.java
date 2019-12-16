package no.digipost.xsd.jaxb;

import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class XSAdaptersTest {

    private final XSDateTimeAdapter dateTimeAdapter = new XSDateTimeAdapter();

    @Test
    public void datetime_marshall_unmarshall_roundtrip_yields_equal_objects() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("GMT-8")).withNano(0);
        assertThat(dateTimeAdapter.unmarshal(dateTimeAdapter.marshal(now)), is(now));
    }

    @Test
    public void unmarshall_yields_datetime_with_region_based_zoneId_replaced_with_GMT_offset() {
        ZoneId newYorkZone = ZoneId.of("America/New_York");
        ZonedDateTime rightNowInAmerica = ZonedDateTime.now(newYorkZone).withNano(0);
        String xmlDateTimeString = dateTimeAdapter.marshal(rightNowInAmerica);

        boolean daylightSavings = newYorkZone.getRules().isDaylightSavings(rightNowInAmerica.toInstant());
        ZoneId gmtZone = daylightSavings ? ZoneId.of("GMT-4") : ZoneId.of("GMT-5");
        assertThat(dateTimeAdapter.unmarshal(xmlDateTimeString), is(rightNowInAmerica.withZoneSameInstant(gmtZone)));
    }

    @Test
    public void dateTimeAdapter_is_null_safe() {
        assertThat(dateTimeAdapter.marshal(null), nullValue());
        assertThat(dateTimeAdapter.unmarshal(null), nullValue());

    }


    private final XSDateAdapter dateAdapter = new XSDateAdapter();

    @Test
    public void date_marshall_unmarshall_roundtrip_yields_equal_objects() {
        LocalDate today = LocalDate.now();
        assertThat(dateAdapter.unmarshal(dateAdapter.marshal(today)), is(today));
    }

    @Test
    public void dateAdater_is_null_safe() {
        assertThat(dateAdapter.marshal(null), nullValue());
        assertThat(dateAdapter.unmarshal(null), nullValue());
    }

}
