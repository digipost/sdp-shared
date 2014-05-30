package no.posten.dpost.offentlig.commons.model;

public enum Prioritet implements IntRepresentation<Prioritet> {

	NORMAL(0), PRIORITERT(5);
	private int value;

	private Prioritet(final int value) {
		this.value = value;
	}

	@Override
	public int intValue() {
		return value;
	}
}
