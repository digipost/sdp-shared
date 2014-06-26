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
import no.difi.begrep.sdp.schema_v10.SDPFeil;
import no.difi.begrep.sdp.schema_v10.SDPKvittering;
import no.difi.begrep.sdp.schema_v10.SDPMelding;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Scope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import java.util.List;

public class SimpleStandardBusinessDocument {

	private final StandardBusinessDocument doc;

    public SimpleStandardBusinessDocument(final StandardBusinessDocument doc) {
		this.doc = doc;
	}

	public String getInstanceIdentifier() {
		if (doc.getStandardBusinessDocumentHeader() == null || doc.getStandardBusinessDocumentHeader().getDocumentIdentification() == null) {
			return null;
		}
		return doc.getStandardBusinessDocumentHeader().getDocumentIdentification().getInstanceIdentifier();
	}

	public String getConversationId() {
		Scope scope = getScope();
		if (scope != null) {
			return scope.getInstanceIdentifier();
		}
		return null;
	}

	public void setConversationId(final String conversationId) {
		Scope scope = getScope();
		if (scope != null) {
			scope.setInstanceIdentifier(conversationId);
			return;
		}
		throw new IllegalStateException("Missing scope in SBDH");
	}

	public Scope getScope() {
		if (doc.getStandardBusinessDocumentHeader() != null && doc.getStandardBusinessDocumentHeader().getBusinessScope() != null) {
			List<Scope> scopes = doc.getStandardBusinessDocumentHeader().getBusinessScope().getScopes();
			if (!isEmpty(scopes)) {
				return scopes.get(0);
			}
		}
		return null;
	}

	private static boolean isEmpty(final List<?> list) {
		return list == null || list.size() == 0;
	}

	public boolean erKvittering() {
		return doc.getAny() instanceof SDPKvittering;
	}

	public boolean erDigitalPost() {
		return doc.getAny() instanceof SDPDigitalPost;
	}

	public boolean erFeil() {
		return doc.getAny() instanceof SDPFeil;
	}

	public Organisasjonsnummer getSender() {
		if (doc.getStandardBusinessDocumentHeader() == null || doc.getStandardBusinessDocumentHeader().getSenders() == null ||
				doc.getStandardBusinessDocumentHeader().getSenders().isEmpty() || doc.getStandardBusinessDocumentHeader().getSenders().get(0).getIdentifier() == null) {
			return null;
		}
		try {
			return Organisasjonsnummer.fromIso6523(doc.getStandardBusinessDocumentHeader().getSenders().get(0).getIdentifier().getValue());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	public Organisasjonsnummer getReceiver() {
		if (doc.getStandardBusinessDocumentHeader() == null || doc.getStandardBusinessDocumentHeader().getReceivers() == null ||
				doc.getStandardBusinessDocumentHeader().getReceivers().isEmpty() || doc.getStandardBusinessDocumentHeader().getReceivers().get(0).getIdentifier() == null) {
			return null;
		}
		try {
			return Organisasjonsnummer.fromIso6523(doc.getStandardBusinessDocumentHeader().getReceivers().get(0).getIdentifier().getValue());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public StandardBusinessDocument getUnderlyingDoc() {
		return doc;
	}

	public SDPFeil getFeil() {
		return (SDPFeil) doc.getAny();
	}

	public SimpleKvittering getKvittering() {
		return new SimpleKvittering((SDPKvittering)doc.getAny());
	}
	public SimpleDigitalPost getDigitalPost() {
		return new SimpleDigitalPost((SDPDigitalPost)doc.getAny());
	}
	public SDPMelding getMelding() {
		return (SDPMelding)doc.getAny();
	}

	public class SimpleKvittering {

		public final SDPKvittering kvittering;

		public SimpleKvittering(final SDPKvittering kvittering) {
			this.kvittering = kvittering;
		}

		public boolean erLevertTilPostkasse() {
			return kvittering.getLevering() != null;
		}

		public boolean erAapnet() {
			return kvittering.getAapning() != null;
		}
	}

	public class SimpleDigitalPost {

		public final SDPDigitalPost digitalPost;

		public SimpleDigitalPost(final SDPDigitalPost digitalPost) {
			this.digitalPost = digitalPost;
		}

		public boolean kreverAapningsKvittering() {
			return digitalPost.getDigitalPostInfo().getAapningskvittering() != null && digitalPost.getDigitalPostInfo().getAapningskvittering();
		}
	}

}
