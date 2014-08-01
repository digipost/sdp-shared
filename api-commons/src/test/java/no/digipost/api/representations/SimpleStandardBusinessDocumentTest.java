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
package no.digipost.api.representations;

import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.difi.begrep.sdp.schema_v10.SDPFlyttetDigitalPost;
import no.digipost.api.representations.SimpleStandardBusinessDocument.SimpleDigitalPostleveranse;
import no.digipost.api.representations.SimpleStandardBusinessDocument.SimpleDigitalPostleveranse.Type;
import no.digipost.xsd.types.Postleveranse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class SimpleStandardBusinessDocumentTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
    public void girDigitalPostleveranseMedTypeNY_POST() {
		SimpleDigitalPostleveranse post = new SimpleStandardBusinessDocument(new StandardBusinessDocument().withAny(new SDPDigitalPost())).getDigitalPostleveranse();
		assertThat(post.type, is(Type.NY_POST));
    }

	@Test
    public void girDigitalPostleveranseMedTypeFLYTTET() {
		SimpleDigitalPostleveranse post = new SimpleStandardBusinessDocument(new StandardBusinessDocument().withAny(new SDPFlyttetDigitalPost())).getDigitalPostleveranse();
		assertThat(post.type, is(Type.FLYTTET));
    }

	@Test
	public void feilerDersomUkjentTypeDigitalPostMelding() {
		SimpleStandardBusinessDocument sbd = new SimpleStandardBusinessDocument(new StandardBusinessDocument().withAny(mock(Postleveranse.class)));
		expectedException.expectMessage("er ikke en gyldig type");
		sbd.getDigitalPostleveranse();
	}
}
