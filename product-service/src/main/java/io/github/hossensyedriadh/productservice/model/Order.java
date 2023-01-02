package io.github.hossensyedriadh.productservice.model;

import io.github.hossensyedriadh.productservice.enumerator.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public final class Order implements Serializable {
    @Serial
    private static final long serialVersionUID = 4673322602057857474L;

    private String id;

    private List<Item> items;

    private double totalPrice;

    private OrderStatus status;

    private long createdOn;

    private String orderBy;
}
