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
package no.digipost.api.api.interceptors.steps;

import no.digipost.api.api.representations.EbmsContext;
import no.digipost.api.xml.Constants;
import no.digipost.api.xml.Marshalling;
import no.digipost.api.api.representations.EbmsContext;
import no.digipost.api.api.representations.EbmsProcessingStep;
import no.digipost.api.xml.Marshalling;
import org.joda.time.DateTime;
import org.oasis_open.docs.ebxml_bp.ebbp_signals_2.MessagePartNRInformation;
import org.oasis_open.docs.ebxml_bp.ebbp_signals_2.NonRepudiationInformation;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Receipt;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.w3.xmldsig.Reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static no.digipost.api.xml.Constants.SIGNAL_MESSAGE_QNAME;

public class AddReferencesStep implements EbmsProcessingStep {

	public final Collection<Reference> references;
	private final Jaxb2Marshaller jaxb2Marshaller;
	private final String messageId;

	public AddReferencesStep(final Jaxb2Marshaller jaxb2Marshaller, final String messageId, final Collection<Reference> references) {
		this.jaxb2Marshaller = jaxb2Marshaller;
		this.messageId = messageId;
		this.references = references == null ? new ArrayList<Reference>() : references;
	}

	@Override
	public void apply(final EbmsContext ebmsContext, final SoapHeaderElement ebmsMessaging, final SoapMessage soapMessage) {
		List<MessagePartNRInformation> nrInfos = new ArrayList<MessagePartNRInformation>();
		for (Reference ref : references) {
			nrInfos.add(new MessagePartNRInformation().withReference(ref));
		}

		Receipt receipt = new Receipt()
				.withAnies(new NonRepudiationInformation()
						.withMessagePartNRInformations(nrInfos));
		SignalMessage signalMessage = new SignalMessage()
				.withMessageInfo(new MessageInfo(
						DateTime.now(),
						UUID.randomUUID().toString(),
						messageId))
				.withReceipt(receipt);
		Marshalling.marshal(jaxb2Marshaller, ebmsMessaging, Constants.SIGNAL_MESSAGE_QNAME, signalMessage);
	}

}
