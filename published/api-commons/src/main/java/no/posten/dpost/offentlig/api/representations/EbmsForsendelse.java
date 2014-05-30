package no.posten.dpost.offentlig.api.representations;

import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

public class EbmsForsendelse extends EbmsOutgoingMessage {
	public static final String CONTENT_TYPE_KRYPTERT_DOKUMENTPAKKE = "application/cms";

	private final Dokumentpakke dokumentpakke;
	private final Organisasjonsnummer mottaker;
	private final Organisasjonsnummer avsender;
	public final String conversationId;
	public final String instanceIdentifier;
	public final StandardBusinessDocument doc;

	private EbmsForsendelse(final String messageId, final EbmsMottaker ebmsMottaker, final Organisasjonsnummer avsender, final Prioritet prioritet, final String conversationId, final String instanceIdentifier, final StandardBusinessDocument doc, final Dokumentpakke dokumentpakke) {
		super(ebmsMottaker, messageId, null, prioritet);
		this.avsender = avsender;
		mottaker = ebmsMottaker.orgnr;
		this.conversationId = conversationId;
		this.instanceIdentifier = instanceIdentifier;
		this.doc = doc;
		this.dokumentpakke = dokumentpakke;
	}

	public Dokumentpakke getDokumentpakke() {
		return dokumentpakke;
	}

	public Organisasjonsnummer getMottaker() {
		return mottaker;
	}
	public Organisasjonsnummer getAvsender() {
		return avsender;
	}
	public static Builder create(final Organisasjonsnummer avsender, final Organisasjonsnummer mottaker, final SDPDigitalPost digitalPost, final Dokumentpakke dokumentpakke) {
		Builder builder = new Builder();
		builder.avsender = avsender;
		builder.mottaker = mottaker;
		builder.digitalPost = digitalPost;
		builder.dokumentpakke = dokumentpakke;
		return builder;
	}

	public static Builder create(final StandardBusinessDocument sbd, final Dokumentpakke dokumentpakke) {
		SimpleStandardBusinessDocument sdoc = new SimpleStandardBusinessDocument(sbd);
		Builder builder = new Builder();
		builder.dokumentpakke = dokumentpakke;
		builder.avsender = sdoc.getSender();
		builder.mottaker = sdoc.getReceiver();
		builder.conversationId = sdoc.getConversationId();
		builder.instanceIdentifier = sdoc.getInstanceIdentifier();
		builder.doc = sbd;
		builder.digitalPost = (SDPDigitalPost)sbd.getAny();
		return builder;

	}

	public static EbmsForsendelse from(final StandardBusinessDocument sbd, final Dokumentpakke dokumentpakke) {
		return create(sbd, dokumentpakke).build();
	}


	public static class Builder {
		private Dokumentpakke dokumentpakke;
		private Organisasjonsnummer mottaker;
		private Organisasjonsnummer avsender;
		private String conversationId = newId();
		private String instanceIdentifier = newId();
		private StandardBusinessDocument doc;
		private SDPDigitalPost digitalPost;
		private String messageId = newId();
		private Prioritet prioritet = Prioritet.NORMAL;

		private Builder() {
		}

		public Builder withMessageId(final String messageId) {
			this.messageId = messageId;
			return this;
		}

		public Builder withConversationId(final String conversationId) {
			this.conversationId = conversationId;
			return this;
		}

		public Builder withPrioritet(final Prioritet prioritet) {
			this.prioritet = prioritet;
			return this;
		}

		public EbmsForsendelse build() {
			if (doc == null) {
				doc = StandardBusinessDocumentFactory
						.create(avsender, mottaker, instanceIdentifier, conversationId, digitalPost);
			}
			return new EbmsForsendelse(messageId, new EbmsMottaker(mottaker), avsender, prioritet, conversationId, instanceIdentifier, doc, dokumentpakke);
		}
	}

}
