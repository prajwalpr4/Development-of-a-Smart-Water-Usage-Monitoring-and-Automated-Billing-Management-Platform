package com.codecrew.smartwater;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SmartWaterApplicationTests {

    @Test
    void contextLoads() {
        // Verifies complete Spring Boot 3.5.16 ApplicationContext loads cleanly with Security & JPA
    }
}
