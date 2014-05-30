package no.posten.dpost.offentlig.api.exceptions.ebms.custom.security;

import no.posten.dpost.offentlig.api.exceptions.ebms.AbstractEbmsException;

import static no.posten.dpost.offentlig.api.exceptions.ebms.Category.Content;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Origin.security;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Severity.failure;
import static no.posten.dpost.offentlig.api.exceptions.ebms.custom.Constants.customSecurityErrorCode;

public class SignatureValidationException extends AbstractEbmsException {

	public static final String DEFAULT_DESCRIPTION = "The MSH could not validate Signature.";

	public SignatureValidationException() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public SignatureValidationException(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public SignatureValidationException(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}

	public SignatureValidationException(final String refToMessageInError, final Throwable cause, final String description) {
		super(security, customSecurityErrorCode(security, "01"), failure, Content, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "SignatureValidation";
	}
}
