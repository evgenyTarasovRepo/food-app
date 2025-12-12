package ru.javaops.cloudjava.menuservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Ingredient implements Serializable {

    @NotBlank
    private String name;

    @NotNull
    @PositiveOrZero
    private BigDecimal calories;
}
