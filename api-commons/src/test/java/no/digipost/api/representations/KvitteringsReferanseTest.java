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

import org.junit.Test;
import org.w3.xmldsig.Reference;

import static org.fest.assertions.api.Assertions.assertThat;

public class KvitteringsReferanseTest {

	@Test
	public void testBuilder_FromReference() throws Exception {
		Reference referenceToOriginalMessage = ObjectMother.getReference();

		KvitteringsReferanse kvitteringsReferanse = KvitteringsReferanse.builder(referenceToOriginalMessage).build();

		assertThat(kvitteringsReferanse.getMarshaled()).isNotNull();
	}

	@Test
	public void testBuilder_FromMarshaledReference() throws Exception {
		String marshaledReferenceToOriginalMessage = KvitteringsReferanse.builder(ObjectMother.getReference()).build().getMarshaled();

		KvitteringsReferanse kvitteringsReferanse = KvitteringsReferanse.builder(marshaledReferenceToOriginalMessage).build();

		assertThat(kvitteringsReferanse.getMarshaled()).isEqualTo(marshaledReferenceToOriginalMessage);
	}

	@Test
	public void testGetUnmarshaled() {
		Reference referenceToOriginalMessage = ObjectMother.getReference();

		KvitteringsReferanse kvitteringsReferanse = KvitteringsReferanse.builder(referenceToOriginalMessage).build();

		assertThat(kvitteringsReferanse.getUnmarshaled()).isEqualTo(referenceToOriginalMessage);
	}
}