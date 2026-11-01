package com.shastage.store.infrastructure.mapper;

import com.shastage.store.domain.model.Product;
import com.shastage.store.infrastructure.message.NewProductInfoMessage;

import java.math.BigDecimal;
import java.util.UUID;

public class MessageMapper {
    public static NewProductInfoMessage toNewProductInfoMessage (Product product) {
        return new NewProductInfoMessage(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getAvailableStock(),
                product.getPrice()
        );
    }
}
