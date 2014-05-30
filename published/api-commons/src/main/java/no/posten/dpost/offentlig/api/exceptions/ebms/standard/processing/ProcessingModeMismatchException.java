package no.posten.dpost.offentlig.api.exceptions.ebms.standard.processing;

import no.posten.dpost.offentlig.api.exceptions.ebms.AbstractEbmsException;

import static no.posten.dpost.offentlig.api.exceptions.ebms.Category.Processing;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Origin.ebMS;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Severity.failure;

public class ProcessingModeMismatchException extends AbstractEbmsException {

	public static final String DEFAULT_DESCRIPTION = "The ebMS header or another header (e.g. reliability, security) " +
			"expected by the MSH is not compatible with the expected content, based on the associated P-Mode.";

	public ProcessingModeMismatchException() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public ProcessingModeMismatchException(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public ProcessingModeMismatchException(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}

	public ProcessingModeMismatchException(final String refToMessageInError, final Throwable cause, final String description) {
		super(ebMS, "0010", failure, Processing, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "ProcessingModeMismatch";
	}
}
