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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static no.digipost.api.util.Choice.choice;
import static no.digipost.api.util.Converters.toDateTimeAtStartOfDay;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class ChoiceTest {

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	@Test
	public void throwsExceptionIfBothAreNonNull() {
		expectedException.expect(IllegalArgumentException.class);
		choice(DateTime.now(), LocalDate.now());
	}


	@Test
	public void bothNullsYieldsDateTimeAsNull() {
		assertThat(choice(null, null), nullValue());
	}


	@Test
	public void firstInstancePresent() {
		assertThat(choice("a", null), is("a"));
	}

	@Test
	public void secondInstancePresent() {
		assertThat(choice(null, "a"), is("a"));
	}


	@Test
	public void convertSecondInstance() {
		LocalDate today = LocalDate.now();
		assertThat(choice((DateTime) null, today, toDateTimeAtStartOfDay), is(today.toDateTimeAtStartOfDay()));
	}
}
