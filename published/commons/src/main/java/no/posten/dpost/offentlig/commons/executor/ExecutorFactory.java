package no.posten.dpost.offentlig.commons.executor;

import java.util.concurrent.*;

public interface ExecutorFactory {
	ExecutorService create(int corePoolSize,
							  int maximumPoolSize,
							  long keepAliveTime,
							  TimeUnit unit,
							  BlockingQueue<Runnable> workQueue,
							  RejectedExecutionHandler handler);
}
