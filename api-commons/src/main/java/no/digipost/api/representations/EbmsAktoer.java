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

import no.digipost.api.PMode;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To;

public class EbmsAktoer {

	public enum Rolle {
		AVSENDER(PMode.ROLE_AVSENDER),
		MELDINGSFORMIDLER(PMode.ROLE_MELDINGSFORMIDLER),
		POSTKASSE(PMode.ROLE_POSTKASSE);

		public final String urn;

		private Rolle(final String urn) {
			this.urn = urn;
		}

		public static Rolle parse(final String urn) {
			for (Rolle r : Rolle.values()) {
				if (r.urn.equals(urn)) {
					return r;
				}
			}
			throw new IllegalArgumentException("Invalid role:" + urn);
		}
	}

	public final Organisasjonsnummer orgnr;
	public final Rolle rolle;

	public EbmsAktoer(final Organisasjonsnummer orgnr, final Rolle rolle) {
		this.orgnr = orgnr;
		this.rolle = rolle;
	}

	public static EbmsAktoer meldingsformidler(final String orgnr) {
		return meldingsformidler(Organisasjonsnummer.of(orgnr));
	}

	public static EbmsAktoer meldingsformidler(final Organisasjonsnummer orgnr) {
		return new EbmsAktoer(orgnr, Rolle.MELDINGSFORMIDLER);
	}

	public static EbmsAktoer avsender(final String orgnr) {
		return avsender(Organisasjonsnummer.of(orgnr));
	}

	public static EbmsAktoer avsender(final Organisasjonsnummer orgnr) {
		return new EbmsAktoer(orgnr, Rolle.AVSENDER);
	}

	public static EbmsAktoer postkasse(final String orgnr) {
		return postkasse(Organisasjonsnummer.of(orgnr));
	}

	public static EbmsAktoer postkasse(final Organisasjonsnummer orgnr) {
		return new EbmsAktoer(orgnr, Rolle.POSTKASSE);
	}

	public static EbmsAktoer from(final From from) {
		String id = from.getPartyIds().get(0).getValue();
		return create(id, from.getRole());
	}

	public static EbmsAktoer from(final To to) {
		String id = to.getPartyIds().get(0).getValue();
		return create(id, to.getRole());
	}

	private static EbmsAktoer create(final String id, final String role) {
		Rolle rolle = Rolle.parse(role);
		Organisasjonsnummer nummer = Organisasjonsnummer.of(id);
		return new EbmsAktoer(nummer, rolle);
	}

}
