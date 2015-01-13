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

import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.difi.begrep.sdp.schema_v10.SDPDigitalPostInfo;
import no.difi.begrep.sdp.schema_v10.SDPFlyttetDigitalPost;
import no.difi.begrep.sdp.schema_v10.SDPFysiskPostInfo;
import no.digipost.api.representations.SimpleStandardBusinessDocument.SimpleDigitalPostformidling;
import no.digipost.api.representations.SimpleStandardBusinessDocument.SimpleDigitalPostformidling.Type;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static no.digipost.api.representations.SimpleStandardBusinessDocument.SimpleDigitalPostformidling.Type.NY_POST;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;


public class SimpleDigitalPostTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private final SimpleDigitalPostformidling nyPost = new SimpleDigitalPostformidling(new SDPDigitalPost());
	private final SimpleDigitalPostformidling tilFlytting = new SimpleDigitalPostformidling(new SDPFlyttetDigitalPost());

	@Test
    public void kanIkkeHenteUtFlyttetDigitalPostNaarTypeErNY_POST() {
		assertThat(nyPost.type, is(Type.NY_POST));
		expectedException.expect(IllegalArgumentException.class);
		nyPost.getFlyttetDigitalPost();
    }

	@Test
	public void kanIkkeHenteUtDigitalPostNaarTypeErFLYTTET() {
		assertThat(tilFlytting.type, is(Type.FLYTTET));
		expectedException.expect(IllegalArgumentException.class);
		tilFlytting.getDigitalPost();
	}

	@Test
    public void kanHenteUtUlikeDigitalPostMeldinger() {
		assertNotNull(nyPost.getDigitalPost());
		assertNotNull(tilFlytting.getFlyttetDigitalPost());
    }

	@Test
	public void leveringsdatoSkalSettesNaarSattOgTypeErNY_POST() {
		LocalDate leveringsdato = LocalDate.now().plusDays(7);

		SimpleDigitalPostformidling digitalPostformidling = new SimpleDigitalPostformidling(
				new SDPDigitalPost()
						.withDigitalPostInfo(new SDPDigitalPostInfo()
								.withVirkningsdato(leveringsdato)));

		assertThat(digitalPostformidling.getLeveringstidspunkt(), is(leveringsdato.toDateTimeAtStartOfDay()));
	}

	@Test
	public void leveringsdatoSkalIkkeSettesNaasIkkeSattOgTypeErNY_POST() {
		SimpleDigitalPostformidling digitalPostformidling = new SimpleDigitalPostformidling(
				new SDPDigitalPost()
						.withDigitalPostInfo(new SDPDigitalPostInfo()));

		assertThat(digitalPostformidling.getLeveringstidspunkt(), is(nullValue()));
	}

	@Test
	public void leveringsdatoSkalSettesNaarSattOgTypeErFLYTTET() {
		LocalDate virkningsdato = LocalDate.now().plusDays(7);

		SimpleDigitalPostformidling digitalPostformidling = new SimpleDigitalPostformidling(
				new SDPFlyttetDigitalPost()
						.withMottaksdato(virkningsdato.minusDays(1))
						.withDigitalPostInfo(new SDPDigitalPostInfo().withVirkningsdato(virkningsdato)));

		assertThat(digitalPostformidling.getLeveringstidspunkt(), is(virkningsdato.toDateTimeAtStartOfDay()));
	}

	@Test
	public void leveringstidspunktErAlltidMottakstidspunktHvisVirkningsdatoIkkeErSattOgTypeErFLYTTET() {
		LocalDate mottaksdato = LocalDate.now().minusDays(7);
		SimpleDigitalPostformidling digitalPostformidling = new SimpleDigitalPostformidling(
				new SDPFlyttetDigitalPost()
						.withMottaksdato(mottaksdato)
						.withDigitalPostInfo(new SDPDigitalPostInfo()));

		assertThat(digitalPostformidling.getLeveringstidspunkt(), is(mottaksdato.toDateTimeAtStartOfDay()));
	}

	@Test
	public void leveringstidspunktErVirkningsdatoDersomDenErSenereEnnMottakstidspunktOgTypeErFLYTTET() {
		LocalDate virkningsdato = LocalDate.now().minusDays(7);
		SimpleDigitalPostformidling digitalPostformidling = new SimpleDigitalPostformidling(
				new SDPFlyttetDigitalPost()
						.withMottaksdato(virkningsdato.minusDays(1))
						.withDigitalPostInfo(new SDPDigitalPostInfo().withVirkningsdato(virkningsdato)));

		assertThat(digitalPostformidling.getLeveringstidspunkt(), is(virkningsdato.toDateTimeAtStartOfDay()));
	}

	@Test
	public void leveringsdatoSkalVaereMottattDatoHvisSatt() {
		LocalDate mottaksdato = LocalDate.now().minusDays(10);

		SimpleDigitalPostformidling digitalPostformidling = new SimpleDigitalPostformidling(
				new SDPFlyttetDigitalPost()
						.withDigitalPostInfo(new SDPDigitalPostInfo())
						.withMottaksdato(mottaksdato));

		assertThat(digitalPostformidling.getLeveringstidspunkt(), is(mottaksdato.toDateTimeAtStartOfDay()));
	}


	@Test
	public void leveringsdatoSkalVaereMottattDatoHvisMottattDatoErEtterVirkningsdato() {
		LocalDate mottaksdato = LocalDate.now().plusDays(8);
		LocalDate virkningsdato = LocalDate.now().plusDays(7);

		SimpleDigitalPostformidling digitalPostformidling = new SimpleDigitalPostformidling(
				new SDPFlyttetDigitalPost()
						.withDigitalPostInfo(new SDPDigitalPostInfo()
								.withVirkningsdato(virkningsdato))
						.withMottaksdato(mottaksdato));

		assertThat(digitalPostformidling.getLeveringstidspunkt(), is(mottaksdato.toDateTimeAtStartOfDay()));
	}

	@Test
	public void leveringsdatoSkalVaereVirkningsDatoHvisVirkningsdatoEtterMottattDato() {
		LocalDate mottaksdato = LocalDate.now();
		LocalDate virkningsdato = LocalDate.now().plusDays(7);

		SimpleDigitalPostformidling digitalPostformidling = new SimpleDigitalPostformidling(
				new SDPFlyttetDigitalPost()
						.withDigitalPostInfo(new SDPDigitalPostInfo()
								.withVirkningsdato(virkningsdato))
						.withMottaksdato(mottaksdato));

		assertThat(digitalPostformidling.getLeveringstidspunkt(), is(virkningsdato.toDateTimeAtStartOfDay()));
	}

	@Test
	public void vanligDigitalPostErAldriAapnet() {
	    assertFalse(nyPost.erAlleredeAapnet());
    }

	@Test
    public void flyttetPostKanVaereAapnetEllerUaapnet() {
		assertFalse(tilFlytting.erAlleredeAapnet());
		assertTrue(new SimpleDigitalPostformidling(new SDPFlyttetDigitalPost().withAapnet(true)).erAlleredeAapnet());
    }

	@Test
	public void erIkkeFysiskPostDersomFysiskPostInfoIkkeErSatt() {
		assertFalse(nyPost.erDigitalPostTilFysiskLevering());
		assertFalse(tilFlytting.erDigitalPostTilFysiskLevering());
	}

	@Test
	public void gjenkjennerDigitalPostTilFysiskLevering() {
		SimpleDigitalPostformidling fysiskPost = new SimpleDigitalPostformidling(new SDPDigitalPost().withFysiskPostInfo(new SDPFysiskPostInfo()));
		assertTrue(fysiskPost.erDigitalPostTilFysiskLevering());
		assertTrue(fysiskPost.type == NY_POST);
	}

}
