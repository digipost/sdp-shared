package no.posten.dpost.offentlig.api.handlers;

import no.posten.dpost.offentlig.api.SdpMeldingSigner;
import no.posten.dpost.offentlig.api.interceptors.steps.AddUserMessageStep;
import no.posten.dpost.offentlig.api.representations.EbmsAktoer;
import no.posten.dpost.offentlig.api.representations.EbmsApplikasjonsKvittering;
import no.posten.dpost.offentlig.api.representations.Mpc;
import no.posten.dpost.offentlig.api.representations.SimpleStandardBusinessDocument;
import no.posten.dpost.offentlig.xml.Marshalling;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;

import java.io.IOException;

public class KvitteringSender extends EbmsContextAware implements WebServiceMessageCallback {

	private final EbmsApplikasjonsKvittering appKvittering;
	private final Jaxb2Marshaller marshaller;
	private final EbmsAktoer tekniskAvsender;
	private final EbmsAktoer tekniskMottaker;
	private final SdpMeldingSigner signer;

	public KvitteringSender(final SdpMeldingSigner signer, final EbmsAktoer tekniskAvsender, final EbmsAktoer tekniskMottaker, final EbmsApplikasjonsKvittering appKvittering, final Jaxb2Marshaller marshaller) {
		this.signer = signer;
		this.tekniskAvsender = tekniskAvsender;
		this.tekniskMottaker = tekniskMottaker;
		this.appKvittering = appKvittering;
		this.marshaller = marshaller;
	}

	@Override
	public void doWithMessage(final WebServiceMessage message) throws IOException, TransformerException {
		SaajSoapMessage soapMessage = (SaajSoapMessage) message;
		SimpleStandardBusinessDocument simple = new SimpleStandardBusinessDocument(appKvittering.sbd);
		if (simple.getMelding().getSignature() == null) {
			Document signedDoc = signer.sign(appKvittering.sbd);
			Marshalling.marshal(signedDoc, soapMessage.getEnvelope().getBody().getPayloadResult());
		} else {
			Marshalling.marshal(marshaller, soapMessage.getEnvelope().getBody(), appKvittering.sbd);
		}

		Mpc mpc = new Mpc(appKvittering.prioritet, null);
		ebmsContext.addRequestStep(new AddUserMessageStep(mpc, appKvittering.messageId, null, appKvittering.sbd, tekniskAvsender, tekniskMottaker
				, marshaller));
	}

}
