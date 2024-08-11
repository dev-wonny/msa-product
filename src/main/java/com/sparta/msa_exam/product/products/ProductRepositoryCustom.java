package com.sparta.msa_exam.product.products;

import com.sparta.msa_exam.product.dto.ProductResponseDto;
import com.sparta.msa_exam.product.dto.ProductSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
	Page<ProductResponseDto> searchProducts(ProductSearchDto searchDto, Pageable pageable);
}