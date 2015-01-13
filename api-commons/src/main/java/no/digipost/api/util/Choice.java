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
package no.digipost.api.util;

/**
 * Utility for resolving one of two mutual exclusive instances, typically from
 * a JAXB-representation of an xsd:choice element. If given two actual instances
 * (non-<code>null</code> references) it will fail-fast with an IllegalArgumentException.
 */
public final class Choice<T> {

	public static <T> T choice(T first, T second) {
		return choice(first, second, Converters.<T>nop());
    }

	public static <T, S> T choice(T first, S second, Converter<? super S, ? extends T> secondChoiceConverter) {
		if (first != null && second != null) {
			throw new IllegalArgumentException("Can only specify one of the arguments, not both. Got first arg:" + first + ", second: " + second);
		} else if (first != null) {
			return first;
		} else if (second != null) {
			return secondChoiceConverter.apply(second);
		} else {
			return null;
		}
    }

	private Choice() {
	}

}
