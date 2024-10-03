package org.sandopla.photocenter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PhotocenterApplicationTests {

    @Test
    void contextLoads() {
        // Цей тест перевіряє, чи правильно завантажується контекст Spring
    }

    @Test
    void simpleTest() {
        assertTrue(true, "This test should always pass");
        assertEquals(4, 2 + 2, "Basic addition should work");
    }

}