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
package no.digipost.api;

public interface PMode {

	// ROLES
	public static final String ROLE_MELDINGSFORMIDLER = "urn:sdp:meldingsformidler";
	public static final String ROLE_AVSENDER = "urn:sdp:avsender";
	public static final String ROLE_POSTKASSE = "urn:sdp:postkasseleverandør";

	// PARTY IDs
	public static final String PARTY_ID_TYPE = "urn:oasis:names:tc:ebcore:partyid-type:iso6523:9908";

	// COLLABORATION INFO
	public static final String FORMIDLING_DIGITAL_AGREEMENT_REF = "http://begrep.difi.no/SikkerDigitalPost/1.0/transportlag/Meldingsutveksling/FormidleDigitalPostForsendelse";
	public static final String FORMIDLING_FYSISK_AGREEMENT_REF = "http://begrep.difi.no/SikkerDigitalPost/1.0/transportlag/Meldingsutveksling/FormidleFysiskPostForsendelse";
	public static final String FLYTT_AGREEMENT_REF = "http://begrep.difi.no/SikkerDigitalPost/1.0/transportlag/Meldingsutveksling/FlyttetDigitalPost";
	public static final String SERVICE = "SDP";

	public enum Action {
		FORMIDLE_DIGITAL("FormidleDigitalPost", FORMIDLING_DIGITAL_AGREEMENT_REF),
		FORMIDLE_FYSISK("FormidleFysiskPost", FORMIDLING_FYSISK_AGREEMENT_REF),
		KVITTERING("KvitteringsForespoersel", FORMIDLING_DIGITAL_AGREEMENT_REF),
		FLYTT("FlyttetDigitalPost", FLYTT_AGREEMENT_REF);
		public final String value;
		public final String agreementRef;
		Action(final String value, final String agreementRef) {
			this.value = value;
			this.agreementRef = agreementRef;
		}

		public static Action fromValue(final String value) {
			for (Action action : values()) {
				if (action.value.equals(value)) {
					return action;
				}
			}
			return null;
		}
	}

}
