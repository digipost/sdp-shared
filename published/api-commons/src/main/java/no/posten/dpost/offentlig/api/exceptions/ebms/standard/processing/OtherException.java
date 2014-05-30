package no.posten.dpost.offentlig.api.exceptions.ebms.standard.processing;

import no.posten.dpost.offentlig.api.exceptions.ebms.AbstractEbmsException;

import static no.posten.dpost.offentlig.api.exceptions.ebms.Category.Content;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Origin.ebMS;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Severity.failure;

public class OtherException extends AbstractEbmsException {

	public static final OtherException GENERAL_ERROR = new OtherException(null, "Something went wrong, please try again later.");

	public static final String DEFAULT_DESCRIPTION = "-";

	public OtherException() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public OtherException(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public OtherException(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}

	public OtherException(final String refToMessageInError, final Throwable cause, final String description) {
		super(ebMS, "0004", failure, Content, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "Other";
	}
}
