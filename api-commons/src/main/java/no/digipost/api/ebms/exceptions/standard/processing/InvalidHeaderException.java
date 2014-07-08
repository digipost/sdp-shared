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
package no.digipost.api.ebms.exceptions.standard.processing;

import no.digipost.api.ebms.exceptions.AbstractEbmsException;
import no.digipost.api.ebms.Category;
import no.digipost.api.ebms.Origin;
import no.digipost.api.ebms.Severity;

public class InvalidHeaderException extends AbstractEbmsException {

	public static final String DEFAULT_DESCRIPTION = "The ebMS header is either not well formed as an XML document, " +
			"or does not conform to the ebMS packaging rules.";

	public static final String INVALID_HEADER_CODE = "0009";
	public static final String INVALID_HEADER_EBMS_CODE = EBMS_STANDARD_ERROR_CODE_PREFIX + ":" + INVALID_HEADER_CODE;

	public InvalidHeaderException() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public InvalidHeaderException(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public InvalidHeaderException(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}

	public InvalidHeaderException(final String refToMessageInError, final Throwable cause, final String description) {
		super(Origin.ebMS, INVALID_HEADER_CODE, Severity.failure, Category.UnPackaging, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "InvalidHeader";
	}
}
