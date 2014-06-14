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
package no.digipost.api;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;
import org.springframework.ws.soap.SoapMessage;

public class EbmsClientException extends RuntimeException {

	private final SoapMessage soapError;
	private final Error error;

	public EbmsClientException(final SoapMessage soapError, final Error error) {
		this.soapError = soapError;
		this.error = error;
	}

	public Error getError() {
		return error;
	}

	public SoapMessage getSoapError() {
		return soapError;
	}
}
