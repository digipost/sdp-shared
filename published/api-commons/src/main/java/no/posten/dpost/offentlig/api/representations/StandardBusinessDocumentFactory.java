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
package no.posten.dpost.offentlig.api.representations;

import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.difi.begrep.sdp.schema_v10.SDPFeil;
import no.difi.begrep.sdp.schema_v10.SDPKvittering;
import no.difi.begrep.sdp.schema_v10.SDPMelding;
import no.difi.begrep.sdp.schema_v10.SDPTilbaketrekking;
import org.joda.time.DateTime;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.BusinessScope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.DocumentIdentification;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Partner;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.PartnerIdentification;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Scope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

public class StandardBusinessDocumentFactory {

	public enum Type {
		DigitalPost("digitalPost", SDPDigitalPost.class),
		Tilbaketrekking("tilbaketrekking", SDPTilbaketrekking.class),
		Kvittering("kvittering", SDPKvittering.class),
		Feil("feil", SDPFeil.class);

        private final String name;
		private final Class<? extends SDPMelding> clazz;

		private Type(final String name, final Class<? extends SDPMelding> clazz) {
			this.name = name;
			this.clazz = clazz;
		}

        @Override
		public String toString() {
			return name;
		}

        public static boolean isValid(final String type) {
			for(Type t : Type.values()) {
				if (t.name.equals(type)) {
					return true;
				}
			}
			return false;
		}

		public static Type from(final SDPMelding melding) {
			for(Type t : Type.values()) {
				if (t.clazz.isInstance(melding)) {
					return t;
				}
			}
			throw new IllegalArgumentException("Unsupported type of SDPMelding:" + melding.getClass());
		}
	}

	public static final String STANDARD = "urn:no:difi:sdp:1.0";
	public static final String HEADER_VERSION = "1.0";
	public static final String TYPE_VERSION = "1.0";
	public static final String CONVERSATIONID = "ConversationId";

	public static StandardBusinessDocument create(final Organisasjonsnummer avsender, final Organisasjonsnummer mottaker, final String instanceIdentifier, final String conversationId, final SDPMelding body) {
		return new StandardBusinessDocument()
			.withStandardBusinessDocumentHeader(
                    new StandardBusinessDocumentHeader()
                            .withHeaderVersion(HEADER_VERSION)
                            .withSenders(new Partner().withIdentifier(new PartnerIdentification(avsender.asIso6523(), Organisasjonsnummer.ISO6523_ACTORID)))
                            .withReceivers(new Partner().withIdentifier(new PartnerIdentification(mottaker.asIso6523(), Organisasjonsnummer.ISO6523_ACTORID)))
                            .withDocumentIdentification(new DocumentIdentification()
                                    .withStandard(STANDARD)
                                    .withTypeVersion(TYPE_VERSION)
                                    .withInstanceIdentifier(instanceIdentifier)
                                    .withType(Type.from(body).toString())
                                    .withCreationDateAndTime(DateTime.now())
                            )
                            .withBusinessScope(new BusinessScope()
                                    .withScopes(new Scope()
                                            .withIdentifier(STANDARD)
                                            .withType(CONVERSATIONID)
                                            .withInstanceIdentifier(conversationId)
                                    )
                            )
            )
			.withAny(body);
	}

}
