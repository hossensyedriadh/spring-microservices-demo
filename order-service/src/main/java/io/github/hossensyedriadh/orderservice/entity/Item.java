package io.github.hossensyedriadh.orderservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public final class Item implements Serializable {
    @Serial
    private static final long serialVersionUID = 5228696112771614669L;

    @Id
    private String id;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    @PositiveOrZero
    private Double pricePerUnit;

    @NotNull
    private String productRef;
}
