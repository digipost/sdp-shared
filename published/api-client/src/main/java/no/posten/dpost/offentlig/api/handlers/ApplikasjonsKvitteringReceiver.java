package no.posten.dpost.offentlig.api.handlers;

import no.posten.dpost.offentlig.api.representations.EbmsAktoer;
import no.posten.dpost.offentlig.api.representations.EbmsApplikasjonsKvittering;
import no.posten.dpost.offentlig.xml.Marshalling;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import javax.xml.transform.TransformerException;

import java.io.IOException;

public class ApplikasjonsKvitteringReceiver extends EbmsContextAware implements WebServiceMessageExtractor<EbmsApplikasjonsKvittering> {

	private final Jaxb2Marshaller jaxb2Marshaller;

	public ApplikasjonsKvitteringReceiver(final Jaxb2Marshaller jaxb2Marshaller) {
		this.jaxb2Marshaller = jaxb2Marshaller;
	}

	@Override
	public EbmsApplikasjonsKvittering extractData(final WebServiceMessage message) throws IOException, TransformerException {
		SoapBody soapBody = ((SaajSoapMessage)message).getSoapBody();
		StandardBusinessDocument sbd = Marshalling.unmarshal(jaxb2Marshaller, soapBody, StandardBusinessDocument.class);
		PartyInfo partyInfo = ebmsContext.userMessage.getPartyInfo();
		EbmsAktoer avsender = EbmsAktoer.from(partyInfo.getFrom());
		EbmsAktoer mottaker = EbmsAktoer.from(partyInfo.getTo());

		return EbmsApplikasjonsKvittering.create(avsender, mottaker, sbd)
				.withMessageId(ebmsContext.userMessage.getMessageInfo().getMessageId())
				.withRefToMessageId(ebmsContext.userMessage.getMessageInfo().getRefToMessageId())
				.withReferences(ebmsContext.incomingReferences)
				.build();
	}

}
