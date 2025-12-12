package ru.javaops.cloudjava.menuservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.http.HttpStatus;
import ru.javaops.cloudjava.menuservice.exception.MenuServiceException;

public enum Category {
    BREAKFAST,
    LUNCH,
    DINNER,
    DRINKS,
    SNACKS,
    SALADS;

    @JsonCreator
    public static Category fromString(String category) {
        try {
            return Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            var msg = "Failed to crete Category from string: %s".formatted(category);
            throw new MenuServiceException(msg, HttpStatus.BAD_REQUEST);
        }
    }
}
