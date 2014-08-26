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
package no.digipost.api.handlers;

import no.digipost.api.exceptions.MessageSenderIOException;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.transport.http.HttpComponentsConnection;

import java.io.IOException;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EbmsContextAwareWebServiceTemplateTest {

	@Mock
	private WebServiceMessage requestMock;

	@Mock
	private HttpComponentsConnection connectionMock;

	@Mock
	private HttpResponse mockResponse;

	@Before
	public void setUp() {
		initMocks(this);
	}

	@Test(expected = MessageSenderIOException.class)
	public void skal_ikke_feile_selv_om_entity_mangler() throws IOException {
		EbmsContextAwareWebServiceTemplate template = new EbmsContextAwareWebServiceTemplate(null, null);
		when(connectionMock.getHttpResponse()).thenReturn(mockResponse);
		template.handleError(connectionMock, requestMock);
	}

}
