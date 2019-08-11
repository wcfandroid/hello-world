package com.example;

import java.math.BigDecimal;

/**
 * Created by weichangfa on 2017/4/8.
 */

public class MyThread extends Thread {
    private Object lock;
    private String showChar;
    private int showNumPosition;
    private int printCount = 0; //统计打印了几个字母

    private volatile static int addNumber = 1;

    public MyThread(Object lock, String showChar, int showNumPosition) {
        super();
        this.lock = lock;
        this.showChar = showChar;
        this.showNumPosition = showNumPosition;
    }

    @Override
    public void run() {
        try {
            synchronized (lock) {
                while (true) {
                    if (addNumber % 3 == showNumPosition) {
                        System.out.println("ThreadName="
                                + Thread.currentThread().getName()
                                + ",runCount=" + addNumber + " " + showChar);
                        lock.notifyAll();
                        addNumber++;
                        printCount++;
                        if (printCount == 3) {
                            break;
                        }
                    } else {
                        System.out.println("wait -------ThreadName="
                                + Thread.currentThread().getName()
                                + ",runCount=" + addNumber + " " + showChar);
                        lock.wait();
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();
        MyThread a = new MyThread(lock, "AA", 1);
        MyThread b = new MyThread(lock, "BB", 2);
        MyThread c = new MyThread(lock, "CC", 0);
        a.start();
        b.start();
        c.start();
        
    }
}

