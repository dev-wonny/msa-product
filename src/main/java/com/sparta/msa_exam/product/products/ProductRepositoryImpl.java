package com.sparta.msa_exam.product.products;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.msa_exam.product.core.Product;
import com.sparta.msa_exam.product.core.QProduct;
import com.sparta.msa_exam.product.dto.ProductResponseDto;
import com.sparta.msa_exam.product.dto.ProductSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ProductRepositoryImpl implements ProductRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QProduct product = QProduct.product; // QProduct 인스턴스 생성

	public ProductRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public Page<ProductResponseDto> searchProducts(ProductSearchDto searchDto, Pageable pageable) {
		List<OrderSpecifier<?>> orders = getAllOrderSpecifiers(pageable);

		// 페이징 처리 검색: Query DSL 편하다
		QueryResults<Product> results = queryFactory
				.selectFrom(product)
				.where(
						nameContains(searchDto.getName()),
						descriptionContains(searchDto.getDescription()),
						priceBetween(searchDto.getMinPrice(), searchDto.getMaxPrice()),
						quantityBetween(searchDto.getMinQuantity(), searchDto.getMaxQuantity())
				)
				.orderBy(orders.toArray(new OrderSpecifier[0]))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetchResults();

		List<ProductResponseDto> content = results.getResults().stream()
				.map(Product::toResponseDto)
				.collect(Collectors.toList());
		final long total = results.getTotal();

		return new PageImpl<>(content, pageable, total);
	}

	private BooleanExpression nameContains(String name) {
		return name != null ? product.name.containsIgnoreCase(name) : null;
	}

	private BooleanExpression descriptionContains(String description) {
		return description != null ? product.description.containsIgnoreCase(description) : null;
	}

	private BooleanExpression priceBetween(Double minPrice, Double maxPrice) {
		if (minPrice != null && maxPrice != null) {
			return product.supply_price.between(minPrice, maxPrice);
		} else if (minPrice != null) {
			return product.supply_price.goe(minPrice);
		} else if (maxPrice != null) {
			return product.supply_price.loe(maxPrice);
		} else {
			return null;
		}
	}

	private BooleanExpression quantityBetween(Integer minQuantity, Integer maxQuantity) {
		if (minQuantity != null && maxQuantity != null) {
			return product.quantity.between(minQuantity, maxQuantity);
		} else if (minQuantity != null) {
			return product.quantity.goe(minQuantity);
		} else if (maxQuantity != null) {
			return product.quantity.loe(maxQuantity);
		} else {
			return null;
		}
	}

	private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable) {
		List<OrderSpecifier<?>> orders = new ArrayList<>();

		if (pageable.getSort() != null) {
			for (Sort.Order sortOrder : pageable.getSort()) {
				com.querydsl.core.types.Order direction = sortOrder.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;
				switch (sortOrder.getProperty()) {
					case "createdAt":
						orders.add(new OrderSpecifier<>(direction, product.createdAt));
						break;
					case "price":
						orders.add(new OrderSpecifier<>(direction, product.supply_price));
						break;
					case "quantity":
						orders.add(new OrderSpecifier<>(direction, product.quantity));
						break;
					default:
						break;
				}
			}
		}

		return orders;
	}
}