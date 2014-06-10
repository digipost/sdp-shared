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

import no.digipost.api.PMode;
import no.digipost.api.representations.EbmsContext;
import no.digipost.api.representations.Organisasjonsnummer;
import no.digipost.api.representations.SimpleStandardBusinessDocument;
import no.digipost.api.security.OrgnummerExtractor;
import no.digipost.api.xml.Constants;
import no.digipost.api.xml.Marshalling;
import no.digipost.api.config.TransaksjonsLogg;
import no.digipost.api.config.TransaksjonsLogg.Retning;
import no.digipost.api.config.TransaksjonsLogg.Type;
import no.digipost.api.representations.SimpleUserMessage;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.server.SoapEndpointInterceptor;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import java.security.cert.X509Certificate;

import static no.digipost.api.config.TransaksjonsLogg.Type.APPLIKASJONSKVITTERING;
import static no.digipost.api.config.TransaksjonsLogg.Type.EBMSFEIL;
import static no.digipost.api.config.TransaksjonsLogg.Type.PULLREQUEST;
import static no.digipost.api.config.TransaksjonsLogg.Type.TRANSPORTKVITTERING;
import static no.digipost.api.config.TransaksjonsLogg.Type.USERMESSAGE;
import static no.digipost.api.exceptions.ebms.standard.processing.EmptyMessagePartitionChannelException.EMPTY_MPC_EBMS_CODE;

public class TransactionLogInterceptor implements SoapEndpointInterceptor, ClientInterceptor {

	public enum Phase {
		OUTSIDE_WSSEC,
		INSIDE_WSSEC
	}

	private final Jaxb2Marshaller jaxb2Marshaller;
	private TransaksjonsLogg logg = new TransaksjonsLogg();
	private final Phase phase;
	private static final String KEY = "translog.requestlogged";
	private final OrgnummerExtractor orgnrExtractor = new OrgnummerExtractor();

	private TransactionLogInterceptor(final Jaxb2Marshaller jaxb2Marshaller, final Phase phase) {
		this.jaxb2Marshaller = jaxb2Marshaller;
		this.phase = phase;
	}

	protected void setTransaksjonslogg(final TransaksjonsLogg logg) {
		this.logg = logg;
	}

	public static TransactionLogInterceptor createClientInterceptor(final Jaxb2Marshaller jaxb2Marshaller) {
		return new TransactionLogInterceptor(jaxb2Marshaller, null);
	}

	public static TransactionLogInterceptor createServerInterceptor(final Jaxb2Marshaller jaxb2Marshaller, final Phase phase) {
		return new TransactionLogInterceptor(jaxb2Marshaller, phase);
	}

	/*
	 * SoapEndpointInterceptor
	 */

	@Override
	public boolean understands(final SoapHeaderElement header) {
		return true;
	}

	@Override
	public boolean handleRequest(final MessageContext messageContext, final Object endpoint) throws Exception {
		if (phase == Phase.INSIDE_WSSEC) {
			loggIncomingEndpointRequest(messageContext, getName(endpoint));
			messageContext.setProperty(KEY, true);
		}
		return true;
	}

	private String getName(final Object endpoint) {
		return ((MethodEndpoint) endpoint).getBean().toString();
	}

	private void loggIncomingEndpointRequest(final MessageContext messageContext, final String endpoint) {
		setOrgNummer(messageContext);
		handleIncoming(EbmsContext.from(messageContext), (SoapMessage) messageContext.getRequest(), endpoint);
	}

	@Override
	public boolean handleResponse(final MessageContext messageContext, final Object endpoint) throws Exception {
		if (phase == Phase.OUTSIDE_WSSEC) {
			if (messageContext.getProperty(KEY) == null) {
				loggIncomingEndpointRequest(messageContext, getName(endpoint));
			}
			handleOutgoing(EbmsContext.from(messageContext), (SoapMessage) messageContext.getResponse(), getName(endpoint));
		}
		return true;
	}

	@Override
	public boolean handleFault(final MessageContext messageContext, final Object endpoint) throws Exception {
		if (phase == Phase.OUTSIDE_WSSEC) {
			if (messageContext.getProperty(KEY) == null) {
				loggIncomingEndpointRequest(messageContext, getName(endpoint));
			}
			handleFault(TransaksjonsLogg.Retning.UTGÅENDE, EbmsContext.from(messageContext),
					(SoapMessage) messageContext.getResponse(),
					getName(endpoint));
		}
		return true;
	}

	@Override
	public void afterCompletion(final MessageContext messageContext, final Object endpoint, final Exception ex) throws Exception {
	}

	@Override
	public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {
	}
	
	/*
	 * ClientInterceptor
	 */

	@Override
	public boolean handleRequest(final MessageContext messageContext) throws WebServiceClientException {
		handleOutgoing(EbmsContext.from(messageContext), (SoapMessage) messageContext.getRequest(), "sender");
		return true;
	}

	@Override
	public boolean handleResponse(final MessageContext messageContext) throws WebServiceClientException {
		handleIncoming(EbmsContext.from(messageContext), (SoapMessage) messageContext.getResponse(), "sender");
		return true;
	}

	@Override
	public boolean handleFault(final MessageContext messageContext) throws WebServiceClientException {
		handleFault(TransaksjonsLogg.Retning.INNKOMMENDE, EbmsContext.from(messageContext), (SoapMessage) messageContext.getResponse(), "sender");
		return true;
	}

	private void handleIncoming(final EbmsContext context, final SoapMessage soapMessage, final String endpoint) {
		decorate(context, soapMessage);

		Messaging msg = Marshalling.getMessaging(jaxb2Marshaller, soapMessage);
		for (UserMessage userMessage : msg.getUserMessages()) {
			SimpleUserMessage u = new SimpleUserMessage(userMessage);
			context.mpcMap.put(u.getMessageId(), u.getMpc());
			logg.innkommende(endpoint, getOrgNr(context), getType(u), u.getMpc(), getConversationId(context), getInstanceIdentifier(context), u.getMessageId(), u.getRefToMessageId());
		}
		for (SignalMessage signalMessage : msg.getSignalMessages()) {
			MessageInfo messageInfo = signalMessage.getMessageInfo();
			context.mpcMap.put(messageInfo.getMessageId(), getMpcFromSignal(context, signalMessage));
			logg.innkommende(endpoint, getOrgNr(context), getType(signalMessage), getMpcFromSignal(context, signalMessage), getConversationId(context), getInstanceIdentifier(context), messageInfo.getMessageId(), messageInfo.getRefToMessageId());
		}
	}

	private void handleOutgoing(final EbmsContext context, final SoapMessage soapMessage, final String endpoint) {
		decorate(context, soapMessage);

		Messaging msg = Marshalling.getMessaging(jaxb2Marshaller, soapMessage);
		for (UserMessage userMessage : msg.getUserMessages()) {
			SimpleUserMessage u = new SimpleUserMessage(userMessage);
			context.mpcMap.put(u.getMessageId(), u.getMpc());
			logg.utgående(endpoint, getOrgNr(context), getType(u), u.getMpc(), getConversationId(context), getInstanceIdentifier(context), u.getMessageId(), u.getRefToMessageId());
		}
		for (SignalMessage signalMessage : msg.getSignalMessages()) {
			MessageInfo messageInfo = signalMessage.getMessageInfo();
			context.mpcMap.put(messageInfo.getMessageId(), getMpcFromSignal(context, signalMessage));
			logg.utgående(endpoint, getOrgNr(context), getType(signalMessage), getMpcFromSignal(context, signalMessage), getConversationId(context), getInstanceIdentifier(context), messageInfo.getMessageId(), messageInfo.getRefToMessageId());
		}
	}

	private void handleFault(final Retning retning, final EbmsContext context, final SoapMessage soapMessage, final String endpoint) {
		SoapBody soapBody = soapMessage.getSoapBody();
		SoapFault soapFault = soapBody.getFault();
		logg.soapfault(endpoint, getOrgNr(context), retning, soapFault);

		if (soapMessage.getSoapHeader().examineHeaderElements(Constants.MESSAGING_QNAME).hasNext()) {
			Messaging messaging = Marshalling.getMessaging(jaxb2Marshaller, soapMessage);
			for (SignalMessage signalMessage : messaging.getSignalMessages()) {
				for (Error error : signalMessage.getErrors()) {
					logg.ebmserror(endpoint, getOrgNr(context), retning, error, signalMessage.getMessageInfo(), getMpcFromSignal(context, signalMessage), getConversationId(context), getInstanceIdentifier(context));
				}
			}
		}
	}

	private String getMpcFromSignal(final EbmsContext context, final SignalMessage signalMessage) {
		String mpc = null;
		if (signalMessage.getPullRequest() != null) {
			mpc = signalMessage.getPullRequest().getMpc();
		}
		String refToMessageId = signalMessage.getMessageInfo().getRefToMessageId();
		if (refToMessageId != null && context.mpcMap.containsKey(refToMessageId)) {
			return context.mpcMap.get(refToMessageId);
		}
		return mpc;
	}

	private Type getType(final SimpleUserMessage u) {
		if (u.getAction().equals(PMode.ACTION_FORMIDLE)) {
			return USERMESSAGE;
		} else {
			return APPLIKASJONSKVITTERING;
		}
	}

	private String getInstanceIdentifier(final EbmsContext context) {
		if (context.sbd != null) {
			return context.sbd.getInstanceIdentifier();
		}
		return null;
	}

	private String getConversationId(final EbmsContext context) {
		if (context.sbd != null) {
			return context.sbd.getConversationId();
		}
		return null;
	}

	private String getOrgNr(final EbmsContext context) {
		StringBuilder builder = new StringBuilder();
		if (context.remoteParty != null && context.remoteParty != Organisasjonsnummer.NULL) {
			builder.append(context.remoteParty.toString());
		} else {
			builder.append("-");
		}
		if (context.sbd != null) {
			builder.append(" ");
			builder.append(context.sbd.getSender());
			builder.append(" ");
			builder.append(context.sbd.getReceiver());
		}
		return builder.toString();
	}

	private Type getType(final SignalMessage signalMessage) {
		if (signalMessage.getPullRequest() != null) {
			return PULLREQUEST;
		} else if (!signalMessage.getErrors().isEmpty()) {
			if (signalMessage.getErrors().size() == 1 && EMPTY_MPC_EBMS_CODE.equals(signalMessage.getErrors().get(0).getErrorCode())) {
				return Type.TOMKØ;
			}
			return EBMSFEIL;
		} else {
			return TRANSPORTKVITTERING;
		}
	}

	private void decorate(final EbmsContext context, final SoapMessage soapMessage) {
		if (context.sbd == null && soapMessage.getSoapBody().getPayloadSource() != null) {
			StandardBusinessDocument sbd = Marshalling.unmarshal(jaxb2Marshaller, soapMessage.getSoapBody(), StandardBusinessDocument.class);
			context.sbd = new SimpleStandardBusinessDocument(sbd);
		}
	}

	private void setOrgNummer(final MessageContext messageContext) {
		EbmsContext ebmsContext = EbmsContext.from(messageContext);
		if (ebmsContext.remoteParty != null) {
			return;
		}
		ebmsContext.remoteParty = Organisasjonsnummer.NULL;

		X509Certificate cert = (X509Certificate) messageContext.getProperty(Wss4jInterceptor.INCOMING_CERTIFICATE);
		if (cert != null) {
			Organisasjonsnummer orgnr = orgnrExtractor.tryParse(cert);
			if (orgnr != null) {
				ebmsContext.remoteParty = orgnr;
			}
		}
	}


}
