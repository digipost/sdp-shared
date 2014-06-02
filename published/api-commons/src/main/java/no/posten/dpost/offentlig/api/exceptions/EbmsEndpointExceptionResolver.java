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
package no.posten.dpost.offentlig.api.exceptions;

import no.posten.dpost.offentlig.api.exceptions.ebms.AbstractEbmsException;
import no.posten.dpost.offentlig.api.exceptions.ebms.custom.security.SignatureValidationException;
import no.posten.dpost.offentlig.api.exceptions.ebms.standard.processing.OtherException;
import no.posten.dpost.offentlig.api.exceptions.ebms.standard.security.PolicyNoncomplianceException;
import no.posten.dpost.offentlig.api.representations.EbmsContext;
import org.joda.time.DateTime;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.AbstractEndpointExceptionResolver;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.security.WsSecurityFaultException;
import org.springframework.ws.soap.security.wss4j.Wss4jSecurityValidationException;
import org.springframework.ws.soap.security.xwss.XwsSecurityValidationException;
import org.springframework.ws.soap.soap12.Soap12Fault;

import java.util.Locale;
import java.util.UUID;

import static no.posten.dpost.offentlig.api.representations.EbmsContext.from;
import static no.posten.dpost.offentlig.xml.Constants.MESSAGING_QNAME;
import static no.posten.dpost.offentlig.xml.Constants.SIGNAL_MESSAGE_QNAME;
import static no.posten.dpost.offentlig.xml.Marshalling.marshal;

public class EbmsEndpointExceptionResolver extends AbstractEndpointExceptionResolver {

	private final Jaxb2Marshaller marshaller;
	private static final Logger LOG = LoggerFactory.getLogger(EbmsEndpointExceptionResolver.class);

	public EbmsEndpointExceptionResolver(final Jaxb2Marshaller marshaller) {
		this.marshaller = marshaller;
		// TODO vurdere denne mot loggingen under
		super.setWarnLogCategory("org.springframework.ws");
	}

	/**
	 * @return <code>true</code> if resolved; <code>false</code> otherwise
	 */
	@Override
	protected boolean resolveExceptionInternal(final MessageContext messageContext, final Object endpoint, final Exception ex) {

		// map til korrekt ebMS exception
		AbstractEbmsException mappedException = mapException(ex);
		if (mappedException.loggable()) {
			// TODO vurdere denne i fht warnings
			// TODO loggingen virker ikke i fbm integrasjonstester
			LOG.error("Feil", ex);
		}

		// legg til ebMS error header
		addEbmsError(messageContext, mappedException.toError());

		// dersom failure, lag soap fault ref ebMS spec
		if (mappedException.isSeverityFailure()) {
			addSoapFault(messageContext, mappedException);
		}

		return true;

	}

	private AbstractEbmsException mapException(final Exception ex) {
		AbstractEbmsException mappedException;
		if (ex instanceof AbstractEbmsException) {
			mappedException = (AbstractEbmsException) ex;
		} else if (ex instanceof XwsSecurityValidationException || ex instanceof Wss4jSecurityValidationException) {
			mappedException = new SignatureValidationException();
		} else if (ex instanceof WsSecurityFaultException) {
			mappedException = new PolicyNoncomplianceException(ex);
		} else {
			// default til Other
			mappedException = new OtherException(ex);
		}
		return mappedException;
	}

	private void addSoapFault(final MessageContext messageContext, final AbstractEbmsException ex) {
		SoapBody response = ((SoapMessage) messageContext.getResponse()).getSoapBody();
		if (ex.getCause() instanceof WsSecurityFaultException) {
			WsSecurityFaultException wsSecurityFaultException = (WsSecurityFaultException) ex.getCause();
			Soap12Fault soapFault = (Soap12Fault) response.addClientOrSenderFault(wsSecurityFaultException.getFaultString(), Locale.ENGLISH);
			soapFault.addFaultSubcode(wsSecurityFaultException.getFaultCode());
		} else {
			response.addClientOrSenderFault(ex.getShortDescription(), Locale.ENGLISH);
		}
	}

	private void addEbmsError(final MessageContext messageContext, final Error error) {
		SoapMessage responseMessage = (SoapMessage) messageContext.getResponse();
		SoapHeaderElement ebmsMessaging = responseMessage.getSoapHeader().addHeaderElement(MESSAGING_QNAME);
		ebmsMessaging.setMustUnderstand(true);

		EbmsContext ebmsContext = from(messageContext);
		String refToMessageId = null;
		if (error.getRefToMessageInError() != null) {
			refToMessageId = error.getRefToMessageInError();
		} else if (ebmsContext.userMessage != null) {
			refToMessageId = ebmsContext.userMessage.getMessageInfo().getMessageId();
		} else if (ebmsContext.pullSignal != null) {
			refToMessageId = ebmsContext.pullSignal.getMessageInfo().getMessageId();
		}

		SignalMessage signalMessage = new SignalMessage()
				.withErrors(error)
				.withMessageInfo(new MessageInfo(
						DateTime.now(),
						UUID.randomUUID().toString(),
						refToMessageId));

		marshal(marshaller, ebmsMessaging, SIGNAL_MESSAGE_QNAME, signalMessage);
	}
}
