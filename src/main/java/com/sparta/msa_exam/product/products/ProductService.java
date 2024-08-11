package com.sparta.msa_exam.product.products;

import com.sparta.msa_exam.product.core.Product;
import com.sparta.msa_exam.product.dto.ProductRequestDto;
import com.sparta.msa_exam.product.dto.ProductResponseDto;
import com.sparta.msa_exam.product.dto.ProductSearchDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {

	private static final Logger log = LoggerFactory.getLogger(ProductService.class);
//	private final CircuitBreakerRegistry circuitBreakerRegistry;
	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Transactional
	public ProductResponseDto createProduct(ProductRequestDto requestDto, String userId) {
		Product product = Product.createProduct(requestDto, userId);
		Product savedProduct = productRepository.save(product);
		return toResponseDto(savedProduct);
	}

	public Page<ProductResponseDto> getProducts(ProductSearchDto searchDto, Pageable pageable) {
		return productRepository.searchProducts(searchDto, pageable);
	}

	@Transactional(readOnly = true)
	public ProductResponseDto getProductById(Long productId) {
		Product product = productRepository.findById(productId)
				// 코트레벨에서 필터를 걸었다 -> DB에서 가져올때 거르고 가져와라
				.filter(p -> p.getDeletedAt() == null)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found or has been deleted"));
		return toResponseDto(product);
	}

	@Transactional
	public ProductResponseDto updateProduct(Long productId, ProductRequestDto requestDto, String userId) {
		Product product = productRepository.findById(productId)
				.filter(p -> p.getDeletedAt() == null)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found or has been deleted"));

		product.updateProduct(requestDto.getName(), requestDto.getDescription(), requestDto.getPrice(), requestDto.getQuantity(), userId);
		Product updatedProduct = productRepository.save(product);

		return toResponseDto(updatedProduct);
	}

	@Transactional
	public void deleteProduct(Long productId, String deletedBy) {
		Product product = productRepository.findById(productId)
				.filter(p -> p.getDeletedAt() == null)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found or has been deleted"));
		product.deleteProduct(deletedBy);
		productRepository.save(product);
	}

	@Transactional
	public void reduceProductQuantity(Long productId, int quantity) {
		// 전체를 가져와서 찾지말고, DB에서 id로 조회해라
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

		if (product.getQuantity() < quantity) {
			throw new IllegalArgumentException("Not enough quantity for product ID: " + productId);
		}

		product.reduceQuantity(quantity);
		productRepository.save(product);
	}

	private ProductResponseDto toResponseDto(Product product) {
		return new ProductResponseDto(
				product.getProduct_id(),
				product.getName(),
				product.getDescription(),
				product.getSupply_price(),
				product.getQuantity(),
				product.getCreatedAt(),
				product.getCreatedBy(),
				product.getUpdatedAt(),
				product.getUpdatedBy()
		);
	}

	/*
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
	 */

}
