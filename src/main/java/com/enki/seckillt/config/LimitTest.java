package com.enki.seckillt.config;

import com.enki.seckillt.annotations.Limit;
import com.enki.seckillt.annotations.LimitType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Enki
 * @Version 1.0
 */
@RestController
public class LimitTest {
    private static final AtomicInteger ATOMIC_INTEGER_1 = new AtomicInteger();

    @Limit(key = "customer_limit_test", period = 10, count = 3, limitType = LimitType.CUSTOMER)
    @GetMapping("/limitTest2")
    public int testLimiter2() {
        return ATOMIC_INTEGER_1.incrementAndGet();
    }

}
