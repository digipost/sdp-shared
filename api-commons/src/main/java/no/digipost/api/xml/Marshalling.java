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
import no.difi.begrep.sdp.schema_v10.SDPManifest;
import org.etsi.uri._01903.v1_3.QualifyingProperties;
import org.etsi.uri._2918.v1_2.XAdESSignatures;
import org.oasis_open.docs.ebxml_bp.ebbp_signals_2.NonRepudiationInformation;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.w3.xmldsig.X509Data;
import org.w3c.dom.Document;
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
import java.util.Iterator;

import static no.digipost.api.xml.Schemas.*;

public class Marshalling {

	// Use when spring managed
	public static Jaxb2Marshaller createManaged() {
		return create(false);
	}

	// Use when not spring managed
	public static Jaxb2Marshaller createUnManaged() {
		return create(true);
	}

	private static Jaxb2Marshaller create(final boolean runAfterPropertiesSet) {
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
						packageName(SDPManifest.class),
						packageName(XAdESSignatures.class),
						packageName(X509Data.class),
						packageName(QualifyingProperties.class)
				}
		);
		marshaller.setSchemas(SDP_SCHEMA, SBDH_SCHEMA, EBMS_SCHEMA, XMLDSIG_SCHEMA, XADES_SCHEMA, ASICE_SCHEMA);
		if (runAfterPropertiesSet) {
			try {
				marshaller.afterPropertiesSet();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
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

	public static Messaging getMessaging(final Jaxb2Marshaller jaxb2Marshaller, final WebServiceMessage message) {
		Iterator<SoapHeaderElement> soapHeaderElementIterator = ((SoapMessage) message).getSoapHeader().examineHeaderElements(Constants.MESSAGING_QNAME);
		if (!soapHeaderElementIterator.hasNext()) {
			throw new RuntimeException("Missing required EBMS SOAP header");
		}
		SoapHeaderElement incomingSoapHeaderElement = soapHeaderElementIterator.next();

		return (Messaging) jaxb2Marshaller.unmarshal(incomingSoapHeaderElement.getSource());
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
}
