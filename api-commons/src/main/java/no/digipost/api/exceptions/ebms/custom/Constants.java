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
package no.digipost.api.exceptions.ebms.custom;

import no.digipost.api.exceptions.ebms.Origin;

public class Constants {

	public static final String customSecurityErrorCode(Origin origin, String suffix) {
		if (suffix.length() != 2) {
			throw new IllegalArgumentException("Illegal error code suffix");
		}
		switch (origin) {
			case security:
				return "11" + suffix;
			case ebMS:
				return "10" + suffix;
			case reliability:
				return "12" + suffix;
			default:
				throw new IllegalArgumentException("Illegal error code origin");
		}
	}

}
