package no.posten.dpost.offentlig.api.interceptors;

import no.posten.dpost.offentlig.commons.statistics.Statistic;
import no.posten.dpost.offentlig.commons.statistics.Statistics;
import no.posten.dpost.offentlig.commons.statistics.ThreadLocalStatisticsContainer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.server.SoapEndpointInterceptor;

import java.util.*;

public class ResponsTidInterceptor implements SoapEndpointInterceptor, ClientInterceptor {

	private static final ThreadLocalStatisticsContainer STATS_CONTAINER = new ThreadLocalStatisticsContainer();
	private static final Logger LOG = LoggerFactory.getLogger(ResponsTidInterceptor.class);
	private final String around;

	public ResponsTidInterceptor(final String around) {
		this.around = around;
	}

	@Override
	public boolean handleRequest(final MessageContext messageContext, final Object endpoint) throws Exception {
		if (messageContext.getProperty("TIME") == null) {
			messageContext.setProperty("TIME", new Stack<Long>());
		}
		getStack(messageContext).push(System.currentTimeMillis());
		return true;
	}

	private Stack<Long> getStack(final MessageContext messageContext) {
		return (Stack<Long>)messageContext.getProperty("TIME");
	}

	@Override
	public boolean handleResponse(final MessageContext messageContext, final Object endpoint) throws Exception {
		Stack<Long> stack = getStack(messageContext);
		long duration = System.currentTimeMillis() - stack.pop();
		LOG.debug("RESPONS " + stack.size() + " " + duration + "ms " + around);
		logStats("RE", around, duration);
		return true;
	}

	@Override
	public boolean handleFault(final MessageContext messageContext, final Object endpoint) throws Exception {
		Stack<Long> stack = getStack(messageContext);
		long duration = System.currentTimeMillis() - stack.pop();
		LOG.debug("FAULT " + stack.size() + " " + duration + "ms " + around);
		logStats("F", around, duration);
		return true;
	}

	@Override
	public void afterCompletion(final MessageContext messageContext, final Object endpoint, final Exception ex) throws Exception {

	}

	@Override
	public boolean handleRequest(final MessageContext messageContext) throws WebServiceClientException {
		if (messageContext.getProperty("TIME") == null) {
			messageContext.setProperty("TIME", new Stack<Long>());
		}
		getStack(messageContext).push(System.currentTimeMillis());
		return true;
	}

	@Override
	public boolean handleResponse(final MessageContext messageContext) throws WebServiceClientException {
		Stack<Long> stack = getStack(messageContext);
		long duration = System.currentTimeMillis() - stack.pop();
		LOG.debug("RESPONS " + stack.size() + " " + duration + "ms " + around);
		logStats("RE", around, duration);
		return true;
	}

	@Override
	public boolean handleFault(final MessageContext messageContext) throws WebServiceClientException {
		Stack<Long> stack = getStack(messageContext);
		long duration = System.currentTimeMillis() - stack.pop();
		LOG.debug("FAULT " + stack.size() + " " + duration + "ms " + around);
		logStats("F", around, duration);
		return true;
	}

	@Override
	public boolean understands(final SoapHeaderElement header) {
		return false;
	}

	private void logStats(String type, String around, long duration) {
		Statistics statistics = STATS_CONTAINER.get();
		if (statistics != null && type != null && around != null) {
			EbmsStatistic ebmsStats = statistics.get("EBMS", EbmsStatistic.class);
			if (ebmsStats == null) {
				ebmsStats = new EbmsStatistic();
				statistics.put("EBMS", ebmsStats);
			}
			String s;
			int index = StringUtils.lastIndexOf(around, ".");
			if (index == -1) {
				s = around;
			} else {
				s = StringUtils.substring(around+1, index);
			}
			String shortform = s.replaceAll("[^A-Z]", "") + ":" + type;
			ebmsStats.add(new InterceptorTiming(shortform, duration));
		}
	}

	private static class EbmsStatistic implements Statistic {
		private final List<InterceptorTiming> timings = new ArrayList<InterceptorTiming>();

		public void add(InterceptorTiming interceptorTiming) {
			timings.add(interceptorTiming);
		}

		@Override
		public String getSummary() {
			StringBuilder b = new StringBuilder();
			b.append("[EBMS ");
			Collections.reverse(timings);
			for (InterceptorTiming timing : timings) {
				b.append(String.format("%s:%sms ", timing.type, timing.duration));
			}
			b.append("]");
			return b.toString();
		}
	}

	public static class InterceptorTiming {
		public final String type;
		public final long duration;

		public InterceptorTiming(String type, long duration) {
			this.type = type;
			this.duration = duration;
		}
	}
}
