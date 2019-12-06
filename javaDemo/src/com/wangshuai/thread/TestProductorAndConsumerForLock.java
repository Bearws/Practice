package com.wangshuai.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestProductorAndConsumerForLock {
    public static void main(String[] args) {
        Clerk1 clerk = new Clerk1();
        Producter1 pro = new Producter1(clerk);
        Consumer1 con = new Consumer1(clerk);
        new Thread(pro, "生产者").start();
        new Thread(con, "消费者").start();
    }
}

//
class Clerk1 {
    private int product = 0;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    //进货
    public void get() {
        lock.lock();
        try {
            while (product >= 10) {
                System.out.println(Thread.currentThread().getName() + " : " + "产品已满！");
                try {
                    condition.await();//为了避免虚假唤醒问题，应该总是使用在循环中
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + " : " + ++product);
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    //进货
    public void sale() {
        lock.lock();
        try {
            while (product <= 0) {
                System.out.println(Thread.currentThread().getName() + " : " + "缺货！");
                try {
                    condition.await();//为了避免虚假唤醒问题，应该总是使用在循环中
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + " : " + --product);
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}

class Producter1 implements Runnable {
    private com.wangshuai.thread.Clerk1 clerk;

    public Producter1(com.wangshuai.thread.Clerk1 clerk) {
        this.clerk = clerk;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            clerk.get();
        }
    }
}

class Consumer1 implements Runnable {
    private com.wangshuai.thread.Clerk1 clerk;

    public Consumer1(com.wangshuai.thread.Clerk1 clerk) {
        this.clerk = clerk;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            clerk.sale();
        }
    }
}
