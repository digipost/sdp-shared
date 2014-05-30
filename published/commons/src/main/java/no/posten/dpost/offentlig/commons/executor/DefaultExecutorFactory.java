package no.posten.dpost.offentlig.commons.executor;

import java.util.concurrent.*;

public class DefaultExecutorFactory implements ExecutorFactory {

	@Override
	public ExecutorService create(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
	}

}
