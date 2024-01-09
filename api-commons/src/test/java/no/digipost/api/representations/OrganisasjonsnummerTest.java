package no.digipost.api.representations;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static no.digipost.api.representations.Organisasjonsnummer.COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY_NEW;
import static no.digipost.api.representations.Organisasjonsnummer.COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY_OLD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrganisasjonsnummerTest {

    @Test
    public void initializes_organisasjonsnummer_without_prefix() {
        String nummer = "984661185";
        Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of(nummer);

        assertThat(organisasjonsnummer.toString(), is(nummer));
    }

    @Test
    public void initializes_organisasjonsnummer_with_new_prefix() {
        String nummer = COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY_NEW + ":984661185";
        Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of(nummer);

        assertThat(organisasjonsnummer.getOrganisasjonsnummerMedLandkode(), is(nummer));
    }

    @Test
    public void initializes_organisasjonsnummer_with_old_prefix() {
        String nummer = COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY_OLD + ":984661185";
        Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of(nummer);

        assertThat(organisasjonsnummer.getOrganisasjonsnummerMedLandkode(), is(nummer));
    }

    @Test
    public void constructor_throws_exception_if_not_valid() {
        assertThrows(IllegalArgumentException.class, () -> Organisasjonsnummer.of("98466118522222"));
    }

    @Test
    public void invalid_prefix_with_length_4_throws_exception() {
        assertThrows(IllegalArgumentException.class, () -> Organisasjonsnummer.of("0000:984661185"));
    }

    @Test
    public void with_old_landkode_returns_return_organisasjosnummer_with_9908_prefix() {
        String expected = COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY_OLD + ":984661185";
        Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of("984661185");

        String actual = organisasjonsnummer.getOrganisasjonsnummerMedLandkode();

        assertThat(actual, is(expected));
    }

    @Test
    public void with_new_landkode_returns_organisasjosnummer_without_9908_prefix() {
        String source = COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY_NEW + ":984661185";
        String expected = "984661185";

        Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of(source);

        assertThat(organisasjonsnummer.getOrganisasjonsnummer(), is(expected));
    }

    @Test
    public void without_landkode_returns_organisasjosnummer_with_9908_prefix() {
        String source = "984661185";
        String expected = COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY_OLD + ":984661185";

        Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of(source);

        String actual = organisasjonsnummer.getOrganisasjonsnummerMedLandkode();

        assertThat(actual, is(expected));
    }

    @Test
    public void without_landkode_returns_organisasjosnummer_without_9908_prefix() {
        String expected = "984661185";
        Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of(expected);

        String actual = organisasjonsnummer.getOrganisasjonsnummer();

        assertThat(actual, is(expected));
        assertThat(Organisasjonsnummer.hvisGyldig(expected).get(), is(organisasjonsnummer));
    }

    @Test
    public void determine_if_is_one_of_multiple_organisasjonsnr() {
        String orgnr = "984661185";
        Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of(orgnr);
        assertTrue(organisasjonsnummer.erEnAv(Organisasjonsnummer.of("123456789"), Organisasjonsnummer.of(orgnr)));
        assertFalse(organisasjonsnummer.erEnAv(Organisasjonsnummer.of("123456789"), Organisasjonsnummer.of("987654321")));
    }

    @Test
    public void evaluates_string_with_or_without_authoroty_part_as_same() {
        Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of("984661185");
        assertTrue(organisasjonsnummer.er(organisasjonsnummer.getOrganisasjonsnummer()));
        assertTrue(organisasjonsnummer.er(organisasjonsnummer.getOrganisasjonsnummerMedLandkode()));
    }

    @Test
    public void without_authority_part_is_Same_as_with_authority_part() {
        Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of("0192:984661185");
        assertTrue(organisasjonsnummer.er(organisasjonsnummer.getOrganisasjonsnummer()));
    }

    @Test
    public void evaluates_other_strings_as_not_same() {
        Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of("984661185");
        assertFalse(organisasjonsnummer.er("xyz"));
        assertFalse(organisasjonsnummer.er("991825827"));
    }

    @Test
    public void evaluates_other_organisasjonsnummer_as_same() {
        Organisasjonsnummer withoutCountryCode = Organisasjonsnummer.of("984661185");
        Organisasjonsnummer withNewCountryCode = Organisasjonsnummer.of(COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY_NEW + ":984661185");
        Organisasjonsnummer withOldCountryCode = Organisasjonsnummer.of(COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY_OLD + ":984661185");
        Organisasjonsnummer notEqual = Organisasjonsnummer.of("123456789");
        assertTrue(withoutCountryCode.erSammeOrganisasjonsnummerUavhengigAvLandkode(withNewCountryCode));
        assertTrue(withoutCountryCode.erSammeOrganisasjonsnummerUavhengigAvLandkode(withOldCountryCode));
        assertTrue(withNewCountryCode.erSammeOrganisasjonsnummerUavhengigAvLandkode(withoutCountryCode));
        assertTrue(withNewCountryCode.erSammeOrganisasjonsnummerUavhengigAvLandkode(withOldCountryCode));
        assertTrue(withOldCountryCode.erSammeOrganisasjonsnummerUavhengigAvLandkode(withoutCountryCode));
        assertTrue(withOldCountryCode.erSammeOrganisasjonsnummerUavhengigAvLandkode(withNewCountryCode));
        assertFalse(notEqual.erSammeOrganisasjonsnummerUavhengigAvLandkode(withoutCountryCode));
        assertFalse(notEqual.erSammeOrganisasjonsnummerUavhengigAvLandkode(withNewCountryCode));
        assertFalse(notEqual.erSammeOrganisasjonsnummerUavhengigAvLandkode(withOldCountryCode));
    }

    @Test
    public void correct_equals_and_hashcode() {
        EqualsVerifier.forClass(Organisasjonsnummer.class).verify();
    }

    @Test
    public void invalid_orgnr_yields_empty_optional() {
        String invalid = "abc";
        assertThat(Organisasjonsnummer.hvisGyldig(invalid), is(Optional.empty()));
        assertFalse(Organisasjonsnummer.erGyldig(invalid));
    }

}