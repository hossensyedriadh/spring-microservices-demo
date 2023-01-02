package io.github.hossensyedriadh.orderservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    private int quantity;

    @NotNull
    @PositiveOrZero
    private double pricePerUnit;

    @NotNull
    private String productRef;
}
