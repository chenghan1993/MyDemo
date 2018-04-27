package com.java.test.multiThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TestReadWriteLock {
	public static void main(String[] args) {
		ReadWriteLock lock = new ReentrantReadWriteLock();
		ExecutorService executor = Executors.newCachedThreadPool();
		Account account = new Account(lock, "Jack", 10000);
		// 账号取钱10次，存钱10次，查询20次
		for (int i = 1; i <= 10; i++) {
			Operation operation1 = new Operation(account, "take");
			Operation operation2 = new Operation(account, "query");
			Operation operation3 = new Operation(account, "save");
			Operation operation4 = new Operation(account, "query");
			executor.execute(operation1);
			executor.execute(operation2);
			executor.execute(operation3);
			executor.execute(operation4);
		}
		executor.shutdown();
		System.out.println("账号" + account.getAccoutNo() + ",最后金额为：" + account.getMoney());
	}
}

class Operation implements Runnable {

	private Account account;

	private String type;

	public Operation(Account account, String type) {
		this.account = account;
		this.type = type;
	}

	@Override
	public void run() {
		if ("take".equals(type)) {
			// 获取写锁
			account.getLock().writeLock().lock();
			account.setMoney(account.getMoney() - 100);
			System.out.println("取走100元,账号" + account.getAccoutNo() + " 还有" + account.getMoney() + "元");
			account.getLock().writeLock().unlock();
		} else if ("query".equals(type)) {
			// 获取读锁
			account.getLock().readLock().lock();
			System.out.println("查询账号" + account.getAccoutNo() + " 还有" + account.getMoney() + "元");
			account.getLock().readLock().unlock();
		} else if ("save".equals(type)) {
			// 获取写锁
			account.getLock().writeLock().lock();
			account.setMoney(account.getMoney() + 100);
			System.out.println("存入100元,账号" + account.getAccoutNo() + " 还有" + account.getMoney() + "元");
			account.getLock().writeLock().unlock();
		}
	}

}

class Account {
	private int money;
	private ReadWriteLock lock;
	private String accountNo;

	Account(ReadWriteLock lock, String accountNo, int money) {
		this.lock = lock;
		this.accountNo = accountNo;
		this.money = money;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public ReadWriteLock getLock() {
		return lock;
	}

	public void setLock(ReadWriteLock lock) {
		this.lock = lock;
	}

	public String getAccoutNo() {
		return accountNo;
	}

	public void setAccoutNo(String accountNo) {
		this.accountNo = accountNo;
	}

}