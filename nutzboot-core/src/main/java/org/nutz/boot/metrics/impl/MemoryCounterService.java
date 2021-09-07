package org.nutz.boot.metrics.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.nutz.boot.metrics.CounterService;

public class MemoryCounterService implements CounterService {

	protected ConcurrentHashMap<String, AtomicLong> atoms = new ConcurrentHashMap<>();

	protected AtomicLong getAtomicLong(String metricName) {
		return atoms.computeIfAbsent(metricName, (name) -> new AtomicLong());
	}

	public long increment(String metricName) {
		return getAtomicLong(metricName).incrementAndGet();
	}

	public long decrement(String metricName) {
		return getAtomicLong(metricName).decrementAndGet();
	}

	public void reset(String metricName) {
		atoms.remove(metricName);
	}

	public void submit(String metricName, long value) {
		getAtomicLong(metricName).set(value);
	}

	public long get(String metricName) {
		return getAtomicLong(metricName).get();
	}

	public Set<String> keys() {
		return new HashSet<>(atoms.keySet());
	}

}
