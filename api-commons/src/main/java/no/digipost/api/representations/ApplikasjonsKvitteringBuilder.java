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

import no.difi.begrep.sdp.schema_v10.SDPAapning;
import no.difi.begrep.sdp.schema_v10.SDPFeil;
import no.difi.begrep.sdp.schema_v10.SDPFeiltype;
import no.difi.begrep.sdp.schema_v10.SDPKvittering;
import no.difi.begrep.sdp.schema_v10.SDPLevering;
import no.difi.begrep.sdp.schema_v10.SDPMelding;
import no.difi.begrep.sdp.schema_v10.SDPVarslingfeilet;
import no.difi.begrep.sdp.schema_v10.SDPVarslingskanal;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import static org.joda.time.DateTime.now;

public class ApplikasjonsKvitteringBuilder {

	private EbmsAktoer avsender;
	private String instanceIdentifier;
	private String messageId;
	private String conversationId;
	private EbmsAktoer ebmsMottaker;
	private Organisasjonsnummer sbdhMottaker;
	private EbmsOutgoingMessage.Prioritet prioritet = EbmsOutgoingMessage.Prioritet.NORMAL;

	private SDPMelding kvittering = new SDPKvittering()
			.withLevering(new SDPLevering())
			.withTidspunkt(now());

	public static ApplikasjonsKvitteringBuilder create(final EbmsAktoer avsender, final EbmsAktoer ebmsMottaker, final Organisasjonsnummer sbdhMottaker, final String messageId,
	                                                   final String conversationId, final String instanceIdentifier) {
		ApplikasjonsKvitteringBuilder builder = new ApplikasjonsKvitteringBuilder();
		builder.ebmsMottaker = ebmsMottaker;
		builder.sbdhMottaker = sbdhMottaker;
		builder.messageId = messageId;
		builder.avsender = avsender;
		builder.conversationId = conversationId;
		builder.instanceIdentifier = instanceIdentifier;
		return builder;
	}

	public ApplikasjonsKvitteringBuilder medPrioritet(EbmsOutgoingMessage.Prioritet prioritet) {
		this.prioritet = prioritet;
		return this;
	}

	public ApplikasjonsKvitteringBuilder medFeil(SDPFeiltype feiltype, final String feilinformasjon) {
		kvittering = new SDPFeil()
				.withFeiltype(feiltype)
				.withDetaljer(feilinformasjon)
				.withTidspunkt(now());
		return this;
	}

	public ApplikasjonsKvitteringBuilder medAapning() {
		kvittering = new SDPKvittering()
				.withAapning(new SDPAapning())
				.withTidspunkt(now());
		return this;
	}

	public ApplikasjonsKvitteringBuilder medVarslingfeilet(SDPVarslingskanal varslingskanal, String beskrivelse) {
		kvittering = new SDPKvittering()
				.withVarslingfeilet(new SDPVarslingfeilet()
						.withBeskrivelse(beskrivelse)
						.withVarslingskanal(varslingskanal))
				.withTidspunkt(now());
		return this;
	}

	public EbmsApplikasjonsKvittering build() {
		final StandardBusinessDocument doc = StandardBusinessDocumentFactory
				.create(avsender.orgnr, sbdhMottaker, instanceIdentifier, conversationId, kvittering);
		return EbmsApplikasjonsKvittering.create(avsender, ebmsMottaker, doc).withMessageId(messageId).withPrioritet(prioritet).build();
	}
}
