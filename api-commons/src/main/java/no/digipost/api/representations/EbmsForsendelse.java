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

import no.difi.begrep.sdp.schema_v10.SDPMelding;
import no.digipost.xsd.types.DigitalPostformidling;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import java.io.InputStream;

public class EbmsForsendelse extends EbmsOutgoingMessage {
	private final Dokumentpakke dokumentpakke;
	private final EbmsAktoer ebmsMottaker;
	private final EbmsAktoer ebmsAvsender;
	public final String conversationId;
	public final String instanceIdentifier;
	public final StandardBusinessDocument doc;
	private final Organisasjonsnummer sbdhMottaker;
	public final InputStream sbdStream;

	private EbmsForsendelse(final String messageId, final EbmsAktoer ebmsMottaker, final EbmsAktoer ebmsAvsender, final String mpcId, final Organisasjonsnummer sbdhMottaker, final Prioritet prioritet, final String conversationId, final String instanceIdentifier, final StandardBusinessDocument doc, final Dokumentpakke dokumentpakke, final InputStream sbdStream) {
		super(ebmsMottaker, messageId, null, prioritet, mpcId);
		this.ebmsMottaker = ebmsMottaker;
		this.ebmsAvsender = ebmsAvsender;
		this.sbdhMottaker = sbdhMottaker;
		this.conversationId = conversationId;
		this.instanceIdentifier = instanceIdentifier;
		this.doc = doc;
		this.dokumentpakke = dokumentpakke;
		this.sbdStream = sbdStream;
	}

	public Dokumentpakke getDokumentpakke() {
		return dokumentpakke;
	}

	public EbmsAktoer getMottaker() {
		return ebmsMottaker;
	}

	public EbmsAktoer getAvsender() {
		return ebmsAvsender;
	}

	public Organisasjonsnummer getSbdhMottaker() {
		return sbdhMottaker;
	}

	public static <P extends SDPMelding & DigitalPostformidling> Builder create(final EbmsAktoer avsender, final EbmsAktoer mottaker, final Organisasjonsnummer sbdhMottaker, final P digitalPostformidling, final Dokumentpakke dokumentpakke) {
		Builder builder = new Builder();
		builder.avsender = avsender;
		builder.mottaker = mottaker;
		builder.sbdhMottaker = sbdhMottaker;
		builder.digitalPost = digitalPostformidling;
		builder.dokumentpakke = dokumentpakke;
		return builder;
	}

	public static Builder create(final EbmsAktoer avsender, final EbmsAktoer mottaker, final Organisasjonsnummer sbdhMottaker, final StandardBusinessDocument sbd, final Dokumentpakke dokumentpakke) {
		SimpleStandardBusinessDocument sdoc = new SimpleStandardBusinessDocument(sbd);
		Builder builder = new Builder();
		builder.dokumentpakke = dokumentpakke;
		builder.avsender = avsender;
		builder.mottaker = mottaker;
		builder.sbdhMottaker = sbdhMottaker;
		builder.conversationId = sdoc.getConversationId();
		builder.instanceIdentifier = sdoc.getInstanceIdentifier();
		builder.doc = sbd;
		builder.digitalPost = (SDPMelding)sbd.getAny();
		return builder;
	}

	public static EbmsForsendelse from(final EbmsAktoer avsender, final EbmsAktoer mottaker, final StandardBusinessDocument sbd, final Dokumentpakke dokumentpakke) {
		return create(avsender, mottaker, new SimpleStandardBusinessDocument(sbd).getReceiver(), sbd, dokumentpakke).build();
	}

	public static class Builder {
		private Dokumentpakke dokumentpakke;
		private Organisasjonsnummer sbdhMottaker;
		private EbmsAktoer mottaker;
		private EbmsAktoer avsender;
		private String conversationId = newId();
		private String instanceIdentifier = newId();
		private StandardBusinessDocument doc;
		private SDPMelding digitalPost;
		private String messageId = newId();
		public InputStream sbdStream = null;
		private Prioritet prioritet = Prioritet.NORMAL;
		private String mpcId = null;

		private Builder() {
		}

		public Builder withMessageId(final String messageId) {
			this.messageId = messageId;
			return this;
		}
		public Builder withSbdStream(final InputStream sbdStream) {
			this.sbdStream = sbdStream;
			return this;
		}

		public Builder withConversationId(final String conversationId) {
			this.conversationId = conversationId;
			return this;
		}
		public Builder withMpcId(final String mpcId) {
			this.mpcId = mpcId;
			return this;
		}

		public Builder withPrioritet(final Prioritet prioritet) {
			this.prioritet = prioritet;
			return this;
		}

		public EbmsForsendelse build() {
			if (doc == null) {
				doc = StandardBusinessDocumentFactory
						.create(avsender.orgnr, sbdhMottaker, instanceIdentifier, conversationId, digitalPost);
			}
			return new EbmsForsendelse(messageId, mottaker, avsender, mpcId, sbdhMottaker, prioritet, conversationId, instanceIdentifier, doc, dokumentpakke, sbdStream);
		}
	}

}
