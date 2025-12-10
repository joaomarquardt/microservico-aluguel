package com.es2.microservicos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;

@SpringBootTest
@ActiveProfiles("test")
class MicroservicosApplicationTests {
	@MockitoBean(name = "restClientEquipamento")
	private RestClient restClientEquipamento;

	@MockitoBean(name = "restClientExterno")
	private RestClient restClientExterno;

	@Test
	void contextLoads() {
	}
}
