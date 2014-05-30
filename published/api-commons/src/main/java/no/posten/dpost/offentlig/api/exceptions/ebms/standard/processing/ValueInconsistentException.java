package no.posten.dpost.offentlig.api.exceptions.ebms.standard.processing;

import no.posten.dpost.offentlig.api.exceptions.ebms.AbstractEbmsException;

import static no.posten.dpost.offentlig.api.exceptions.ebms.Category.Content;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Origin.ebMS;
import static no.posten.dpost.offentlig.api.exceptions.ebms.Severity.failure;

public class ValueInconsistentException extends AbstractEbmsException {

	private static final String DEFAULT_DESCRIPTION =
			"Although the message document is well formed and schema valid, " +
					"some element/attribute value is inconsistent either with the content of other element/attribute, " +
					"or with the processing mode of the MSH, or with the normative requirements of the ebMS specification.";

	public ValueInconsistentException() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public ValueInconsistentException(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public ValueInconsistentException(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}
	public ValueInconsistentException(final String refToMessageInError, final Throwable cause, final String description) {
		super(ebMS, "0003", failure, Content, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "ValueInconsistent";
	}
}
