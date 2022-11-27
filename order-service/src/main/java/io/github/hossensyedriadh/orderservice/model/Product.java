package io.github.hossensyedriadh.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public final class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 4789475612634306943L;

    private String id;

    private String name;

    private String category;

    private String specifications;

    private int stock;

    private double price;
}
