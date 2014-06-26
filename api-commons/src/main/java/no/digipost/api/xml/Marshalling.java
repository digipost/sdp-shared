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

import no.difi.begrep.sdp.schema_v10.SDPKvittering;
import no.digipost.api.exceptions.ebms.standard.processing.InvalidHeaderException;
import org.etsi.uri._01903.v1_3.QualifyingProperties;
import org.etsi.uri._2918.v1_2.XAdESSignatures;
import org.oasis_open.docs.ebxml_bp.ebbp_signals_2.NonRepudiationInformation;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xmlsoap.schemas.soap.envelope.Envelope;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static no.digipost.api.xml.Constants.MESSAGING_QNAME;

public class Marshalling {

	private static class MarshallerForContainerManagement {
		private static final Jaxb2Marshaller instance = create();
	}

	private static class FullyInitializedMarshaller {
		private static final Jaxb2Marshaller instance = create(); static {
			try {
				instance.afterPropertiesSet();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	// Use when spring managed
	public static Jaxb2Marshaller getManaged() {
		return MarshallerForContainerManagement.instance;
	}

	// Use when not spring managed
	public static Jaxb2Marshaller getUnManaged() {
		return FullyInitializedMarshaller.instance;
	}

	private static Jaxb2Marshaller create() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setPackagesToScan(
				new String[]{
						packageName(StandardBusinessDocument.class),
						packageName(Envelope.class),
						packageName(org.w3.soap.Envelope.class),
						packageName(org.w3.xmldsig.Reference.class),
						packageName(Messaging.class),
						packageName(NonRepudiationInformation.class),
						packageName(SDPKvittering.class),
						packageName(XAdESSignatures.class),
						packageName(QualifyingProperties.class)
				}
		);
		marshaller.setSchemas(Schemas.schemaResources());
		return marshaller;
	}

	private static String packageName(final Class<?> jaxbClass) {
		return jaxbClass.getPackage().getName();
	}

	public static void marshal(final Jaxb2Marshaller jaxb2Marshaller, final SoapHeaderElement header, final QName qName, final Object element) {
		marshal(jaxb2Marshaller, wrap(qName, element), header.getResult());
	}

	public static void marshal(final Jaxb2Marshaller jaxb2Marshaller, final SoapHeaderElement header, final Object element) {
		marshal(jaxb2Marshaller, element, header.getResult());
	}

	public static void marshal(final Jaxb2Marshaller jaxb2Marshaller, final SoapBody body, final Object element) {
		marshal(jaxb2Marshaller, element, body.getPayloadResult());
	}

	public static void marshal(final Jaxb2Marshaller jaxb2Marshaller, final Object element, final Result payloadResult) {
		try {
			JAXBSource jaxbSource = new JAXBSource(jaxb2Marshaller.getJaxbContext().createMarshaller(), element);
			TransformerUtil.transform(jaxbSource, payloadResult);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public static void marshal(final Document doc, final Result payloadResult) {
		DOMSource source = new DOMSource(doc);
		TransformerUtil.transform(source, payloadResult);
	}

	/**
	 * Enten returnerer denne et Messaging objekt, eller så kaster den en InvalidHeaderException
	 */
	public static Messaging getMessaging(final Jaxb2Marshaller jaxb2Marshaller, final WebServiceMessage message) {

		SoapHeader soapHeader = ((SoapMessage) message).getSoapHeader();
		if (soapHeader == null) {
			throw new InvalidHeaderException(null, "The ebMS header is missing (no SOAP header found in SOAP request)");
		}

		Iterator<SoapHeaderElement> soapHeaderElementIterator = soapHeader.examineHeaderElements(MESSAGING_QNAME);
		if (!soapHeaderElementIterator.hasNext()) {
			throw new InvalidHeaderException(null, "The ebMS header is missing in SOAP header");
		}

		SoapHeaderElement incomingSoapHeaderElement = soapHeaderElementIterator.next();
		try {
			return (Messaging) jaxb2Marshaller.unmarshal(incomingSoapHeaderElement.getSource());
		} catch (Exception e) {
			throw new InvalidHeaderException();
		}

	}

	public static <T> T unmarshal(final Jaxb2Marshaller jaxb2Marshaller, final Node node, final Class<T> clazz) {
		try {
			JAXBElement<T> jaxbElement = jaxb2Marshaller.getJaxbContext().createUnmarshaller().unmarshal(node, clazz);
			return jaxbElement.getValue();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T unmarshal(final Jaxb2Marshaller jaxb2Marshaller, final SoapHeaderElement header, final Class<T> clazz) {
		try {
			JAXBElement<T> jaxbElement = jaxb2Marshaller.getJaxbContext().createUnmarshaller().unmarshal(header.getSource(), clazz);
			return jaxbElement.getValue();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T unmarshal(final Jaxb2Marshaller jaxb2Marshaller, final SoapBody body, final Class<T> clazz) {
		try {
			JAXBElement<T> jaxbElement = jaxb2Marshaller.getJaxbContext().createUnmarshaller().unmarshal(body.getPayloadSource(), clazz);
			return jaxbElement.getValue();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T unmarshal(final Jaxb2Marshaller jaxb2Marshaller, final InputStream is, final Class<T> clazz) {
		try {
			JAXBElement<T> jaxbElement = jaxb2Marshaller.getJaxbContext().createUnmarshaller().unmarshal(new StreamSource(is), clazz);
			return jaxbElement.getValue();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> JAXBElement<T> wrap(final QName qName, final T object) {
		return new JAXBElement<T>(qName, (Class<T>) object.getClass(), object);
	}

	public static void trimNamespaces(final Document doc) {
		NamedNodeMap attributes = doc.getDocumentElement().getAttributes();
		List<Attr> attrsToRemove = new ArrayList<Attr>();
		for (int i = 0; i < attributes.getLength(); i++) {
			if (doc.getElementsByTagNameNS(attributes.item(i).getNodeValue(), "*").getLength() == 0) {
				attrsToRemove.add((Attr) attributes.item(i));
			}
		}
		for (Attr a : attrsToRemove) {
			doc.getDocumentElement().removeAttributeNode(a);
		}
	}
}
