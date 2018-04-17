package org.nutz.boot.metrics;

import java.util.Set;

public interface CounterService {

	long increment(String metricName);

	long decrement(String metricName);

	void reset(String metricName);

	void submit(String metricName, long value);

	long get(String metricName);

	Set<String> keys();
}
