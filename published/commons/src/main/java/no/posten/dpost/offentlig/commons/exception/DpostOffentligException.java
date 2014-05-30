package no.posten.dpost.offentlig.commons.exception;

public class DpostOffentligException extends RuntimeException {
	public DpostOffentligException(final String message, final Throwable cause) {
		super(message, cause);
	}
	public DpostOffentligException(final String message) {
		super(message);
	}

	private static final long serialVersionUID = -6849492599803989011L;

}
