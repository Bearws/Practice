package com.wangshuai.thread;


import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * CopyOnWriteArrayList/CopyOnWriteArraySet : “写入并复制”
 * 注意：添加操作多时，效率低，因为每次添加时都会进行复制，开销非常的大。并发迭代操作多时可以选择。
 */
public class TestCopyOnWriteArrayList {
    public static void main(String[] args) {
        HelloThread thread = new HelloThread();
        new Thread(thread).start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(HelloThread.getList());
    }
}

class HelloThread implements Runnable {
    //private static List<String> list = Collections.synchronizedList(new ArrayList<>());
    private static CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

    static {
        list.add("AA");
        list.add("BB");
        list.add("CC");
    }

    @Override
    public void run() {
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
            list.add("ss");
        }
    }

    public static String getList() {
        return list.toString();
    }
}