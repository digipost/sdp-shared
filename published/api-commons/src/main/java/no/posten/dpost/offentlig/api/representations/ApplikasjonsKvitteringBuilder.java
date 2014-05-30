package no.posten.dpost.offentlig.api.representations;

import no.difi.begrep.sdp.schema_v10.SDPAapning;
import no.difi.begrep.sdp.schema_v10.SDPFeil;
import no.difi.begrep.sdp.schema_v10.SDPFeiltype;
import no.difi.begrep.sdp.schema_v10.SDPKvittering;
import no.difi.begrep.sdp.schema_v10.SDPLevering;
import no.difi.begrep.sdp.schema_v10.SDPMelding;
import org.joda.time.DateTime;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;


public class ApplikasjonsKvitteringBuilder {

	private Organisasjonsnummer avsender;
	private Organisasjonsnummer mottaker;
	private String instanceIdentifier;
	private String messageId;
	private String conversationId;

	private SDPMelding kvittering = new SDPKvittering()
		.withLevering(new SDPLevering())
		.withTidspunkt(DateTime.now());

	public static ApplikasjonsKvitteringBuilder create(final Organisasjonsnummer avsender, final Organisasjonsnummer mottaker, final String messageId,
	                                                    final String conversationId, final String instanceIdentifier) {
		ApplikasjonsKvitteringBuilder builder = new ApplikasjonsKvitteringBuilder();
		builder.messageId = messageId;
		builder.avsender = avsender;
		builder.mottaker = mottaker;
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
				.create(avsender, mottaker, instanceIdentifier, conversationId, kvittering);
		return EbmsApplikasjonsKvittering.create(avsender, mottaker, doc).withMessageId(messageId).build();
	}
}
