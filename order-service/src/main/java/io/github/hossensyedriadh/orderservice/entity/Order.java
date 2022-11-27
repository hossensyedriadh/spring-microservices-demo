package io.github.hossensyedriadh.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.hossensyedriadh.orderservice.enumerator.OrderStatus;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "orders")
public final class Order implements Serializable {
    @Serial
    private static final long serialVersionUID = 4673322602057857474L;

    @Setter(AccessLevel.NONE)
    @MongoId
    private String id;

    @NotNull
    private List<Item> items;

    @NotNull
    @PositiveOrZero
    private double totalPrice;

    @NotNull
    private OrderStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss Z")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZonedDateTime createdOn;

    @NotNull
    private String orderBy;
}