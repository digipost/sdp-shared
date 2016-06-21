package no.digipost.api.representations;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class OrganisasjonsnummerTest {

	@Test
	public void Constructor_InitializesOrganisasjonsnummer() {
		String organisasjonsnummer = "organisasjonsnummer";
		OrganisasjonsNummerMedLengdeValidering Organisasjonsnummer = new OrganisasjonsNummerMedLengdeValidering(organisasjonsnummer);

		assertThat(Organisasjonsnummer.organisasjonsnummer).isEqualTo(organisasjonsnummer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void Constructor_ThrowsExceptionIfNotValid() {
		String organisasjonsnummer = "984661185";
		OrganisasjonsNummerMedLengdeValidering Organisasjonsnummer = new OrganisasjonsNummerMedLengdeValidering(organisasjonsnummer);
	}

	@Test
	public void GetMedLandkode_ReturnsOrganisasjosnummerWith9908Prefix() {
		String expected = "9908:organisasjonsnummer";
		OrganisasjonsNummerMedLengdeValidering organisasjonsnummer = new OrganisasjonsNummerMedLengdeValidering("organisasjonsnummer");

		String actual = organisasjonsnummer.GetMedLankode();

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void GetUtenLandkode_ReturnsOrganisasjosnummerWithoutPrefix() {
		String expected = "organisasjonsnummer";
		OrganisasjonsNummerMedLengdeValidering organisasjonsnummer = new OrganisasjonsNummerMedLengdeValidering(expected);

		String actual = organisasjonsnummer.GetUtenLandkode();

		assertThat(actual).isEqualTo(expected);
	}

}