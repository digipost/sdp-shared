package no.posten.dpost.offentlig.api.exceptions.ebms.standard.processing;

import no.posten.dpost.offentlig.api.exceptions.ebms.AbstractEbmsException;

import static no.posten.dpost.offentlig.api.exceptions.ebms.Category.UnPackaging;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Origin.ebMS;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Severity.failure;

public class MimeInconsistencyException extends AbstractEbmsException {

	public static final String DEFAULT_DESCRIPTION = "The use of MIME is not consistent with the required usage in this specification.";

	public MimeInconsistencyException() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public MimeInconsistencyException(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public MimeInconsistencyException(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}

	public MimeInconsistencyException(final String refToMessageInError, final Throwable cause, final String description) {
		super(ebMS, "0007", failure, UnPackaging, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "MimeInconsistency";
	}
}
