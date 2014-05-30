package no.posten.dpost.offentlig.commons.exception;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Duration;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SimpleRetryStrategyTest {

	@After
	public void tearDown() {
		DateTimeUtils.setCurrentMillisSystem();
	}

	@Test
	public void test_ingen_initielle_custom_retry_durations() {
		DateTime now = DateTime.now();
		DateTimeUtils.setCurrentMillisFixed(now.getMillis());
		SimpleRetryStrategy retryStrategy = new SimpleRetryStrategy(null, Duration.standardDays(1), Duration.standardDays(2));

		assertTrue(retryStrategy.shouldTryAgain(now, 1));
		assertTrue(retryStrategy.shouldTryAgain(now.minusDays(1), 1));
		assertTrue(retryStrategy.shouldTryAgain(now.minusDays(2).plusMillis(1), 1));
		assertFalse(retryStrategy.shouldTryAgain(now.minusDays(2).minusMinutes(1), 1));

		assertEquals(Duration.standardDays(1), retryStrategy.tillNextTime(0)); // fy-fy
		assertEquals(Duration.standardDays(1), retryStrategy.tillNextTime(1));
		assertEquals(Duration.standardDays(1), retryStrategy.tillNextTime(2));
	}

	@Test
	public void test_med_initielle_custom_retry_durations() {
		DateTime now = DateTime.now();
		DateTimeUtils.setCurrentMillisFixed(now.getMillis());
		SimpleRetryStrategy retryStrategy = new SimpleRetryStrategy(new Duration[] { Duration.standardMinutes(5), Duration.standardHours(1) }, Duration.standardDays(1), Duration.standardDays(30));

		assertTrue(retryStrategy.shouldTryAgain(now, 1));
		assertFalse(retryStrategy.shouldTryAgain(now.minusDays(30).minusMinutes(1), 1));

		assertEquals(Duration.standardMinutes(5), retryStrategy.tillNextTime(1));
		assertEquals(Duration.standardHours(1), retryStrategy.tillNextTime(2));
		assertEquals(Duration.standardDays(1), retryStrategy.tillNextTime(3));
		assertEquals(Duration.standardDays(1), retryStrategy.tillNextTime(4));
		assertEquals(Duration.standardDays(1), retryStrategy.tillNextTime(200));
	}
}
