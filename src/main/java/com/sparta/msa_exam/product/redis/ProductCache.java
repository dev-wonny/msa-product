package com.sparta.msa_exam.product.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("productCache")
public class ProductCache implements Serializable {
	@Id
	private Long product_id;
	private String name;
	private Integer supply_price;
}
