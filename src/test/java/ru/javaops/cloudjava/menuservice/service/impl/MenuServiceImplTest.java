package ru.javaops.cloudjava.menuservice.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.javaops.cloudjava.menuservice.BaseTest;
import ru.javaops.cloudjava.menuservice.dto.MenuItemDto;
import ru.javaops.cloudjava.menuservice.dto.SortBy;
import ru.javaops.cloudjava.menuservice.exception.MenuServiceException;
import ru.javaops.cloudjava.menuservice.model.Category;
import ru.javaops.cloudjava.menuservice.service.MenuService;
import ru.javaops.cloudjava.menuservice.storage.repositories.MenuItemRepository;
import ru.javaops.cloudjava.menuservice.testutils.TestData;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
public class MenuServiceImplTest extends BaseTest {

    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuItemRepository repository;

    @Test
    void getMenusFor_DRINKS_returnsCorrectList() {
        List<MenuItemDto> drinks = menuService.getMenusFor(Category.DRINKS, SortBy.AZ);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItemDto::getName, List.of("Cappuccino", "Tea", "Wine"));
    }

    @Test
    void createMenuItem_createsMenuItem() {
        var dto = TestData.createMenuRequest();
        // Вычитаем некоторое количество наносекунд из-за возможных проблем со сравнением дат (проявляется на Windows,
        // при тестировании на Ubuntu и Mac такой проблемы не возникало)
        // так как Postgres не поддерживает точность дат до наносекунд из коробки
        var now = LocalDateTime.now().minusNanos(1000);
        MenuItemDto result = menuService.createMenuItem(dto);
        assertThat(result.getId()).isNotNull();
        assertFieldsEquality(result, dto, "name", "description", "price", "imageUrl", "timeToCook");
        assertThat(result.getCreatedAt()).isAfter(now);
        assertThat(result.getUpdatedAt()).isAfter(now);
    }

    @Test
    void getMenu_ById() {
        var id = getIdByName("Cappuccino");

        var menuItem = menuService.getMenu(id);

        assertThat(menuItem).isNotNull();
        assertThat(id).isEqualTo(menuItem.getId());
        assertThat(menuItem.getName()).isEqualTo("Cappuccino");
    }

    @Test
    void getMenuById_throws_whenMenuNotFound() {
        var id = Long.MAX_VALUE;

        assertThatThrownBy(() -> menuService.getMenu(id))
                .isInstanceOf(MenuServiceException.class);
    }

    @Test
    void deleteMenuItem_byId() {
        var id = 1L;
        menuService.deleteMenuItem(id);

        assertThatThrownBy(() -> menuService.getMenu(id))
                .isInstanceOf(MenuServiceException.class);
    }

    @Test
    void createMenuItem_throws_whenMenuWithSameNameAlreadyExists() {
        var dto = TestData.createMenuRequest();

        menuService.createMenuItem(dto);

        assertThatThrownBy(() -> menuService.createMenuItem(dto))
                .isInstanceOf(MenuServiceException.class);
    }

    @Test
    void updateMenuItem_updateExistingMenuItem() {
        var id = getIdByName("Cappuccino");
        var dto = TestData.updateMenuBuilder("New cappuccino", 3.50, 5000L, "Delicious", "http://images.com/new_cappuccino_1.png");

        MenuItemDto updated = menuService.updateMenuItem(id, dto);

        assertThat(updated.getId()).isEqualTo(id);
        assertThat(updated.getName()).isEqualTo(dto.getName());
        assertThat(updated.getPrice()).isEqualByComparingTo(dto.getPrice());
        assertThat(updated.getImageUrl()).isEqualTo(dto.getImageUrl());
        assertThat(updated.getTimeToCook()).isEqualTo(dto.getTimeToCook());
        assertThat(updated.getDescription()).isEqualTo(dto.getDescription());
    }

    @Test
    void updateMenuItem_throws_whenMenuDoesNotExists() {
        var id = Long.MAX_VALUE;
        var dto = TestData.updateMenuBuilder("Tuna salad", 65.50, 7000L, "Delicious tuna salad", "http://images.com/tuna_salad.png");

        assertThatThrownBy(() -> menuService.updateMenuItem(id, dto))
                .isInstanceOf(MenuServiceException.class);
    }

    @Test
    void updateMenuItem_throws_whenNewNameIsNotUnique() {
        var id = 1L;
        var dto = TestData.updateMenuBuilder("Cappuccino", 65.50, 7000L, "Super", "http://images.com/cappuccino-3.png");

        assertThatThrownBy(() -> menuService.updateMenuItem(id, dto))
                .isInstanceOf(MenuServiceException.class);
    }
}
