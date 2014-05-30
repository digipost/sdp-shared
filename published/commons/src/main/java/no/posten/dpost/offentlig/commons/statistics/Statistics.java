package no.posten.dpost.offentlig.commons.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class Statistics {
	private final Logger LOG = LoggerFactory.getLogger(Statistics.class);
	private final Map<String, Statistic> stats = new LinkedHashMap<String, Statistic>();

	public <T extends Statistic> T get(String key, Class<T> clazz) {
		Statistic s = stats.get(key);
		if (s == null) {
			return null;
		} else if (clazz.isAssignableFrom(s.getClass())) {
			return (T) s;
		} else {
			LOG.warn("Uventet klasse for Statistic-objekt: " + s.getClass().getName());
			return null;
		}
	}

	public void put(String key, Statistic jdbcStats) {
		stats.put(key, jdbcStats);
	}

	public String getSummary() {
		StringBuilder sb = new StringBuilder();
		for (String key : stats.keySet()) {
			sb.append(stats.get(key).getSummary());
		}
		return sb.toString();
	}
}
