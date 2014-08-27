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

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public interface PMode {

	// ROLES
	public static final String ROLE_MELDINGSFORMIDLER = "urn:sdp:meldingsformidler";
	public static final String ROLE_AVSENDER = "urn:sdp:avsender";
	public static final String ROLE_POSTKASSE = "urn:sdp:postkasseleverandør";

	// PARTY IDs
	public static final String PARTY_ID_TYPE = "urn:oasis:names:tc:ebcore:partyid-type:iso6523:9908";

	// COLLABORATION INFO
	public static final String FORMIDLING_AGREEMENT_REF_OLD = "http://begrep.difi.no/SikkerDigitalPost/transportlag/Meldingsutveksling/FormidleDigitalPostForsendelse";
	public static final String FORMIDLING_AGREEMENT_REF = "http://begrep.difi.no/SikkerDigitalPost/1.0/transportlag/Meldingsutveksling/FormidleDigitalPostForsendelse";
	public static final String FLYTT_AGREEMENT_REF = "http://begrep.difi.no/SikkerDigitalPost/1.0/transportlag/Meldingsutveksling/FlyttDigitalPost";
	public static final String SERVICE = "SDP";

	public enum Action {
		FORMIDLE("FormidleDigitalPost"),
		KVITTERING("KvitteringsForespoersel"),
		FLYTT("FlyttetDigitalPost");
		public final String value;
		Action(String value) { this.value = value; }

		public static Action fromValue(String value) {
			for (Action action : values()) {
				if (action.value.equals(value)) {
					return action;
				}
			}
			return null;
		}
	}

	public static final Set<String> VALID_AGREEMENTS = new HashSet(asList(FORMIDLING_AGREEMENT_REF, FLYTT_AGREEMENT_REF, FORMIDLING_AGREEMENT_REF_OLD));

}
