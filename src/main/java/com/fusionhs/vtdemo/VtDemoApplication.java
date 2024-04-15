package com.fusionhs.vtdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

@Slf4j
@SpringBootApplication
public class VtDemoApplication {
    private final ReentrantLock lock = new ReentrantLock();
    private final RestTemplate restTemplate;

    public VtDemoApplication(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(VtDemoApplication.class, args);
        PinnedDemo.twoEmployeesInTheOffice();
    }


    public void useAVirtualThread() throws InterruptedException {
        Thread thread = Thread.ofVirtual().start(() -> log.info("Hello"));
        thread.join();
    }

    public void useANamedVirtualThread() throws InterruptedException {

        Thread.Builder builder = Thread.ofVirtual().name("MyThread");
        Runnable task = () -> log.info("Running thread");
        Thread t = builder.start(task);
        log.info("Thread t name: " + t.getName());
        t.join();
    }

    public void useAVirtualThreadPool() throws InterruptedException, ExecutionException {
        try (ExecutorService myExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<? extends Future<?>> futureList = Stream.of(1, 2, 3, 4, 5, 6)
                    .map(i -> myExecutor.submit(() -> log.info("Running thread-" + i)))
                    .toList();

            for (var future : futureList) {
                future.get();
            }
            log.info("Task completed");
        }
    }

    /**
     * <p>It’s not an error but a behavior that limits the application’s scalability. Note that if a carrier thread is pinned, the JVM can always add a new platform thread to the carrier pool if the configurations of the carrier pool allow it</p>
     * <p>Fortunately, there are only two cases in which a virtual thread is pinned to the carrier thread:
     *
     * <ul>
     *     <li>When it executes code inside a synchronized block or method, see {@link jdk.internal.org.jline.utils.InputStreamReader#read() InputStreamReader.read()}</li>
     *     <li>When it calls a native method or a foreign function (i.e., a call to a native library using JNI).</li>
     * </ul>
     * <p>
     * Use <code>-Djdk.tracePinnedThreads=full/short</code> to help spot cases like this
     * </p>
     * <p>
     * Source: https://blog.rockthejvm.com/ultimate-guide-to-java-virtual-threads
     */
    private synchronized int synchronizedMethod(int i) {

        restTemplate.getForObject("http://localhost:8081/wait", String.class);

        log.info("Thread {} completed", i);
        return i;
    }

    private int lockedMethod(int i) {
        lock.lock();
        try {
            restTemplate.getForObject("http://localhost:8081/wait", String.class);
        } finally {
            lock.unlock();
        }
        log.info("Thread {} completed", i);
        return i;
    }


}
