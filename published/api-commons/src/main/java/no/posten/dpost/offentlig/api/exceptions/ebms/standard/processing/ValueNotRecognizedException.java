package no.posten.dpost.offentlig.api.exceptions.ebms.standard.processing;

import no.posten.dpost.offentlig.api.exceptions.ebms.AbstractEbmsException;

import static no.posten.dpost.offentlig.api.exceptions.ebms.Category.Content;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Origin.ebMS;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Severity.failure;

public class ValueNotRecognizedException extends AbstractEbmsException {

	public static final String DEFAULT_DESCRIPTION = "Although the message document is well formed and schema valid, " +
			"some element/attribute contains a value that could not be recognized and therefore could not be used by the MSH.";

	public ValueNotRecognizedException() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public ValueNotRecognizedException(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public ValueNotRecognizedException(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}
	public ValueNotRecognizedException(final String refToMessageInError, final Throwable cause, final String description) {
		super(ebMS, "0001", failure, Content, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "ValueNotRecognized";
	}
}
