package no.posten.dpost.offentlig.api.representations;

import no.posten.dpost.offentlig.api.representations.SimpleStandardBusinessDocument.SimpleKvittering;
import org.springframework.util.StringUtils;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.w3.xmldsig.Reference;

import java.util.ArrayList;
import java.util.List;


public class EbmsApplikasjonsKvittering extends EbmsOutgoingMessage {
	public final StandardBusinessDocument sbd;
	public final List<Reference> references = new ArrayList<Reference>();
	public EbmsMottaker avsender = null;

	private EbmsApplikasjonsKvittering(final EbmsMottaker avsender, final EbmsMottaker mottaker, final EbmsOutgoingMessage.Prioritet prioritet, final String messageId, final String refToMessageId, final StandardBusinessDocument sbd) {
		super(mottaker, messageId, refToMessageId, prioritet);
		this.sbd = sbd;
		this.avsender = avsender;
	}

	public static Builder create(final EbmsMottaker avsender, final EbmsMottaker mottaker, final StandardBusinessDocument sbd) {
		return new Builder(avsender, mottaker, sbd);
	}
	public static Builder create(final Organisasjonsnummer avsender, final Organisasjonsnummer mottaker, final StandardBusinessDocument sbd) {
		return new Builder(new EbmsMottaker(avsender), new EbmsMottaker(mottaker), sbd);
	}

	public SimpleStandardBusinessDocument getStandardBusinessDocument() {
		return new SimpleStandardBusinessDocument(sbd);
	}
	public SimpleKvittering getKvittering() {
		return new SimpleStandardBusinessDocument(sbd).getKvittering();
	}


	public static class Builder {

		private final EbmsMottaker avsender;
		private final EbmsMottaker mottaker;
		private Prioritet prioritet = Prioritet.NORMAL;
		private final StandardBusinessDocument sbd;
		private String messageId = null;
		private String refToMessageId = null;
		private List<Reference> references = new ArrayList<Reference>();

		public Builder(final EbmsMottaker avsender, final EbmsMottaker mottaker, final StandardBusinessDocument sbd) {
			this.mottaker = mottaker;
			this.avsender = avsender;
			this.sbd = sbd;
		}

		public Builder withMessageId(final String messageId) {
			this.messageId = messageId;
			return this;
		}
		public Builder withPrioritet(final Prioritet prioritet) {
			this.prioritet = prioritet;
			return this;
		}
		public Builder withRefToMessageId(final String refToMessageId) {
			this.refToMessageId = refToMessageId;
			return this;
		}
		public Builder withReferences(final List<Reference> incomingReferences) {
			references = incomingReferences;
			return this;
		}

		public EbmsApplikasjonsKvittering build() {
			String id = StringUtils.isEmpty(messageId) ? newId() : messageId;
			EbmsApplikasjonsKvittering kvittering = new EbmsApplikasjonsKvittering(avsender, mottaker, prioritet, id, refToMessageId, sbd);
			kvittering.references.addAll(references);
			return kvittering;
		}


	}
}
