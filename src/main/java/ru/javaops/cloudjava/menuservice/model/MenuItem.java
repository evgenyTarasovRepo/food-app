package ru.javaops.cloudjava.menuservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu_items")
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false)
    @NotNull
    private String name;

    @Column(name = "description", nullable = false)
    @NotNull
    private String description;

    @Column(name = "price", precision = 6, scale = 2)
    private BigDecimal price;

    @Column(name = "category", nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "time_to_cook", nullable = false)
    @NotNull
    private Long timeToCook;

    @Column(name = "weight", nullable = false)
    @NotNull
    private Double weight;

    @Column(name = "image_url", nullable = false)
    @NotNull
    private String imageUrl;

    @Type(JsonBinaryType.class)
    @Column(name = "ingredient_collection", columnDefinition = "jsonb", nullable = false)
    @NotNull
    private IngredientCollection ingredientCollection;

    @Column(name = "created_at")
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
