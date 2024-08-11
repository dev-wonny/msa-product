package com.sparta.msa_exam.product.dto;

import com.sparta.msa_exam.product.core.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
	private Long product_id;
	private String name;
	private Integer supply_price;

	/**
	 * 요구사항: 응답 객체
	 * @param product
	 * @return
	 */
	public static ProductDto fromEntity(Product product) {
		return ProductDto.builder()
				.product_id(product.getProduct_id())
				.name(product.getName())
				.supply_price(product.getSupply_price())
				.build();
	}
}
