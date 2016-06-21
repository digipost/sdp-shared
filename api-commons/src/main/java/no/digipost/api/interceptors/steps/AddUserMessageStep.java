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
package no.digipost.api.interceptors.steps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import no.digipost.api.PMode;
import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.EbmsContext;
import no.digipost.api.representations.EbmsProcessingStep;
import no.digipost.api.representations.Mpc;
import no.digipost.api.representations.SimpleStandardBusinessDocument;
import no.digipost.api.xml.Constants;
import no.digipost.api.xml.Marshalling;

import org.joda.time.DateTime;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.AgreementRef;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.CollaborationInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.mime.Attachment;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

public class AddUserMessageStep implements EbmsProcessingStep {

	private final EbmsAktoer tekniskAvsender;
	private final EbmsAktoer mottaker;
	private final Jaxb2Marshaller marshaller;
	private final Mpc mpc;
	private final String messageId;
	private final PMode.Action action;
	private final String refToMessageId;
	private final String instanceIdentifier;

	public AddUserMessageStep(final Mpc mpc, final String messageId, final PMode.Action action, final String refToMessageId, final StandardBusinessDocument doc, final EbmsAktoer tekniskAvsender, final EbmsAktoer mottaker, final Jaxb2Marshaller marshaller) {
		this.mpc = mpc;
		this.messageId = messageId;
		this.action = action;
		this.refToMessageId = refToMessageId;
		this.tekniskAvsender = tekniskAvsender;
		this.mottaker = mottaker;
		this.marshaller = marshaller;
		instanceIdentifier = new SimpleStandardBusinessDocument(doc).getInstanceIdentifier();
	}

	@Override
	public void apply(final EbmsContext ebmsContext, final SoapHeaderElement ebmsMessaging, final SoapMessage soapMessage) {
		PartyInfo partyInfo = new PartyInfo(
				new From().withRole(tekniskAvsender.rolle.urn)
						.withPartyIds(new PartyId(tekniskAvsender.orgnr.medLandkode(), PMode.PARTY_ID_TYPE)),
				new To().withRole(mottaker.rolle.urn)
						.withPartyIds(new PartyId(mottaker.orgnr.medLandkode(), PMode.PARTY_ID_TYPE))
		);
		UserMessage userMessage = new UserMessage()
				.withMpc(mpc.toString())
				.withMessageInfo(createMessageInfo())
				.withCollaborationInfo(createCollaborationInfo())
				.withPartyInfo(partyInfo);
		addPartInfo(soapMessage, userMessage);
		Marshalling.marshal(marshaller, ebmsMessaging, Constants.USER_MESSAGE_QNAME, userMessage);
	}

	private CollaborationInfo createCollaborationInfo() {
		return new CollaborationInfo()
				.withAction(action.value)
				.withAgreementRef(new AgreementRef()
						.withValue(action.agreementRef))
				.withConversationId(instanceIdentifier)
				.withService(new Service().withValue(PMode.SERVICE));
	}

	private void addPartInfo(final SoapMessage requestMessage, final UserMessage userMessage) {
		List<PartInfo> parts = new ArrayList<PartInfo>();
		parts.add(new PartInfo());
		Iterator<Attachment> attachments = requestMessage.getAttachments();
		while (attachments.hasNext()) {
			Attachment attachment = attachments.next();
			String cid = "cid:" + attachment.getContentId().replace("<", "").replace(">", "");
			parts.add(createPartInfo(cid, attachment.getContentType(), "sdp:Dokumentpakke"));
		}
		userMessage.setPayloadInfo(new PayloadInfo().withPartInfos(parts));
	}

	private PartInfo createPartInfo(final String href, final String mimeType, final String content) {
		return new PartInfo()
				.withHref(href)
				.withPartProperties(new PartProperties()
								.withProperties(new Property(mimeType, "MimeType"), new Property(content, "Content"))
				);

	}

	private MessageInfo createMessageInfo() {
		return new MessageInfo()
				.withMessageId(messageId)
				.withRefToMessageId(refToMessageId)
				.withTimestamp(DateTime.now());
	}

}
