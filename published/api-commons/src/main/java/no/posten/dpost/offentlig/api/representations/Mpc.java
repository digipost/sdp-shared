package no.posten.dpost.offentlig.api.representations;

import no.posten.dpost.offentlig.api.representations.EbmsOutgoingMessage.Prioritet;
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
		return new Mpc(Prioritet.from(parts[1]), parts.length == 3 ? parts[2] : null);
	}
}
