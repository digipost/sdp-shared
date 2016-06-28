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
import no.digipost.api.config.TransaksjonsLogg;
import no.digipost.api.config.TransaksjonsLogg.Retning;
import no.digipost.api.config.TransaksjonsLogg.Type;
import no.digipost.api.representations.EbmsContext;
import no.digipost.api.representations.SimpleStandardBusinessDocument;
import no.digipost.api.representations.SimpleUserMessage;
import no.digipost.api.xml.Constants;
import no.digipost.api.xml.Marshalling;
import no.digipost.api.xml.MessagingMarshalling;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapMessage;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import static no.digipost.api.config.TransaksjonsLogg.EMPTY_MESSAGE_PARTITION_CHANNEL_EBMS_ERROR_CODE;
import static no.digipost.api.config.TransaksjonsLogg.Type.*;

public class TransactionLog {

	private final Jaxb2Marshaller jaxb2Marshaller;
	private TransaksjonsLogg logg = new TransaksjonsLogg();


	public TransactionLog(final Jaxb2Marshaller jaxb2Marshaller) {
		this.jaxb2Marshaller = jaxb2Marshaller;
	}

	public void setTransaksjonslogg(final TransaksjonsLogg logg) {
		this.logg = logg;
	}

	public void handleIncoming(final EbmsContext context, final SoapMessage soapMessage, final String endpoint) {
		decorate(context, soapMessage);

		Messaging msg = MessagingMarshalling.getMessaging(jaxb2Marshaller, soapMessage);
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

	public void handleOutgoing(final EbmsContext context, final SoapMessage soapMessage, final String endpoint) {
		decorate(context, soapMessage);

		Messaging msg = MessagingMarshalling.getMessaging(jaxb2Marshaller, soapMessage);
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

	public void handleFault(final Retning retning, final EbmsContext context, final SoapMessage soapMessage, final String endpoint) {
		SoapBody soapBody = soapMessage.getSoapBody();
		SoapFault soapFault = soapBody.getFault();
		logg.soapfault(endpoint, getOrgNr(context), soapFault);

		if (soapMessage.getSoapHeader().examineHeaderElements(Constants.MESSAGING_QNAME).hasNext()) {
			Messaging messaging = MessagingMarshalling.getMessaging(jaxb2Marshaller, soapMessage);
			for (SignalMessage signalMessage : messaging.getSignalMessages()) {
				for (Error error : signalMessage.getErrors()) {
					logg.ebmserror(endpoint, getOrgNr(context), retning, error, signalMessage.getMessageInfo(), getMpcFromSignal(context, signalMessage), getConversationId(context), getInstanceIdentifier(context));
				}
			}
		}
	}

	// Private

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
		if (u.getAction().equals(PMode.Action.FORMIDLE_DIGITAL.value)) {
			return USERMESSAGE_SDP;
		} else if (u.getAction().equals(PMode.Action.FLYTT.value)) {
			return USERMESSAGE_FLYTT;
		} else if (u.getAction().equals(PMode.Action.FORMIDLE_FYSISK.value)) {
			return USERMESSAGE_FYSISK;
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
			return context.sbd.getConversationUuid();
		}
		return null;
	}

	private String getOrgNr(final EbmsContext context) {
		StringBuilder builder = new StringBuilder();
		builder.append(context.remoteParty.isPresent() ? context.remoteParty.toString() : "-");

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
			if (signalMessage.getErrors().size() == 1 && EMPTY_MESSAGE_PARTITION_CHANNEL_EBMS_ERROR_CODE.equals(signalMessage.getErrors().get(0).getErrorCode())) {
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
			if (sbd != null && sbd.getStandardBusinessDocumentHeader() != null) {
				context.sbd = new SimpleStandardBusinessDocument(sbd);
			}
		}
	}


}
