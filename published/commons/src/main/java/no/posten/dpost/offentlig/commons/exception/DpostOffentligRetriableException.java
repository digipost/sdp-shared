package no.posten.dpost.offentlig.commons.exception;

public class DpostOffentligRetriableException extends DpostOffentligException {
	public DpostOffentligRetriableException(final String message, final Exception cause) {
		super(message, cause);
	}
	public DpostOffentligRetriableException(final String message) {
		super(message);
	}

	private static final long serialVersionUID = -6849492545673989015L;
}
