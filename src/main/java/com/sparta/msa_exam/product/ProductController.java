package com.sparta.msa_exam.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

	@Value("${server.port}")
	private String ServerPort;

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping("/product/{id}")
	public String getProduct(@PathVariable("id") String productId) {
		return "Product Id is " + productId + " >>> Server Port number is " + ServerPort;
	}

	@GetMapping("/productDetail/{productId}")
	public Product getProductDetail(@PathVariable("productId") String productId) {
		return productService.getProductDetail(productId);
	}
}
