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

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class OrganisasjonsnummerTest {

	@Test
	public void fraString_InitializesOrganisasjonsnummer() {
		String nummer = "984661185";
		Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.fraString(nummer);

		assertThat(organisasjonsnummer.toString()).isEqualTo(nummer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void Constructor_ThrowsExceptionIfNotValid() {
		String nummer = "98466118522222";
		Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.fraString(nummer);
	}

	@Test
	public void medLandkode_ReturnsOrganisasjosnummerWith9908Prefix() {
		String expected = "9908:984661185";
		Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.fraString("984661185");

		String actual = organisasjonsnummer.medLandkode();

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void utenLandkode_ReturnsOrganisasjosnummerWithoutPrefix() {
		String expected = "984661185";
		Organisasjonsnummer organisasjonsnummer = Organisasjonsnummer.fraString(expected);

		String actual = organisasjonsnummer.utenLandkode();

		assertThat(actual).isEqualTo(expected);
	}

}