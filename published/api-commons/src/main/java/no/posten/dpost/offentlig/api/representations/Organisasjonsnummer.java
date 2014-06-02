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
package no.posten.dpost.offentlig.api.representations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Organisasjonsnummer {
	private final String orgNummer;
	public static final String ISO6523_ACTORID = "iso6523-actorid-upis";

	public static final Organisasjonsnummer NULL = new Organisasjonsnummer("");

	public Organisasjonsnummer(final String orgNummer) {
		this.orgNummer = orgNummer;
	}

	public String asIso6523() {
		return "9908:" + orgNummer;
	}

	@Override
	public String toString() {
		return orgNummer;
	}

	public static Organisasjonsnummer fromIso6523(final String iso6523Orgnr) {
		Pattern pattern = Pattern.compile("^([0-9]{4}:)?([0-9]{9})$");
		Matcher matcher = pattern.matcher(iso6523Orgnr);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid PartyInfo:" + iso6523Orgnr);
		}
		return new Organisasjonsnummer(matcher.group(2));
	}
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Organisasjonsnummer) {
			return orgNummer.equals(((Organisasjonsnummer)obj).orgNummer);
		}
		return false;
	}
	@Override
	public int hashCode() {
		return orgNummer.hashCode();
	}
}
