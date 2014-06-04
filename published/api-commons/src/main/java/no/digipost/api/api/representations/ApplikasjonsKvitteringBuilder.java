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
package no.digipost.api.api.representations;

import no.difi.begrep.sdp.schema_v10.SDPAapning;
import no.difi.begrep.sdp.schema_v10.SDPFeil;
import no.difi.begrep.sdp.schema_v10.SDPFeiltype;
import no.difi.begrep.sdp.schema_v10.SDPKvittering;
import no.difi.begrep.sdp.schema_v10.SDPLevering;
import no.difi.begrep.sdp.schema_v10.SDPMelding;
import org.joda.time.DateTime;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

public class ApplikasjonsKvitteringBuilder {

	private EbmsAktoer avsender;
	private String instanceIdentifier;
	private String messageId;
	private String conversationId;
	private static EbmsAktoer ebmsMottaker;
	private static Organisasjonsnummer sbdhMottaker;

	private SDPMelding kvittering = new SDPKvittering()
		.withLevering(new SDPLevering())
		.withTidspunkt(DateTime.now());

	public static ApplikasjonsKvitteringBuilder create(final EbmsAktoer avsender, final EbmsAktoer ebmsMottaker, final Organisasjonsnummer sbdhMottaker, final String messageId,
	                                                    final String conversationId, final String instanceIdentifier) {
		ApplikasjonsKvitteringBuilder.ebmsMottaker = ebmsMottaker;
		ApplikasjonsKvitteringBuilder.sbdhMottaker = sbdhMottaker;
		ApplikasjonsKvitteringBuilder builder = new ApplikasjonsKvitteringBuilder();
		builder.messageId = messageId;
		builder.avsender = avsender;
		builder.conversationId = conversationId;
		builder.instanceIdentifier = instanceIdentifier;
		return builder;
	}

    public ApplikasjonsKvitteringBuilder medFeil(final String feilinformasjon) {
		kvittering = new SDPFeil()
			.withFeiltype(SDPFeiltype.KLIENT)
			.withDetaljer(feilinformasjon);
		return this;
	}

	public ApplikasjonsKvitteringBuilder medAapning() {
		kvittering = new SDPKvittering()
			.withAapning(new SDPAapning())
			.withTidspunkt(DateTime.now());
		return this;
	}

	public EbmsApplikasjonsKvittering build() {
		final StandardBusinessDocument doc = StandardBusinessDocumentFactory
				.create(avsender.orgnr, sbdhMottaker, instanceIdentifier, conversationId, kvittering);
		return EbmsApplikasjonsKvittering.create(avsender, ebmsMottaker, doc).withMessageId(messageId).build();
	}
}
