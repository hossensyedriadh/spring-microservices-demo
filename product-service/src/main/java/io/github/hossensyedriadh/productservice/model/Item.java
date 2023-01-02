package io.github.hossensyedriadh.productservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public final class Item implements Serializable {
    @Serial
    private static final long serialVersionUID = 5228696112771614669L;

    private String id;

    private int quantity;

    private double pricePerUnit;

    private String productRef;
}
