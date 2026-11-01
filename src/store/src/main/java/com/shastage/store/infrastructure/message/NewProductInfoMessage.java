package com.shastage.store.infrastructure.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
public class NewProductInfoMessage {
    private UUID productId;
    private String name;
    private String category;
    private Integer stock;
    private BigDecimal price;

    public String toString() {
        return "Product ID: " + productId.toString() +
                "\nName: " + name +
                "\nCategory: " + category +
                ",\nStock: " + stock +
                ",\nPrice: " + price;
    }
}
