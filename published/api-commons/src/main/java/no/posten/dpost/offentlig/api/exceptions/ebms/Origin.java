package no.posten.dpost.offentlig.api.exceptions.ebms;

public enum Origin {

	ebMS,
	security,
	reliability;

	@Override
	public String toString() {
		return this.name();
	}

}
