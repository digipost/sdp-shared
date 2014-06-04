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

import org.springframework.util.StringUtils;

public class Mpc {

	public final EbmsForsendelse.Prioritet prioritet;
	public final String avsenderId;

	public Mpc(final EbmsForsendelse.Prioritet prioritet, final String avsenderId) {
		this.prioritet = prioritet;
		this.avsenderId = avsenderId;
	}

	@Override
	public String toString() {
		String result = "urn:" + prioritet.name().toLowerCase();
		if (!StringUtils.isEmpty(avsenderId)) {
			result += ":" + avsenderId;
		}
		return result;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

    @Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Mpc)) {
			return false;
		}
		Mpc other = (Mpc)obj;
		return toString().equals(other.toString());
	}

	public static Mpc from(final String mpc) {
		String parts[] = mpc.split(":", 3);
		return new Mpc(EbmsOutgoingMessage.Prioritet.from(parts[1]), parts.length == 3 ? parts[2] : null);
	}
}
