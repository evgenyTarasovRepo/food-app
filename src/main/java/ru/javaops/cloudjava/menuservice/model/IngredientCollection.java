package ru.javaops.cloudjava.menuservice.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class IngredientCollection implements Serializable {

    @NotNull
    List<Ingredient> ingredients;
}
