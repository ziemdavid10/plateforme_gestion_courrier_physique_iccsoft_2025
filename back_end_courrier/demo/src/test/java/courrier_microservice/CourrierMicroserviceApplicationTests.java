package courrier_microservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = iccsoft_auth.IccsoftAuthApplication.class,
    properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
    }
)
class CourrierMicroserviceApplicationTests {

	@Test
	void contextLoads() {
	}

}
