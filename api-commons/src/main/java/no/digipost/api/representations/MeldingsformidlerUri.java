package no.digipost.api.representations;

import java.net.URI;

public class MeldingsformidlerUri {

	public final URI baseUri;
	private DatabehandlerOrganisasjonsNummer databehandlerOrganisasjonsnummer;

	public MeldingsformidlerUri(URI baseUri, DatabehandlerOrganisasjonsNummer databehandlerOrganisasjonsnummer){
		this.baseUri = baseUri;
		this.databehandlerOrganisasjonsnummer = databehandlerOrganisasjonsnummer;
	}

	public URI getFull(AvsenderOrganisasjonsNummer avsenderOrganisasjonsNummer){
		String uriWithOrganisasjonsnummer = String.format("%s/%s/%s",
				baseUri,
				databehandlerOrganisasjonsnummer.GetMedLankode(),
				avsenderOrganisasjonsNummer.GetMedLankode()
				);

		return URI.create(uriWithOrganisasjonsnummer);
	}
}
