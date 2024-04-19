package com.fusionhs.vtdemo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import static java.lang.Thread.sleep;

@Slf4j
public class PinnedDemo {
    static Bathroom bathroom = new Bathroom();

    private static Thread virtualThread(String name, Runnable runnable) {
        return Thread.ofVirtual()
                .name(name)
                .start(runnable);
    }

    static Thread goToTheWc() {
        return virtualThread(
                "Go to the WC",
                () -> {
                    try {
                        bathroom.useTheWc();
                    } catch (InterruptedException e) {
                    }
                });
    }

    @SneakyThrows
    /**
     * Using the following config:
     * -Djdk.tracePinnedThreads=full
     * -Djdk.virtualThreadScheduler.parallelism=2
     * -Djdk.virtualThreadScheduler.maxPoolSize=1
     * -Djdk.virtualThreadScheduler.minRunnable=1
     */
    static void twoEmployeesInTheOffice() {
        var riccardo = goToTheWc();
        var daniel = takeABreak();
        riccardo.join();
        daniel.join();
    }

    static Thread takeABreak() {
        return virtualThread(
                "Take a break",
                () -> {
                    log.info("I'm going to take a break");
                    try {
                        sleep(Duration.ofSeconds(1L));
                    } catch (InterruptedException e) {
                    }
                    log.info("I'm done with the break");
                });
    }


    static class Bathroom {
        void useTheWc() throws InterruptedException {
            log.info("I'm going to use the WC");
            sleep(Duration.ofSeconds(1L));
            log.info("I'm done with the WC");
        }
    }
}
