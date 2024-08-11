package com.sparta.msa_exam.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * json 요청시 들어오는 key라서 product라고 넣지 않음
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {
	private String name;
	private String description;
	private Integer price;
	private Integer quantity;
}