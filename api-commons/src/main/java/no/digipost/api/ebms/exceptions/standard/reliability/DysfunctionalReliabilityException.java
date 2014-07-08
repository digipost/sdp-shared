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
package no.digipost.api.ebms.exceptions.standard.reliability;

import no.digipost.api.ebms.exceptions.AbstractEbmsException;
import no.digipost.api.ebms.Category;
import no.digipost.api.ebms.Origin;
import no.digipost.api.ebms.Severity;

public class DysfunctionalReliabilityException extends AbstractEbmsException {

	public static final String DEFAULT_DESCRIPTION = "Some reliability function as implemented by the Reliability module, " +
			"is not operational, or the reliability state associated with this message sequence is not valid.";

	public DysfunctionalReliabilityException() {
		this(null, null, DEFAULT_DESCRIPTION);
	}

	public DysfunctionalReliabilityException(final Throwable cause) {
		this(null, cause, DEFAULT_DESCRIPTION);
	}

	public DysfunctionalReliabilityException(final String refToMessageInError, final String description) {
		this(refToMessageInError, null, description);
	}

	public DysfunctionalReliabilityException(final String refToMessageInError, final Throwable cause, final String description) {
		super(Origin.reliability, "0201", Severity.failure, Category.Processing, description, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "DysfunctionalReliability";
	}
}
