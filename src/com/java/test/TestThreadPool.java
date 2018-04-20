package com.java.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestThreadPool implements Runnable {

	private static final int POOL_SIZE = 10;

	ExecutorService executorService1 = Executors.newCachedThreadPool();

	int cpuNums = Runtime.getRuntime().availableProcessors();
	ExecutorService executorService2 = Executors.newFixedThreadPool(cpuNums * POOL_SIZE);

	@Override
	public void run() {

	}

}
