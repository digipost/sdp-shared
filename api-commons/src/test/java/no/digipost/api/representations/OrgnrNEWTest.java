package no.digipost.api.representations;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class OrgnrNEWTest {

	@Test
	public void fra_string_initializes_organisasjonsnummer() {
		String nummer = "984661185";
		OrgnrNEW organisasjonsnummer = OrgnrNEW.of(nummer);

		assertThat(organisasjonsnummer.toString()).isEqualTo(nummer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructor_throws_exception_if_not_valid() {
		String nummer = "98466118522222";
		OrgnrNEW organisasjonsnummer = OrgnrNEW.of(nummer);
	}

	@Test
	public void med_landkode_returns_organisasjosnummer_with9908_prefix() {
		String expected = "9908:984661185";
		OrgnrNEW organisasjonsnummer = OrgnrNEW.of("984661185");

		String actual = organisasjonsnummer.getOrganisasjonsnummerMedLandkode();

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void uten_landkode_returns_organisasjosnummer_without_prefix() {
		String expected = "984661185";
		OrgnrNEW organisasjonsnummer = OrgnrNEW.of(expected);

		String actual = organisasjonsnummer.getOrganisasjonsnummerUtenLandkode();

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void forfrem_til_avsender(){
		OrgnrNEW organisasjonsnummer = OrgnrNEW.of("984661185");
		AvsenderOrganisasjonsnummer avsenderOrganisasjonsnummer = organisasjonsnummer.forfremTilAvsender();
	}

	@Test
	public void forfrem_til_databehandler(){
		OrgnrNEW organisasjonsnummer = OrgnrNEW.of("984661185");
		DatabehandlerOrganisasjonsnummer databehandlerOrganisasjonsnummer = organisasjonsnummer.forfremTilDatabehandler();
	}


}