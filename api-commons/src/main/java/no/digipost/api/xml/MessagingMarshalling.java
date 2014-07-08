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
package no.digipost.api.xml;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

import java.util.Iterator;

import static no.digipost.api.xml.Constants.MESSAGING_QNAME;

public class MessagingMarshalling {

	/**
	 * Enten returnerer denne et Messaging objekt, eller så kaster den en RuntimeException
	 */
	public static Messaging getMessaging(final Jaxb2Marshaller jaxb2Marshaller, final WebServiceMessage message) {

		SoapHeader soapHeader = ((SoapMessage) message).getSoapHeader();
		if (soapHeader == null) {
			throw new RuntimeException("The ebMS header is missing (no SOAP header found in SOAP request)");
		}

		Iterator<SoapHeaderElement> soapHeaderElementIterator = soapHeader.examineHeaderElements(MESSAGING_QNAME);
		if (!soapHeaderElementIterator.hasNext()) {
			throw new RuntimeException("The ebMS header is missing in SOAP header");
		}

		SoapHeaderElement incomingSoapHeaderElement = soapHeaderElementIterator.next();
		try {
			return (Messaging) jaxb2Marshaller.unmarshal(incomingSoapHeaderElement.getSource());
		} catch (Exception e) {
			throw new RuntimeException("The ebMs header failed to unmarshall");
		}

	}

}
