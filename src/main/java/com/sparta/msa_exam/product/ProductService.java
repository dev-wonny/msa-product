package com.sparta.msa_exam.product;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

	private static final Logger log = LoggerFactory.getLogger(ProductService.class);
	private final CircuitBreakerRegistry circuitBreakerRegistry;

	public ProductService(CircuitBreakerRegistry circuitBreakerRegistry) {
		this.circuitBreakerRegistry = circuitBreakerRegistry;
	}

	@PostConstruct
	public void registerEventListener() {
		circuitBreakerRegistry.circuitBreaker("productService").getEventPublisher()
				.onStateTransition(event -> log.info("#######CircuitBreaker State Transition: {}", event)) // 상태 전환 이벤트 리스너
				.onFailureRateExceeded(event -> log.info("#######CircuitBreaker Failure Rate Exceeded: {}", event)) // 실패율 초과 이벤트 리스너
				.onCallNotPermitted(event -> log.info("#######CircuitBreaker Call Not Permitted: {}", event)) // 호출 차단 이벤트 리스너
				.onError(event -> log.info("#######CircuitBreaker Error: {}", event)); // 오류 발생 이벤트 리스너
	}

	@CircuitBreaker(name = "productService", fallbackMethod = "failbackGetProductDetails")
	public Product getProductDetail(String productId) {
		log.info("###Fetching product details for productId: {}", productId);

		if ("222".equals(productId)) {
			log.warn("###Received Empty body for productId: {}", productId);
			throw new RuntimeException("[TEST] Product Service makes Empty body Error");
		}

		return new Product(
				Long.parseLong(productId),
				"test product's title",
				500
		);
	}

	private Product failbackGetProductDetails(String productId, Throwable t) {
		log.error("####Fallback trigger for productId: {}, due to: {}", productId, t.getMessage());
		return new Product(
				Long.parseLong(productId),
				"Failback Product : " + productId,
				500
		);
	}
}
