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

import java.net.URI;

public class MeldingsformidlerUri {

	public final URI baseUri;
	private Organisasjonsnummer databehandlerOrganisasjonsnummer;

	public MeldingsformidlerUri(URI baseUri, Organisasjonsnummer databehandlerOrganisasjonsnummer){
		this.baseUri = baseUri;
		this.databehandlerOrganisasjonsnummer = databehandlerOrganisasjonsnummer;
	}

	public URI getFull(Organisasjonsnummer avsenderOrganisasjonsnummer){
		String uriWithOrganisasjonsnummer = String.format("%s/%s/%s",
				baseUri,
				databehandlerOrganisasjonsnummer.medLandkode(),
				avsenderOrganisasjonsnummer.medLandkode()
				);

		return URI.create(uriWithOrganisasjonsnummer);
	}
}
