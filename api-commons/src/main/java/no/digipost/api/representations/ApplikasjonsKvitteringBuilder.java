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

import no.difi.begrep.sdp.schema_v10.*;
import no.digipost.api.PMode;

import org.joda.time.DateTime;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

public class ApplikasjonsKvitteringBuilder {

	private DateTime creationTime;
	private EbmsAktoer avsender;
	private String instanceIdentifier;
	private String messageId;
	private String conversationId;
	private EbmsAktoer ebmsMottaker;
	private Organisasjonsnummer sbdhMottaker;
	private EbmsOutgoingMessage.Prioritet prioritet = EbmsOutgoingMessage.Prioritet.NORMAL;
	private PMode.Action action = PMode.Action.KVITTERING;

	private SDPMelding kvittering = null;
	private DateTime kvitteringTidspunkt = DateTime.now();

	public static ApplikasjonsKvitteringBuilder create(final EbmsAktoer avsender, final EbmsAktoer ebmsMottaker, final Organisasjonsnummer sbdhMottaker, final String messageId,
													   final String conversationId, final String instanceIdentifier, DateTime creationTime) {
		ApplikasjonsKvitteringBuilder builder = new ApplikasjonsKvitteringBuilder();
		builder.creationTime = creationTime;
		builder.ebmsMottaker = ebmsMottaker;
		builder.sbdhMottaker = sbdhMottaker;
		builder.messageId = messageId;
		builder.avsender = avsender;
		builder.conversationId = conversationId;
		builder.instanceIdentifier = instanceIdentifier;
		return builder;
	}

	public ApplikasjonsKvitteringBuilder medAction(final PMode.Action action) {
		this.action = action;
		return this;
	}

	public ApplikasjonsKvitteringBuilder medPrioritet(final EbmsOutgoingMessage.Prioritet prioritet) {
		this.prioritet = prioritet;
		return this;
	}

	public ApplikasjonsKvitteringBuilder medMottak() {
		kvittering = new SDPKvittering()
				.withMottak(new SDPMottak())
				.withTidspunkt(kvitteringTidspunkt);
		return this;
	}

	public ApplikasjonsKvitteringBuilder medLevering() {
		kvittering = new SDPKvittering()
				.withLevering(new SDPLevering())
				.withTidspunkt(kvitteringTidspunkt);
		return this;
	}

	public ApplikasjonsKvitteringBuilder medFeil(final SDPFeiltype feiltype, final String feilinformasjon) {
		kvittering = new SDPFeil()
				.withFeiltype(feiltype)
				.withDetaljer(feilinformasjon)
				.withTidspunkt(kvitteringTidspunkt);
		return this;
	}

	public ApplikasjonsKvitteringBuilder medAapning() {
		kvittering = new SDPKvittering()
				.withAapning(new SDPAapning())
				.withTidspunkt(kvitteringTidspunkt);
		return this;
	}

	public ApplikasjonsKvitteringBuilder medReturpost() {
		kvittering = new SDPKvittering()
				.withReturpost(new SDPReturpost())
				.withTidspunkt(kvitteringTidspunkt);
		return this;
	}

	public ApplikasjonsKvitteringBuilder medVarslingfeilet(final SDPVarslingskanal varslingskanal, final String beskrivelse) {
		kvittering = new SDPKvittering()
				.withVarslingfeilet(new SDPVarslingfeilet()
						.withBeskrivelse(beskrivelse)
						.withVarslingskanal(varslingskanal))
				.withTidspunkt(kvitteringTidspunkt);
		return this;
	}
	public ApplikasjonsKvitteringBuilder medTidspunkt(final DateTime kvitteringTidspunkt) {
		if (kvitteringTidspunkt != null) {
			this.kvitteringTidspunkt = kvitteringTidspunkt;
		}
		return this;
	}

	public EbmsApplikasjonsKvittering build() {
		if (kvittering == null) {
			kvittering = new SDPKvittering()
				.withLevering(new SDPLevering())
				.withTidspunkt(kvitteringTidspunkt);
		}

		final StandardBusinessDocument doc = StandardBusinessDocumentFactory
				.create(avsender.orgnr, sbdhMottaker, instanceIdentifier, creationTime, conversationId, kvittering);
		return EbmsApplikasjonsKvittering.create(avsender, ebmsMottaker, doc)
				.withMessageId(messageId)
				.withPrioritet(prioritet)
				.withAction(action)
				.build();
	}

}
