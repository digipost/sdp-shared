package no.digipost.api.representations;

import org.junit.Test;

import java.net.URI;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;


public class MeldingsformidlerUriTest {

	@Test
	public void Constructor_InitializesWithBaseUri() {
		URI baseUri = URI.create("http://baseuri.no");
		DatabehandlerOrganisasjonsNummer databehandlerOrganisasjonsNummer = new DatabehandlerOrganisasjonsNummer("984661185");

		MeldingsformidlerUri meldingsformidlerUri = new MeldingsformidlerUri(baseUri, databehandlerOrganisasjonsNummer);

		assertThat(meldingsformidlerUri.baseUri).isEqualTo(baseUri);
	}

	@Test
	public void getFull_AppendsAvsenderAndDatabehandlerOganisasjonsnummerToBaseUri() {
		URI baseUri = URI.create("http://baseuri.no");
		DatabehandlerOrganisasjonsNummer databehandlerOrganisasjonsNummer = new DatabehandlerOrganisasjonsNummer("984661185");
		AvsenderOrganisasjonsNummer avsenderOrganisasjonsNummer = new AvsenderOrganisasjonsNummer("988015814");
		MeldingsformidlerUri meldingsformidlerUri = new MeldingsformidlerUri(baseUri, databehandlerOrganisasjonsNummer);

		String actual = meldingsformidlerUri.getFull(avsenderOrganisasjonsNummer).toString();

		assertThat(actual, containsString(avsenderOrganisasjonsNummer.GetMedLankode()));
		assertThat(actual, containsString(databehandlerOrganisasjonsNummer.GetMedLankode()));
	}
}