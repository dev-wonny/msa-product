package com.sparta.msa_exam.product.products;

import com.sparta.msa_exam.product.core.enums.Role;
import com.sparta.msa_exam.product.dto.ProductRequestDto;
import com.sparta.msa_exam.product.dto.ProductResponseDto;
import com.sparta.msa_exam.product.dto.ProductSearchDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/products")
public class ProductController {

	@Value("${server.port}")
	private String ServerPort;

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	/**
	 * [Test] ServerPort 확인
	 *
	 * @param productId
	 * @return
	 */
	@GetMapping("/test/{id}")
	public String getServerPort(@PathVariable("id") String productId) {
		return "Product Id is " + productId + " >>> Server Port number is " + ServerPort;
	}

	/**
	 * [Test] 서킷 브레이커 테스트
	 * @param productId
	 * @return
	 */
//	@GetMapping("/productDetail/{productId}")
//	public Product getProductDetail(@PathVariable("productId") Long productId) {
//		return productService.getProductDetail(productId);
//	}

	/**
	 * 상품 생성
	 *
	 * @param productRequestDto
	 * @param userId
	 * @param role
	 * @return
	 */
	@PostMapping
	public ProductResponseDto createProduct(@RequestBody ProductRequestDto productRequestDto,
	                                        @RequestHeader(value = "X-User-Id", required = true) String userId,
	                                        @RequestHeader(value = "X-Role", required = true) String role) {
		final Role userRole = Role.getRoleByName(role);
		if (!userRole.isManager()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. User role is not MANAGER.");
		}
		return productService.createProduct(productRequestDto, userId);
	}

	/**
	 * 전체 상품 조회
	 *
	 * @param searchDto
	 * @param pageable
	 * @return
	 */
	@GetMapping
	public Page<ProductResponseDto> getProducts(ProductSearchDto searchDto, Pageable pageable) {
		return productService.getProducts(searchDto, pageable);
	}

	/**
	 * 상품 단건 조회
	 *
	 * @param productId
	 * @return
	 */
	@GetMapping("/{productId}")
	public ProductResponseDto getProductById(@PathVariable Long productId) {
		return productService.getProductById(productId);
	}

	/**
	 * 상품 단건 수정
	 *
	 * @param productId
	 * @param orderRequestDto
	 * @param userId
	 * @param role
	 * @return
	 */
	@PutMapping("/{productId}")
	public ProductResponseDto updateProduct(@PathVariable Long productId,
	                                        @RequestBody ProductRequestDto orderRequestDto,
	                                        @RequestHeader(value = "X-User-Id", required = true) String userId,
	                                        @RequestHeader(value = "X-Role", required = true) String role) {
		return productService.updateProduct(productId, orderRequestDto, userId);
	}

	/**
	 * 상품 삭제
	 *
	 * @param productId
	 * @param deletedBy
	 */
	@DeleteMapping("/{productId}")
	public void deleteProduct(@PathVariable Long productId, @RequestParam String deletedBy) {
		productService.deleteProduct(productId, deletedBy);
	}

	/**
	 * 삼품 개수 줄이기
	 *
	 * @param id
	 * @param quantity
	 */
	@GetMapping("/{id}/reduceQuantity")
	public void reduceProductQuantity(@PathVariable Long id, @RequestParam int quantity) {
		productService.reduceProductQuantity(id, quantity);
	}
}
