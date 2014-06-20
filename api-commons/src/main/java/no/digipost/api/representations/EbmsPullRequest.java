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
package no.digipost.api.representations;

public class EbmsPullRequest extends EbmsOutgoingMessage {

	public EbmsPullRequest(final EbmsAktoer mottaker) {
		this(mottaker, Prioritet.NORMAL);
	}
	public EbmsPullRequest(final EbmsAktoer mottaker, final String mpcId) {
		this(mottaker, Prioritet.NORMAL, mpcId);
	}

	public EbmsPullRequest(final EbmsAktoer mottaker, final Prioritet prioritet) {
		this(mottaker, prioritet, null);
	}

	public EbmsPullRequest(final EbmsAktoer mottaker, final Prioritet prioritet, final String mpcId) {
		super(mottaker, newId(), null, prioritet, mpcId);
	}

}
