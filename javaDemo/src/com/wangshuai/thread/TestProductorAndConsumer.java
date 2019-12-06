package com.wangshuai.thread;

public class TestProductorAndConsumer {
    public static void main(String[] args) {
        Clerk clerk = new Clerk();
        Producter pro = new Producter(clerk);
        Consumer con = new Consumer(clerk);
        new Thread(pro, "生产者").start();
        new Thread(con, "消费者").start();
    }
}

//
class Clerk {
    private int product = 0;

    //进货
    public synchronized void get() {
        while (product >= 10) {
            System.out.println(Thread.currentThread().getName() + " : " + "产品已满！");
            try {
                this.wait();//为了避免虚假唤醒问题，应该总是使用在循环中
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + " : " + ++product);
        this.notifyAll();

    }

    //进货
    public synchronized void sale() {
        while (product <= 0) {
            System.out.println(Thread.currentThread().getName() + " : " + "缺货！");
            try {
                this.wait();//为了避免虚假唤醒问题，应该总是使用在循环中
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + " : " + --product);
        this.notifyAll();

    }
}

class Producter implements Runnable {
    private Clerk clerk;

    public Producter(Clerk clerk) {
        this.clerk = clerk;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            clerk.get();
        }
    }
}

class Consumer implements Runnable {
    private Clerk clerk;

    public Consumer(Clerk clerk) {
        this.clerk = clerk;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            clerk.sale();
        }
    }
}