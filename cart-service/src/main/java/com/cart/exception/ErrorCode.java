package com.cart.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    NOT_ENOUGH_STOCK(2001, "Not enough stock", HttpStatus.BAD_REQUEST),
    CART_NOT_FOUND(3001, "Cart not found", HttpStatus.NOT_FOUND),
    ITEM_NOT_FOUND(3002, "Item not found in cart", HttpStatus.NOT_FOUND),
    ORDER_CREATION_FAILED(4001, "Order creation failed", HttpStatus.INTERNAL_SERVER_ERROR)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
