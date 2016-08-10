
package no.digipost.api.representations;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class OrganisasjonsnummerTest {

	@Test
	public void initializes_organisasjonsnummer() {
		String nummer = "984661185";
		Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of(nummer);

		assertThat(organisasjonsnummer.toString(), is(nummer));
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructor_throws_exception_if_not_valid() {
		Organisasjonsnummer.of("98466118522222");
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalid_prefix_with_length_4_throws_exception() {
		Organisasjonsnummer.of("0000:984661185");
	}

	@Test
	public void with_landkode_returns_return_organisasjosnummer_with_9908_prefix() {
		String expected = "9908:984661185";
		Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of("984661185");

		String actual = organisasjonsnummer.getOrganisasjonsnummerMedLandkode();

		assertThat(actual, is(expected));
	}

	@Test
	public void with_landkode_returns_organisasjosnummer_without_9908_prefix() {
		String source = "9908:984661185";
		String expected = "984661185";

		Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of(source);

		assertThat(organisasjonsnummer.getOrganisasjonsnummer(), is(expected));
	}

	@Test
	public void without_landkode_returns_organisasjosnummer_with_9908_prefix() {
		String source = "984661185";
		String expected = "9908:984661185";

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
	public void evaluates_other_strings_as_not_same() {
	    Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.of("984661185");
	    assertFalse(organisasjonsnummer.er("xyz"));
	    assertFalse(organisasjonsnummer.er("991825827"));
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