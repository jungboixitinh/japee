package com.cart.mapper;

import com.cart.dto.response.CartResponse;
import com.cart.entity.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartResponse toCartResponse(Cart cart);
}
