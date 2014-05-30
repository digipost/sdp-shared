package no.posten.dpost.offentlig.commons.statistics;

public class ThreadLocalStatisticsContainer {

	public static ThreadLocal<Statistics> THREADLOCAL_STATISTICS = new ThreadLocal<Statistics>();

	public Statistics get() {
		return THREADLOCAL_STATISTICS.get();
	}

	public void init(Statistics statistics) {
		THREADLOCAL_STATISTICS.set(statistics);
	}

	public Statistics finish() {
		Statistics s = THREADLOCAL_STATISTICS.get();
		THREADLOCAL_STATISTICS.remove();
		return s;
	}
}
