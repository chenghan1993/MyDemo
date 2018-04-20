package com.java.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestThreadPool2 {
	public static void main(String[] args) {
		ExecutorService executorService = Executors.newCachedThreadPool();
		List<Future<String>> resultList = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			Future<String> future = executorService.submit(new TaskWithResult(i));
			resultList.add(future);
		}
		executorService.shutdown();
		for (Future<String> fs : resultList) {
			try {
				System.out.println(fs.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}

class TaskWithResult implements Callable<String> {

	private int id;

	public TaskWithResult(int id) {
		this.id = id;
	}

	@Override
	public String call() throws Exception {
		System.out.println("call()被自动调用" + Thread.currentThread().getName());
		for (int i = 999999; i > 0; i--)
			;
		return "call()被自动调用，任务结果是：" + id + " " + Thread.currentThread().getName();
	}

}
