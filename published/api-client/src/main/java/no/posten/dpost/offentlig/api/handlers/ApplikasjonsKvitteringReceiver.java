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
package no.posten.dpost.offentlig.api.handlers;

import no.posten.dpost.offentlig.api.representations.EbmsApplikasjonsKvittering;
import no.posten.dpost.offentlig.api.representations.Organisasjonsnummer;
import no.posten.dpost.offentlig.xml.Marshalling;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import javax.xml.transform.TransformerException;

import java.io.IOException;

public class ApplikasjonsKvitteringReceiver extends EbmsContextAware implements WebServiceMessageExtractor<EbmsApplikasjonsKvittering> {

	private final Jaxb2Marshaller jaxb2Marshaller;

	public ApplikasjonsKvitteringReceiver(final Jaxb2Marshaller jaxb2Marshaller) {
		this.jaxb2Marshaller = jaxb2Marshaller;
	}

	@Override
	public EbmsApplikasjonsKvittering extractData(final WebServiceMessage message) throws IOException, TransformerException {
		SoapBody soapBody = ((SaajSoapMessage)message).getSoapBody();
		StandardBusinessDocument sbd = Marshalling.unmarshal(jaxb2Marshaller, soapBody, StandardBusinessDocument.class);
		PartyInfo partyInfo = ebmsContext.userMessage.getPartyInfo();
		String avsender = partyInfo.getFrom().getPartyIds().get(0).getValue();
		String mottaker = partyInfo.getTo().getPartyIds().get(0).getValue();

		return EbmsApplikasjonsKvittering.create(new Organisasjonsnummer(avsender), new Organisasjonsnummer(mottaker), sbd)
				.withMessageId(ebmsContext.userMessage.getMessageInfo().getMessageId())
				.withRefToMessageId(ebmsContext.userMessage.getMessageInfo().getRefToMessageId())
				.withReferences(ebmsContext.incomingReferences)
				.build();
	}

}
