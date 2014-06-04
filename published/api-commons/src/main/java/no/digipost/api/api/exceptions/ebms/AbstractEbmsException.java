/**
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.digipost.api.api.exceptions.ebms;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;

import static no.digipost.api.api.exceptions.ebms.Severity.failure;

public abstract class AbstractEbmsException extends RuntimeException {

	public static final String LANGUAGE_CODE_ERROR_DESCRIPTION = "en";
	public static final String EBMS_STANDARD_ERROR_CODE_PREFIX = "EBMS";
	private final String refToMessageInError;
	private final String origin;
	private final String code;
	private final String severity;
	private final String category;
	private final String description;

	protected AbstractEbmsException(final Origin origin, final String code, final Severity severity, final Category category, final String description, final String refToMessageInError, final Throwable cause) {
		super(description, cause);
		this.origin = origin.toString();
		this.code = EBMS_STANDARD_ERROR_CODE_PREFIX + ":" + code;
		this.severity = severity.toString();
		this.category = category.toString();
		this.description = description;
		this.refToMessageInError = refToMessageInError;
	}

	public Error toError() {
		Error error = new Error()
				.withOrigin(origin)
				.withErrorCode(code)
				.withSeverity(severity)
				.withCategory(category)
				.withShortDescription(getShortDescription())
				.withDescription(new Description(description, LANGUAGE_CODE_ERROR_DESCRIPTION));

		if (refToMessageInError != null) {
			error.withRefToMessageInError(refToMessageInError);
		}

		if (getCause() != null) {
			error.withErrorDetail(getCause().getMessage());
		}

		return error;
	}

	public abstract String getShortDescription();

	public boolean isSeverityFailure() {
		return severity.equals(Severity.failure.toString());
	}

	public boolean loggable() {
		return true;
	}

}
