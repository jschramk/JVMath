package com.jschramk.JVMath.utilities.test;

import util.PerformanceTimer;

import java.util.ArrayList;
import java.util.List;

public class TestThreading {

    public static void main(String[] args) throws InterruptedException {

        PerformanceTimer p = new PerformanceTimer();

        List<Thread> threads = new ArrayList<>();

        int threadCount = 32;
        int searchNum = 500;

        int target = 250;

        int searchPerThread = searchNum / threadCount;

        for (int i = 0; i < threadCount; i++) {

            int threadIndex = i;
            int start = searchPerThread * i;
            int end = start + searchPerThread + 1;

            Runnable r = () -> {

                for (int j = start; j < end; j++) {

                    System.out.println(String.format("Thread %d: %d", threadIndex, j));

                    if (j == target) {

                        for (int k = threadIndex + 1; k < threads.size(); k++) {
                            threads.get(k).interrupt();
                        }

                        System.out.println(String.format("Thread %d found target", threadIndex));

                        break;

                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        break;
                    }

                    if(Thread.interrupted()) break;

                    if(j == end - 1)
                        System.out.println(String.format("Thread %d finished search", threadIndex));

                }

            };



            threads.add(new Thread(r));


        }

        p.start();

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        p.stop();

        p.printDelta();


    }


}
