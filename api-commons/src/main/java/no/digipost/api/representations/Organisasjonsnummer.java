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

import no.digipost.api.PMode;
import org.apache.commons.lang3.ArrayUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Organisasjonsnummer {

	public static final Pattern ISO6523_PATTERN = Pattern.compile("^([0-9]{4}:)?([0-9]{9})$");
	public static final Organisasjonsnummer NULL = new Organisasjonsnummer("");
	public static final String ISO6523_ACTORID = PMode.PARTY_ID_TYPE;

	private static final String COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY = "9908";

	private final String organisasjonsnummer;

	private Organisasjonsnummer(final String organisasjonsnummer) {
		this.organisasjonsnummer = organisasjonsnummer;
	}

	public String medLandkode() {
		return String.format("%s:%s", COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY, organisasjonsnummer);
	}

	public String utenLandkode() {
		return organisasjonsnummer;
	}

	public static boolean isOrganisasjonsnummer(final String organisasjosnummer) {
		return ISO6523_PATTERN.matcher(organisasjosnummer).matches();
	}

	public static Organisasjonsnummer of(final String organisasjonsnummer) {
		Matcher matcher = ISO6523_PATTERN.matcher(organisasjonsnummer);
		if (!matcher.matches()) {
			throw new IllegalArgumentException(
					String.format("Ugyldig organisasjonsnummer. Forventet format er ISO 6523, men fikk følgende nummer: '%s'. " +
							"Organisasjonsnummeret skal være 9 siffer og kan prefikses med landkode 9908. " +
							"Eksempler på dette er '9908:984661185' og '984661185'.",
							organisasjonsnummer)
			);
		}
		return new Organisasjonsnummer(matcher.group(2));
	}

	@Override
	public String toString() {
		return utenLandkode();
	}

    @Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof Organisasjonsnummer) {
			return organisasjonsnummer.equals(((Organisasjonsnummer)obj).organisasjonsnummer);
		}
		return false;
	}

    @Override
	public int hashCode() {
		return organisasjonsnummer.hashCode();
	}

	public boolean oneOf(Organisasjonsnummer ... candidates) {
		return ArrayUtils.contains(candidates, this);
	}


}
