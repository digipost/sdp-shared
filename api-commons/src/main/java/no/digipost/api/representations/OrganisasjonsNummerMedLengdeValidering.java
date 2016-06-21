/**
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
