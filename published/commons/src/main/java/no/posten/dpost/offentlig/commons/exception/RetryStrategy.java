package no.posten.dpost.offentlig.commons.exception;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public interface RetryStrategy {
	boolean shouldTryAgain(DateTime started, int attemptsSoFar);
	Duration tillNextTime(int attemptsSoFar);
}
