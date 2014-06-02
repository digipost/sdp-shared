package no.posten.dpost.offentlig.api.handlers;

import no.posten.dpost.offentlig.api.representations.EbmsAktoer;
import no.posten.dpost.offentlig.api.representations.EbmsContext;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.WebServiceConnection;

import java.io.IOException;

public class EbmsContextAwareWebServiceTemplate extends WebServiceTemplate {


	private final EbmsAktoer remoteParty;

	public EbmsContextAwareWebServiceTemplate(final SaajSoapMessageFactory factory, final EbmsAktoer remoteParty) {
		super(factory);
		this.remoteParty = remoteParty;
	}

	@Override
	protected <T> T doSendAndReceive(final MessageContext messageContext, final WebServiceConnection connection,
			final WebServiceMessageCallback requestCallback, final WebServiceMessageExtractor<T> responseExtractor) throws IOException {
		EbmsContext context = EbmsContext.from(messageContext);
		context.remoteParty = remoteParty.orgnr;
		if (requestCallback instanceof EbmsContextAware) {
			((EbmsContextAware) requestCallback).setContext(context);
		}
		if (responseExtractor instanceof EbmsContextAware) {
			((EbmsContextAware) responseExtractor).setContext(context);
		}
		return super.doSendAndReceive(messageContext, connection, requestCallback, responseExtractor);
	}



}
