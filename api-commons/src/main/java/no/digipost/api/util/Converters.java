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

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import static org.joda.time.LocalTime.MIDNIGHT;

public final class Converters {

	@SuppressWarnings("rawtypes")
    private static final Converter NOP = new Converter() {
		@Override
        public Object apply(Object value) {
			return value;
        }
	};

	@SuppressWarnings("unchecked")
    public static <T> Converter<T, T> nop() {
		return NOP;
	}

	public static final Converter<LocalDate, DateTime> toDateTimeAt(final LocalTime time) {
		return new Converter<LocalDate, DateTime>() {
        	@Override
        	public DateTime apply(LocalDate date) {
        		return date.toDateTime(time);
        	}
		};
	}

	public static final Converter<LocalDate, DateTime> toDateTimeAtStartOfDay = toDateTimeAt(MIDNIGHT);

	private Converters() {}

}
