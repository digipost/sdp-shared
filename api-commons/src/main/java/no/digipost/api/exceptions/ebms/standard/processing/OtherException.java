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
package no.digipost.api.exceptions.ebms.standard.processing;

import no.digipost.api.exceptions.ebms.Origin;
import no.digipost.api.exceptions.ebms.AbstractEbmsException;
import no.digipost.api.exceptions.ebms.Category;
import no.digipost.api.exceptions.ebms.Severity;

public class OtherException extends AbstractEbmsException {

	public static final String INTERNAL_SERVER_ERROR = "Internal server error";

	public static final OtherException GENERAL_ERROR = new OtherException(null, "Something went wrong, please try again later.");

	public static final String DEFAULT_DESCRIPTION = "-";
	public static final String ERROR_CODE = "0004";

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
		super(Origin.ebMS, ERROR_CODE, Severity.failure, Category.Content, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "Other";
	}
}
