/**
 * Copyright (C) Posten Norge AS
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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

public class ReferanseTest {

	@Test
	public void testBuilder() throws Exception {
		Referanse referanse = Referanse.builder(ObjectMother.getReference()).build();

		assertThat(referanse.getMarshaled()).isNotNull();
	}

	@Test
	public void testGetUnmarshaled() {
		Reference reference = ObjectMother.getReference();
		Referanse referanse = Referanse.builder(reference).build();

		assertThat(referanse.getUnmarshaled()).isEqualTo(reference);
	}
}