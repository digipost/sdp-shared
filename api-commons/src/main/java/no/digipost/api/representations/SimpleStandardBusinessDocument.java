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

import no.difi.begrep.sdp.schema_v10.*;
import no.digipost.xsd.types.DigitalPostformidling;
import org.joda.time.DateTime;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Scope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.w3.xmldsig.Reference;

import java.util.List;

import static no.digipost.api.util.Choice.choice;
import static no.digipost.api.util.Converters.toDateTimeAtStartOfDay;
import static org.apache.commons.lang3.StringUtils.join;

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

	public boolean erFlyttetDigitalPost() {
		return doc.getAny() instanceof SDPFlyttetDigitalPost;
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
	public SimpleDigitalPostformidling getDigitalPostformidling() {
		return new SimpleDigitalPostformidling((DigitalPostformidling) doc.getAny());
	}
	public SDPMelding getMelding() {
		return (SDPMelding)doc.getAny();
	}

	public class SimpleKvittering {

		public final SDPKvittering kvittering;

		public SimpleKvittering(final SDPKvittering kvittering) {
			this.kvittering = kvittering;
		}

		public boolean erMottak() {
			return kvittering.getMottak() != null;
		}

		public boolean erLevertTilPostkasse() {
			return kvittering.getLevering() != null;
		}

		public boolean erAapnet() {
			return kvittering.getAapning() != null;
		}

		public boolean erVarslingFeilet() {
			return kvittering.getVarslingfeilet() != null;
		}

		public boolean erReturpost() {
			return kvittering.getReturpost() != null;
		}

		public SDPVarslingfeilet getVarslingFeilet() {
			return kvittering.getVarslingfeilet();
		}

	}


	public static class SimpleDigitalPostformidling {

		public static enum Type {
			NY_POST(SDPDigitalPost.class),
			FLYTTET(SDPFlyttetDigitalPost.class);

			private final Class<? extends DigitalPostformidling> associatedClass;

			Type(final Class<? extends DigitalPostformidling> associatedClass) {
				this.associatedClass = associatedClass;
			}

			public boolean isInstance(final DigitalPostformidling melding) {
				return associatedClass.isInstance(melding);
			}

			public <T extends DigitalPostformidling> T validateInstance(final T candidate) {
				if (isInstance(candidate)) {
					return candidate;
				} else {
					Type type = Type.of(candidate);
					throw new IllegalArgumentException(
							candidate.getClass().getName() + " er ikke av forventet type " +
							this + ", men ble gjenkjent som " + type);
				}
			}

			public static Type of(final DigitalPostformidling melding) {
				for (Type type : values()) {
	                if (type.isInstance(melding)) {
						return type;
					}
                }
				throw new IllegalArgumentException(
						DigitalPostformidling.class.getSimpleName() + " av type " +	melding.getClass().getName() +
						"ble ikke gjenkjent som noen av [" + join(values(), ", ") + "]");
			}
		}

		public final Type type;
		private final DigitalPostformidling digitalPostformidling;

        public SimpleDigitalPostformidling(final DigitalPostformidling digitalPostformidling) {
			type = Type.of(digitalPostformidling);
			this.digitalPostformidling = digitalPostformidling;
		}

		public SDPDigitalPost getDigitalPost() {
			return (SDPDigitalPost) Type.NY_POST.validateInstance(digitalPostformidling);
		}

		public SDPFlyttetDigitalPost getFlyttetDigitalPost() {
			return (SDPFlyttetDigitalPost) Type.FLYTTET.validateInstance(digitalPostformidling);
		}

		public boolean kreverAapningsKvittering() {
			SDPDigitalPostInfo postinfo = getDigitalPostInfo();
			return postinfo != null ? postinfo.getAapningskvittering() : false;
		}

		public SDPAvsender getAvsender() {
			return digitalPostformidling.getAvsender();
		}

		public SDPMottaker getMottaker() {
			return digitalPostformidling.getMottaker();
        }

		public Reference getDokumentpakkefingeravtrykk() {
			return digitalPostformidling.getDokumentpakkefingeravtrykk();
        }

		public SDPDigitalPostInfo getDigitalPostInfo() {
			return digitalPostformidling.getDigitalPostInfo();
        }

		public boolean erDigitalPostTilFysiskLevering() {
			return digitalPostformidling instanceof SDPDigitalPost && ((SDPDigitalPost) digitalPostformidling).getFysiskPostInfo() != null;
		}

		public SDPFysiskPostInfo getFysiskPostInfo() {
			return ((SDPDigitalPost) digitalPostformidling).getFysiskPostInfo();
		}

		public DateTime getLeveringstidspunkt() {
			SDPDigitalPostInfo postinfo = getDigitalPostInfo();
			DateTime virkningstidspunkt = postinfo != null ? choice(postinfo.getVirkningstidspunkt(), postinfo.getVirkningsdato(), toDateTimeAtStartOfDay) : null;
			DateTime leveringstidspunkt = null;

			if(virkningstidspunkt != null) {
				leveringstidspunkt = virkningstidspunkt;
			}

			if(type == Type.FLYTTET) {
				DateTime mottakstidspunkt = getFlyttetDigitalPost().getMottaksdato().toDateTimeAtStartOfDay();
				if (leveringstidspunkt == null) {
					leveringstidspunkt = mottakstidspunkt;
				} else if (mottakstidspunkt.isAfter(leveringstidspunkt)) {
					leveringstidspunkt = mottakstidspunkt;
				}
			}

			return leveringstidspunkt;
		}

		public boolean erAlleredeAapnet() {
			return type == Type.FLYTTET ? getFlyttetDigitalPost().isAapnet() : false;
		}

	}

}
