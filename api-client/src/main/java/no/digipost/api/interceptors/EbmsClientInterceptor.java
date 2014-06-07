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
package no.digipost.api.interceptors;

import no.digipost.api.EbmsClientException;
import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.EbmsContext;
import no.digipost.api.representations.Organisasjonsnummer;
import no.digipost.api.security.OrgnummerExtractor;
import no.digipost.api.xml.Marshalling;
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
import org.springframework.ws.soap.SoapMessage;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static no.digipost.api.xml.Constants.MESSAGING_QNAME;

public class EbmsClientInterceptor implements ClientInterceptor {

	private final Jaxb2Marshaller jaxb2Marshaller;
	private final EbmsAktoer tekniskMottaker;
	private final OrgnummerExtractor extractor = new OrgnummerExtractor();

	public EbmsClientInterceptor(final Jaxb2Marshaller jaxb2Marshaller, final EbmsAktoer tekniskMottaker) {
		this.jaxb2Marshaller = jaxb2Marshaller;
		this.tekniskMottaker = tekniskMottaker;
	}

	@Override
	public boolean handleRequest(final MessageContext messageContext) throws WebServiceClientException {
		SoapMessage requestMessage = (SoapMessage) messageContext.getRequest();
		SoapHeader soapHeader = requestMessage.getSoapHeader();
		EbmsContext context = EbmsContext.from(messageContext);
		SoapHeaderElement ebmsHeader = soapHeader.addHeaderElement(MESSAGING_QNAME);
		ebmsHeader.setMustUnderstand(true);
		context.processRequest(context, ebmsHeader, requestMessage);
		return true;
	}

	@Override
	public boolean handleResponse(final MessageContext messageContext) throws WebServiceClientException {
		SoapMessage saajSoapMessage = (SoapMessage) messageContext.getResponse();
		Iterator<SoapHeaderElement> soapHeaderElementIterator = saajSoapMessage.getSoapHeader().examineHeaderElements(MESSAGING_QNAME);
		if (!soapHeaderElementIterator.hasNext()) {
			throw new RuntimeException("Missing required EBMS SOAP header");
		}
		SoapHeaderElement ebmsMessaging = soapHeaderElementIterator.next();
		Messaging messaging = Marshalling.unmarshal(jaxb2Marshaller, ebmsMessaging, Messaging.class);
		EbmsContext context = EbmsContext.from(messageContext);
		List<Error> errors = new ArrayList<Error>();
		for (SignalMessage message : messaging.getSignalMessages()) {
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
			if (!tekniskMottaker.orgnr.equals(responder)) {
				throw new RuntimeException(String.format("Unexpected signer in incoming message. Expected: [%s] Extracted: [%s] from %s", tekniskMottaker.orgnr, responder, cert.getSubjectDN().getName()));
			}
		}
		return true;
	}

	@Override
	public boolean handleFault(final MessageContext messageContext) throws WebServiceClientException {
		return true;
	}

	@Override
	public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {
		// TODO Auto-generated method stub
		
	}

}
