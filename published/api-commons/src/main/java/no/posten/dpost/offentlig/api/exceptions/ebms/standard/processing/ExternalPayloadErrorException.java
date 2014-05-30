package no.posten.dpost.offentlig.api.exceptions.ebms.standard.processing;

import no.posten.dpost.offentlig.api.exceptions.ebms.AbstractEbmsException;

import static no.posten.dpost.offentlig.api.exceptions.ebms.Category.Content;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Origin.ebMS;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Severity.failure;

public class ExternalPayloadErrorException extends AbstractEbmsException {

	public static final String DEFAULT_DESCRIPTION = "The MSH is unable to resolve an external payload reference " +
			"(i.e. a Part that is not contained within the ebMS Message, as identified by a PartInfo/href URI).";

	public ExternalPayloadErrorException() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public ExternalPayloadErrorException(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public ExternalPayloadErrorException(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}

	public ExternalPayloadErrorException(final String refToMessageInError, final Throwable cause, final String description) {
		super(ebMS, "0011", failure, Content, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "ExternalPayloadError";
	}
}
