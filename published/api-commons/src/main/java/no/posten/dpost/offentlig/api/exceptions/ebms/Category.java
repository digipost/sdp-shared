package no.posten.dpost.offentlig.api.exceptions.ebms;

public enum Category {

	Content,
	Communication,
	UnPackaging,
	Processing;

	@Override
	public String toString() {
		return this.name();
	}

}