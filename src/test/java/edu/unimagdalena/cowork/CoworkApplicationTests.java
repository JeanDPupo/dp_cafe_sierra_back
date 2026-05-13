package edu.unimagdalena.cowork;

import static org.assertj.core.api.Assertions.assertThat;

import edu.unimagdalena.cowork.api.HealthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CoworkApplicationTests {

    @Autowired
    private HealthController healthController;

    @Test
    void contextLoads() {
        assertThat(healthController).isNotNull();
    }
}
