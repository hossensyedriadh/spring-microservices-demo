package io.github.hossensyedriadh.orderservice.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "items")
public final class Item implements Serializable {
    @Serial
    private static final long serialVersionUID = 5228696112771614669L;

    @Setter(AccessLevel.NONE)
    @MongoId
    private String id;

    @NotNull
    @Positive
    private int quantity;

    @NotNull
    @PositiveOrZero
    private double pricePerUnit;

    @NotNull
    private String productRef;
}
