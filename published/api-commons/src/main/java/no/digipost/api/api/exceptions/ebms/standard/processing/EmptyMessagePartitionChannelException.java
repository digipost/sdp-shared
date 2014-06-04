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
package no.digipost.api.api.exceptions.ebms.standard.processing;

import no.digipost.api.api.exceptions.ebms.AbstractEbmsException;
import no.digipost.api.api.exceptions.ebms.Category;
import no.digipost.api.api.exceptions.ebms.Origin;
import no.digipost.api.api.exceptions.ebms.Severity;
import no.digipost.api.api.exceptions.ebms.AbstractEbmsException;

import static no.digipost.api.api.exceptions.ebms.Category.Communication;
import static no.digipost.api.api.exceptions.ebms.Origin.ebMS;
import static no.digipost.api.api.exceptions.ebms.Severity.warning;

public class EmptyMessagePartitionChannelException extends AbstractEbmsException {

	public static final String DEFAULT_DESCRIPTION = "There is no message available for pulling from this MPC at this moment.";

	public static final String EMPTY_MPC_CODE = "0006";
	public static final String EMPTY_MPC_EBMS_CODE = EBMS_STANDARD_ERROR_CODE_PREFIX + ":" + EMPTY_MPC_CODE;

	public EmptyMessagePartitionChannelException() {
		this(null, null);
	}

	public EmptyMessagePartitionChannelException(final String refToMessageInError) {
		this(refToMessageInError, null);
	}

	public EmptyMessagePartitionChannelException(final Throwable cause) {
		this(null, cause);
	}

	public EmptyMessagePartitionChannelException(final String refToMessageInError, final Throwable cause) {
		super(Origin.ebMS, EMPTY_MPC_CODE, Severity.warning, Category.Communication, DEFAULT_DESCRIPTION, refToMessageInError, cause);
	}

	@Override
	public String getShortDescription() {
		return "EmptyMessagePartitionChannel";
	}

	@Override
	public boolean loggable() {
		return false;
	}
}
