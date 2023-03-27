package com.enki.seckillt;

import com.enki.seckillt.annotations.Limit;
import com.enki.seckillt.annotations.LimitType;
import com.google.common.util.concurrent.RateLimiter;
import org.joda.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
class SeckillTApplicationTests {

    @Test
    void contextLoads() {
        long timestamp = System.currentTimeMillis();
        System.out.println(timestamp);
    }

    private static final AtomicInteger ATOMIC_INTEGER_1 = new AtomicInteger();

    @Test
    @Limit(key = "customer_limit_test", period = 10, count = 3, limitType = LimitType.CUSTOMER)
    @GetMapping("/limitTest2")
    public int testLimiter2() {
        return ATOMIC_INTEGER_1.incrementAndGet();
    }

    @Test
    public void testAcquire() {
        String start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        RateLimiter limiter = RateLimiter.create(1.0); // 这里的1表示每秒允许处理的量为1个
        for (int i = 1; i <= 10; i++) {
            limiter.acquire();// 请求RateLimiter, 超过permits会被阻塞
            System.out.println("call execute.." + i);
        }
        String end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("start time:" + start);
        System.out.println("end time:" + end);
    }
}
