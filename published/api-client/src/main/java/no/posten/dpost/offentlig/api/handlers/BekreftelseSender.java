package no.posten.dpost.offentlig.api.handlers;

import no.posten.dpost.offentlig.api.interceptors.steps.AddReferencesStep;

import no.posten.dpost.offentlig.api.representations.EbmsApplikasjonsKvittering;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;

import javax.xml.transform.TransformerException;

import java.io.IOException;

public class BekreftelseSender extends EbmsContextAware implements WebServiceMessageCallback {

	private final EbmsApplikasjonsKvittering appKvittering;
	private final Jaxb2Marshaller jaxb2Marshaller;

	public BekreftelseSender(final EbmsApplikasjonsKvittering appKvittering, final Jaxb2Marshaller jaxb2Marshaller) {
		this.appKvittering = appKvittering;
		this.jaxb2Marshaller = jaxb2Marshaller;
	}

	@Override
	public void doWithMessage(final WebServiceMessage message) throws IOException, TransformerException {
		ebmsContext.addRequestStep(new AddReferencesStep(jaxb2Marshaller, appKvittering.messageId, appKvittering.references));
	}


}
