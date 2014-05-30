package no.posten.dpost.offentlig.api.interceptors;

import no.posten.dpost.offentlig.api.EbmsClientException;
import no.posten.dpost.offentlig.api.representations.EbmsContext;
import no.posten.dpost.offentlig.api.representations.Organisasjonsnummer;
import no.posten.dpost.offentlig.api.security.OrgnummerExtractor;
import no.posten.dpost.offentlig.xml.Marshalling;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static no.posten.dpost.offentlig.xml.Constants.MESSAGING_QNAME;

public class EbmsClientInterceptor implements ClientInterceptor {

	private final Jaxb2Marshaller jaxb2Marshaller;
	private final Organisasjonsnummer tekniskMottaker;
	private final OrgnummerExtractor extractor = new OrgnummerExtractor();

	public EbmsClientInterceptor(final Jaxb2Marshaller jaxb2Marshaller, final Organisasjonsnummer tekniskMottaker) {
		this.jaxb2Marshaller = jaxb2Marshaller;
		this.tekniskMottaker = tekniskMottaker;
	}

	@Override
	public boolean handleRequest(final MessageContext messageContext) throws WebServiceClientException {
		SaajSoapMessage requestMessage = (SaajSoapMessage) messageContext.getRequest();
		SoapHeader soapHeader = requestMessage.getSoapHeader();
		EbmsContext context = EbmsContext.from(messageContext);
		SoapHeaderElement ebmsHeader = soapHeader.addHeaderElement(MESSAGING_QNAME);
		ebmsHeader.setMustUnderstand(true);
		context.processRequest(context, ebmsHeader, requestMessage);
		return true;
	}

	@Override
	public boolean handleResponse(final MessageContext messageContext) throws WebServiceClientException {
		SaajSoapMessage saajSoapMessage = (SaajSoapMessage)messageContext.getResponse();
		Iterator<SoapHeaderElement> soapHeaderElementIterator = saajSoapMessage.getSoapHeader().examineHeaderElements(MESSAGING_QNAME);
		if (!soapHeaderElementIterator.hasNext()) {
			throw new RuntimeException("Missing required EBMS SOAP header");
		}
		SoapHeaderElement ebmsMessaging = soapHeaderElementIterator.next();
		Messaging messaging = Marshalling.unmarshal(jaxb2Marshaller, ebmsMessaging, Messaging.class);
		EbmsContext context = EbmsContext.from(messageContext);
		List<Error> errors = new ArrayList<Error>();
		for (SignalMessage message: messaging.getSignalMessages()) {
			errors.addAll(message.getErrors());
			if (message.getReceipt() != null) {
				context.receipts.add(message);
			}
		}
		if (errors.size() > 0) {
			throw new EbmsClientException("Unexpected response from server", errors);
		}
		for (UserMessage userMessage : messaging.getUserMessages()) {
			context.userMessage = userMessage;
		}
		context.processResponse(context, ebmsMessaging, saajSoapMessage);
		if (messageContext.containsProperty(Wss4jInterceptor.INCOMING_CERTIFICATE)) {
			X509Certificate cert = (X509Certificate) messageContext.getProperty(Wss4jInterceptor.INCOMING_CERTIFICATE);
			Organisasjonsnummer responder = extractor.from(cert);
			if (!responder.equals(responder)) {
				throw new RuntimeException("Unexpected signer in incoming message:" + responder + " - "+ cert.getSubjectDN().getName());
			}
		}
		return true;
	}

	@Override
	public boolean handleFault(final MessageContext messageContext) throws WebServiceClientException {
		return true;
	}

}
