package com.product.mapper;

import com.product.dto.response.ProductResponse;
import com.product.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toProductResponse(Product product);
}
