package com.fusionhs.vtdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VtDemoApplicationTest {

    @Autowired
    VtDemoApplication demoApplication;

    @Test
    void useAVirtualThread() throws Exception {
        demoApplication.useAVirtualThread();
    }

    @Test
    void useANamedVirtualThread() throws Exception {
        demoApplication.useANamedVirtualThread();
    }

    @Test
    void useAVirtualThreadPool() throws Exception {
        demoApplication.useAVirtualThreadPool();
    }

}