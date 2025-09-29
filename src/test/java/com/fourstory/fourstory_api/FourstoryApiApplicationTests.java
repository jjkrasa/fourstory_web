package com.fourstory.fourstory_api;

import com.fourstory.fourstory_api.security.TestKeyConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestKeyConfig.class)
class FourstoryApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
