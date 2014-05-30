
package no.posten.dpost.offentlig.commons.exception;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.LoggerFactory;

public class SimpleRetryStrategy implements RetryStrategy {

	private final Duration[] initialRetryDurations;
	private final Duration baseRetryDuration;
	private final Duration retryPeriod;

	public SimpleRetryStrategy(Duration[] initialRetryDurations, Duration baseRetryDuration, Duration retryPeriod) {
		if (baseRetryDuration == null) throw new IllegalArgumentException("baseDuration kan kan ikke være null");
		this.initialRetryDurations = (initialRetryDurations != null) ? initialRetryDurations : new Duration[] {};
		this.baseRetryDuration = baseRetryDuration;
		this.retryPeriod = retryPeriod;
	}

	@Override
	public boolean shouldTryAgain(DateTime started, int attemptsSoFar) {
		return started.plus(retryPeriod).isAfter(DateTime.now());
	}

	@Override
	public Duration tillNextTime(int attemptsSoFar) {
		if (attemptsSoFar < 1) {
			LoggerFactory.getLogger(SimpleRetryStrategy.class).warn("Burde aldrig retrye med færre enn 1 attempts.");
			return baseRetryDuration;
		}

		if (attemptsSoFar > initialRetryDurations.length) {
			return baseRetryDuration;
		} else {
			return initialRetryDurations[attemptsSoFar - 1];
		}
	}

}
