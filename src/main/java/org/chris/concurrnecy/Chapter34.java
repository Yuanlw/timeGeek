package org.chris.concurrnecy;

import java.util.concurrent.*;

/**
 * TODO
 *
 * @Author yuanlw
 **/
public class Chapter34 {
    ExecutorService es1 = new ThreadPoolExecutor(50, 1000, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(2000), r -> {
        return new Thread(r, "echo-" + r.hashCode());
    }, new ThreadPoolExecutor.CallerRunsPolicy());

    //L1、L2阶段共用的线程池
    static ExecutorService es = Executors.
            newFixedThreadPool(2);

    public static void main(String[] args) throws Exception {
        //L1阶段的闭锁
        CountDownLatch l1 = new CountDownLatch(2);
        for (int i = 0;i < 2; i++) {
            System.out.println("L1");
            //执行L1阶段任务
            es.execute(() -> {
                //L2阶段的闭锁
                CountDownLatch l2 = new CountDownLatch(2);
                //执行L2阶段子任务
                for (int j = 0; j < 2; j++) {
                    es.execute(() -> {
                        System.out.println("L2");
                        l2.countDown();
                    });
                }
                //等待L2阶段任务执行完
                try {
                    l2.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                l1.countDown();
            });
        }
//等着L1阶段任务执行完
        l1.await();
        System.out.println("end");
    }


}
