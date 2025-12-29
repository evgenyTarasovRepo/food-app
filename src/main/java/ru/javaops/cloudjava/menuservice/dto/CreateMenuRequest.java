package ru.javaops.cloudjava.menuservice.dto;

import lombok.Builder;
import lombok.Data;
import ru.javaops.cloudjava.menuservice.model.Category;
import ru.javaops.cloudjava.menuservice.model.IngredientCollection;

import java.math.BigDecimal;

@Data
@Builder
public class CreateMenuRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Category category;
    private long timeToCook;
    private double weight;
    private String imageUrl;
    private IngredientCollection ingredientCollection;
}
