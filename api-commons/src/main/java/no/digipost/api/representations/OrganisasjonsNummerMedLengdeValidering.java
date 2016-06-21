package no.digipost.api.representations;

public class OrganisasjonsNummerMedLengdeValidering {

	private static int ORGANIZATION_NUMBER_LENGTH_NORWAY = 9;
	private static final String COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY = "9908";


	public final String organisasjonsnummer;

	public OrganisasjonsNummerMedLengdeValidering(String organisasjonsnummer) {
		throwIfInvalidOrganizationNumber(organisasjonsnummer);
		this.organisasjonsnummer = organisasjonsnummer;
	}

	private void throwIfInvalidOrganizationNumber(String organisasjonsNummer) {
		if (organisasjonsNummer.length() != ORGANIZATION_NUMBER_LENGTH_NORWAY) {
			throw new IllegalArgumentException(String.format("Organisasjonsnummer må ha en lengde på %d, men fikk inn '%s', som har en lengde på %d.",
					ORGANIZATION_NUMBER_LENGTH_NORWAY,
					organisasjonsNummer,
					organisasjonsNummer.length()));
		}
	}

	public String GetMedLankode() {
		return String.format("%s:%s", COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY, organisasjonsnummer);
	}

	public String GetUtenLandkode() {
		return organisasjonsnummer;
	}

	@Override
	public String toString() {
		return String.format("Organisasjosnnummer: %s", organisasjonsnummer);
	}
}
