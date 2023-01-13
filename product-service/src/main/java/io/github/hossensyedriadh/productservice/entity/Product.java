package io.github.hossensyedriadh.productservice.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "products")
public final class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = -4438707343816647827L;

    @Setter(AccessLevel.NONE)
    @MongoId(FieldType.STRING)
    private String id;
    
    @NotNull
    @Pattern(message = "Only upper-case, lower-case letters, numbers, hyphens, whitespaces, parentheses and periods are allowed", regexp = "^[a-zA-Z0-9\\s.\\-()]+$")
    @Length(min = 5, max = 150, message = "Length must be within 5-150 characters")
    private String name;

    @NotNull
    @Pattern(message = "Only upper-case, lower-case letters, numbers, whitespaces, hyphens are allowed", regexp = "^[a-zA-Z0-9\\s\\-]+$")
    @Length(min = 5, max = 75, message = "Length must be within 5-75 characters")
    private String category;

    private String specifications;

    @NotNull
    @PositiveOrZero
    private int stock;

    @NotNull
    @PositiveOrZero
    private double price;
}
