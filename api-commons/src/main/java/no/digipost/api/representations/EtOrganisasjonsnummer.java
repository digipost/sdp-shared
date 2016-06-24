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

import org.apache.commons.lang3.ArrayUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class EtOrganisasjonsnummer implements AvsenderOrganisasjonsnummer, DatabehandlerOrganisasjonsnummer  {

	private static final String COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY = "9908";

	public static final Pattern ORGANIZATION_NUMBER_PATTERN = Pattern.compile("^(" + COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY +":)?([0-9]{9})$");

	private String organisasjonsnummer;

	EtOrganisasjonsnummer(String organisasjonsnummer){
		this.organisasjonsnummer =  getOrThrowIfInvalid(organisasjonsnummer);
	}

	private String getOrThrowIfInvalid(final String organisasjonsnummer) {
		Matcher matcher = ORGANIZATION_NUMBER_PATTERN.matcher(organisasjonsnummer);

		if (!matcher.matches()) {
			throw new IllegalArgumentException(
					String.format("Ugyldig organisasjonsnummer. Forventet format er ISO 6523, men fikk følgende nummer: '%s'. " +
									"Organisasjonsnummeret skal være 9 siffer og kan prefikses med landkode 9908. " +
									"Eksempler på dette er '9908:984661185' og '984661185'.",
							organisasjonsnummer)
			);
		}

		int groupOfOrganizationNumber= matcher.groupCount();

		return matcher.group(groupOfOrganizationNumber);
	}

	@Override
	public String getOrganisasjonsnummer() {
		return organisasjonsnummer;
	}

	@Override
	public String getOrganisasjonsnummerMedLandkode() {
		return String.format("%s:%s", COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY, organisasjonsnummer);
	}

	@Override
	public String toString() {
		return getOrganisasjonsnummer();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof EtOrganisasjonsnummer) {
			return getOrganisasjonsnummerMedLandkode().equals(((EtOrganisasjonsnummer)obj).getOrganisasjonsnummerMedLandkode());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return organisasjonsnummer.hashCode();
	}

	public boolean oneOf(Organisasjonsnummer... candidates) {
		return ArrayUtils.contains(candidates, this);
	}

	@Override
	public DatabehandlerOrganisasjonsnummer forfremTilDatabehandler() {
		return this;
	}

	@Override
	public AvsenderOrganisasjonsnummer forfremTilAvsender() {
		return this;
	}
}
