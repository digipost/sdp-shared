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

import org.fest.assertions.core.Condition;
import org.junit.Test;
import org.w3.xmldsig.Reference;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class EbmsApplikasjonsKvitteringTest {

	@Test
	public void testGetMeldingsId() throws Exception {
		EbmsApplikasjonsKvittering ebmsApplikasjonskvittering = getEbmsApplikasjonskvittering();

		assertThat(ebmsApplikasjonskvittering.getMeldingsId()).isEqualTo(ebmsApplikasjonskvittering.messageId);

	}

	@Test
	public void testGetReferanseTilMeldingSomKvitteres() throws Exception {
		KvitteringsReferanse referanseTilMeldingSomKvitteres = getEbmsApplikasjonskvittering().getReferanseTilMeldingSomKvitteres();

		assertThat(referanseTilMeldingSomKvitteres.getMarshalled()).has(lengthGreaterThan(500));
	}

	private Condition<String> lengthGreaterThan(final int length) {
		return new Condition<String>() {
			@Override
			public boolean matches(String s) {
				return s.length() > length;
			}
		};
	}

	private EbmsApplikasjonsKvittering getEbmsApplikasjonskvittering() {
		List<Reference> references = new ArrayList<Reference>();
		references.add(ObjectMother.getReference());

		return EbmsApplikasjonsKvittering.create(EbmsAktoer.avsender("999999999"), EbmsAktoer.avsender("88888888"), null)
				.withReferences(references)
				.build();
	}

}