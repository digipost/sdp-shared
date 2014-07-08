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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

import java.util.ArrayList;
import java.util.List;

import static no.digipost.api.xml.Constants.MESSAGING_QNAME;
import static org.mockito.Mockito.when;

public class MessagingMarshallingTest {

	private final Jaxb2Marshaller jaxb2Marshaller = Marshalling.getMarshallerSingleton();

	@Mock
	private SoapMessage soapMessage;

	@Mock
	private SoapHeader soapHeader;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = RuntimeException.class)
	public void manglende_soap_header_skal_kaste_runtime_exception() {
		MessagingMarshalling.getMessaging(jaxb2Marshaller, soapMessage);
	}

	@Test(expected = RuntimeException.class)
	public void manglende_ebms_header_skal_runtime_exception() {
		when(soapMessage.getSoapHeader()).thenReturn(soapHeader);
		List<SoapHeaderElement> soapHeaderElements = new ArrayList<SoapHeaderElement>();
		when(soapHeader.examineHeaderElements(MESSAGING_QNAME)).thenReturn(soapHeaderElements.iterator());
		MessagingMarshalling.getMessaging(jaxb2Marshaller, soapMessage);
	}
}
