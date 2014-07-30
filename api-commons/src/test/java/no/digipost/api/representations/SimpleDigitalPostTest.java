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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SimpleDigitalPostTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	SimpleDigitalPostleveranse nyPost = new SimpleDigitalPostleveranse(new SDPDigitalPost());
	SimpleDigitalPostleveranse tilFlytting = new SimpleDigitalPostleveranse(new SDPFlyttetDigitalPost());

	@Test
    public void kanIkkeHenteUtFlyttetDigitalPostNaarTypeErNY_POST() {
		assertEquals(Type.NY_POST, nyPost.type);
		expectedException.expect(IllegalStateException.class);
		nyPost.getFlyttetDigitalPost();
    }

	@Test
	public void kanIkkeHenteUtDigitalPostNaarTypeErFLYTTET() {
		assertEquals(Type.FLYTTET, tilFlytting.type);
		expectedException.expect(IllegalStateException.class);
		tilFlytting.getDigitalPost();
	}

	@Test
    public void kanHenteUtUlikeDigitalPostMeldinger() {
		assertNotNull(nyPost.getDigitalPost());
		assertNotNull(tilFlytting.getFlyttetDigitalPost());
    }
}
