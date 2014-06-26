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

import no.difi.begrep.sdp.schema_v10.SDPFeil;
import no.digipost.api.exceptions.ebms.standard.processing.InvalidHeaderException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.oxm.MarshallingFailureException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.BusinessScope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.DocumentIdentification;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Partner;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.PartnerIdentification;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Scope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;
import org.w3.xmldsig.CanonicalizationMethod;
import org.w3.xmldsig.DigestMethod;
import org.w3.xmldsig.Reference;
import org.w3.xmldsig.Signature;
import org.w3.xmldsig.SignatureMethod;
import org.w3.xmldsig.SignatureValue;
import org.w3.xmldsig.SignedInfo;
import org.w3.xmldsig.Transform;
import org.w3.xmldsig.Transforms;

import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static no.difi.begrep.sdp.schema_v10.SDPFeiltype.KLIENT;
import static no.digipost.api.xml.Constants.MESSAGING_QNAME;
import static org.joda.time.DateTime.now;
import static org.mockito.Mockito.when;

public class MarshallingTest {

	private final Jaxb2Marshaller jaxb2Marshaller = Marshalling.createUnManaged();

	@Mock
	private SoapMessage soapMessage;

	@Mock
	private SoapHeader soapHeader;


	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = InvalidHeaderException.class)
	public void manglende_soap_header_skal_kaste_invalid_header() {
		Marshalling.getMessaging(jaxb2Marshaller, soapMessage);
	}

	@Test(expected = InvalidHeaderException.class)
	public void manglende_ebms_header_skal_kaste_invalid_header() {
		when(soapMessage.getSoapHeader()).thenReturn(soapHeader);
		List<SoapHeaderElement> soapHeaderElements = new ArrayList();
		when(soapHeader.examineHeaderElements(MESSAGING_QNAME)).thenReturn(soapHeaderElements.iterator());
		Marshalling.getMessaging(jaxb2Marshaller, soapMessage);
	}


	@Test
	public void marshalling_av_gyldig_SBD_skal_ikke_feile() {
		StringWriter outWriter = new StringWriter();
		StreamResult result = new StreamResult(outWriter);
		SDPFeil sdpFeil = new SDPFeil()
				.withSignature(new Signature()
						.withSignedInfo(
								new SignedInfo()
										.withCanonicalizationMethod(
												new CanonicalizationMethod()
														.withAlgorithm("algo")
										)
										.withSignatureMethod(
												new SignatureMethod()
														.withAlgorithm("algo")
										)
										.withReferences(
												new Reference()
														.withTransforms(
																new Transforms()
																		.withTransforms(
																				new Transform()
																						.withAlgorithm("algo")
																		)
														)
														.withDigestMethod(
																new DigestMethod().withAlgorithm("algo"))
														.withDigestValue(new byte[0])
										)
						)
						.withSignatureValue(
								new SignatureValue()))
				.withDetaljer("Detaljer")
				.withFeiltype(KLIENT)
				.withTidspunkt(now());
		StandardBusinessDocument sbd = createValidStandardBusinessDocument(sdpFeil);
		jaxb2Marshaller.marshal(sbd, result);
	}

	@Test(expected = MarshallingFailureException.class)
	public void marshalling_av_ugyldig_SBD_skal_feile() {
		StringWriter outWriter = new StringWriter();
		StreamResult result = new StreamResult(outWriter);
		StandardBusinessDocument sbd = createInvalidStandardBusinessDocument();
		jaxb2Marshaller.marshal(sbd, result);
	}


	private StandardBusinessDocument createValidStandardBusinessDocument(Object any) {
		Partner partner = new Partner().withIdentifier(new PartnerIdentification("parner", "authority"));

		return new StandardBusinessDocument()
				.withStandardBusinessDocumentHeader(
						new StandardBusinessDocumentHeader()
								.withHeaderVersion("1.0")
								.withSenders(partner)
								.withReceivers(partner)
								.withDocumentIdentification(
										new DocumentIdentification()
												.withStandard("standard")
												.withTypeVersion("typeVersion")
												.withInstanceIdentifier("instanceIdentifier")
												.withType("type")
												.withCreationDateAndTime(new DateTime())
								)
								.withBusinessScope(new BusinessScope()
										.withScopes(new Scope()
														.withType("type")
														.withInstanceIdentifier("instanceIdentifier").withIdentifier("identifier")
														.withIdentifier("identifier")
										))
				).withAny(any);
	}

	private StandardBusinessDocument createInvalidStandardBusinessDocument() {
		Partner partner = new Partner().withIdentifier(new PartnerIdentification("parner", "authority"));

		return new StandardBusinessDocument()
				.withStandardBusinessDocumentHeader(
						new StandardBusinessDocumentHeader()
								.withHeaderVersion("1.0")
								.withSenders(partner)
								.withReceivers(partner)
								.withDocumentIdentification(
										new DocumentIdentification()
												.withStandard("standard")
												.withTypeVersion("typeVersion")
								)
				);
	}

}
