
package no.digipost.api.handlers;

import no.digipost.api.exceptions.MessageSenderIOException;
import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.EbmsContext;
import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.WebServiceConnection;
import org.springframework.ws.transport.http.HttpComponentsConnection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

public class EbmsContextAwareWebServiceTemplate extends WebServiceTemplate {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final EbmsAktoer ebmsAktoerRemoteParty;

	public EbmsContextAwareWebServiceTemplate(final SaajSoapMessageFactory factory, final EbmsAktoer ebmsAktoerRemoteParty) {
		super(factory);
		this.ebmsAktoerRemoteParty = ebmsAktoerRemoteParty;
	}

	@Override
	protected <T> T doSendAndReceive(final MessageContext messageContext, final WebServiceConnection connection,
	                                 final WebServiceMessageCallback requestCallback, final WebServiceMessageExtractor<T> responseExtractor) throws IOException {
		EbmsContext ebmsContext = EbmsContext.from(messageContext);
		ebmsContext.remoteParty = Optional.of(ebmsAktoerRemoteParty.orgnr);
		if (requestCallback instanceof EbmsContextAware) {
			((EbmsContextAware) requestCallback).setContext(ebmsContext);
		}
		if (responseExtractor instanceof EbmsContextAware) {
			((EbmsContextAware) responseExtractor).setContext(ebmsContext);
		}
		try {
			return super.doSendAndReceive(messageContext, connection, requestCallback, responseExtractor);
		} catch (IOException e) {
			throw new MessageSenderIOException(e.getMessage(), e);
		}
	}

	@Override
	protected Object handleError(WebServiceConnection connection, WebServiceMessage request) throws IOException {
		if (connection instanceof HttpComponentsConnection) {
			HttpComponentsConnection componentsConnection = (HttpComponentsConnection) connection;
			HttpEntity entity = componentsConnection.getHttpResponse().getEntity();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (entity != null) {
				entity.writeTo(baos);
				log.warn("Received erroneous response from server: " + baos.toString());
			} else {
				log.warn("Received erroneous response (no body)");
			}
		}
		throw new MessageSenderIOException(connection.getErrorMessage(), null);
	}

}
