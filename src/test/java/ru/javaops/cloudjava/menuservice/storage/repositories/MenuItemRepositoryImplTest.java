package ru.javaops.cloudjava.menuservice.storage.repositories;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.cloudjava.menuservice.BaseTest;
import ru.javaops.cloudjava.menuservice.dto.SortBy;
import ru.javaops.cloudjava.menuservice.model.Category;
import ru.javaops.cloudjava.menuservice.model.MenuItem;
import ru.javaops.cloudjava.menuservice.storage.repositories.updaters.MenuAttrUpdaters;
import ru.javaops.cloudjava.menuservice.testutils.TestData;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@Import(MenuAttrUpdaters.class)
@Transactional(propagation = Propagation.NEVER)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SqlGroup({
        @Sql(
                scripts = "classpath:insert-menu.sql",
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
        ),
        @Sql(
                scripts = "classpath:clear-menus.sql",
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
        )
})
class MenuItemRepositoryImplTest extends BaseTest {
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private EntityManager em;

    @Test
    void updateMenu_updatesMenu_whenAllUpdateFieldsAreSet() {
        var dto = TestData.updateMenuFullRequest();
        var id = getIdByName("Cappuccino");
        int updateCount = menuItemRepository.updateMenu(id, dto);
        assertThat(updateCount).isEqualTo(1);
        MenuItem updated = menuItemRepository.findById(id).get();
        assertFieldsEquality(updated, dto, "name", "description", "price", "timeToCook", "imageUrl");
    }

    @Test
    void updateMenu_updatesMenu_whenSomeUpdateFieldsAreSet() {
        var dto = TestData.updateMenuBuilder("New salad", 20.45, null, "Health salad", null);

        var id = getIdByName("Green Salad");
        var beforeUpdate = menuItemRepository.findById(id).get();

        int updateCount = menuItemRepository.updateMenu(id, dto);
        assertThat(updateCount).isEqualTo(1);

        MenuItem updated = menuItemRepository.findById(id).get();
        assertFieldsEquality(updated, dto, "name", "description", "price");

        assertThat(updated.getImageUrl()).isEqualTo(beforeUpdate.getImageUrl());
        assertThat(updated.getTimeToCook()).isEqualTo(beforeUpdate.getTimeToCook());
    }

    @Test
    void updateMenu_throws_whenUpdateRequestHasNotUniqueName() {
        var dto = TestData.updateMenuBuilder("Tea", 20.45, null, "Health salad", null);
        var cappuccinoId = getIdByName("Cappuccino");

        assertThatThrownBy(() -> menuItemRepository.updateMenu(cappuccinoId, dto))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void updateMenu_updatesNothing_whenNoMenuPresentInDB() {
        var dto = TestData.updateMenuBuilder("New salad", 20.45, null, "Health salad", null);
        var id = Long.MAX_VALUE;

        int updateCount = menuItemRepository.updateMenu(id, dto);

        assertThat(updateCount).isZero();
    }

    @ParameterizedTest
    @EnumSource(SortBy.class)
    void getMenusFor_returnsSortedListForEachSort(SortBy sortBy) {
        var drinks = menuItemRepository.getMenusFor(Category.DRINKS, sortBy);

        assertThat(drinks).hasSize(3);

        switch (sortBy) {
            case PRICE_ASC -> assertThat(drinks)
                    .extracting(MenuItem::getPrice)
                    .isSorted();
            case PRICE_DESC -> assertThat(drinks)
                    .extracting(MenuItem::getPrice)
                    .isSortedAccordingTo(Comparator.reverseOrder());
            case AZ -> assertThat(drinks)
                    .extracting(MenuItem::getName)
                    .isSorted();
            case ZA -> assertThat(drinks)
                    .extracting(MenuItem::getName)
                    .isSortedAccordingTo(Comparator.reverseOrder());
            case DATE_ASC -> assertThat(drinks)
                    .extracting(MenuItem::getCreatedAt)
                    .isSorted();
            case DATE_DESC -> assertThat(drinks)
                    .extracting(MenuItem::getCreatedAt)
                    .isSortedAccordingTo(Comparator.reverseOrder());

        }
    }

    @Test
    void getMenusFor_returnsCorrectListForDRINKS_sortedByPriceAsc() {
        var drinks = menuItemRepository. getMenusFor(Category.DRINKS, SortBy.PRICE_ASC);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItem::getName, List.of("Cappuccino", "Wine", "Tea"));
    }

    @Test
    void getMenusFor_returnsCorrectListForDRINKS_sortedByPriceDesc() {
        var drinks = menuItemRepository.getMenusFor(Category.DRINKS, SortBy.PRICE_DESC);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItem::getName, List.of("Tea", "Wine", "Cappuccino"));
    }

    @Test
    void getMenusFor_returnsCorrectListForDRINKS_sortedByNameAsc() {
        var drinks = menuItemRepository.getMenusFor(Category.DRINKS, SortBy.AZ);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItem::getName, List.of("Cappuccino", "Tea", "Wine"));
    }

    @Test
    void getMenusFor_returnsCorrectListForDRINKS_sortedByNameDesc() {
        var drinks = menuItemRepository.getMenusFor(Category.DRINKS, SortBy.ZA);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItem::getName, List.of("Wine", "Tea", "Cappuccino"));
    }

    @Test
    void getMenusFor_returnsCorrectListForDRINKS_sortedByDateAsc() {
        var drinks = menuItemRepository.getMenusFor(Category.DRINKS, SortBy.DATE_ASC);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItem::getName, List.of("Cappuccino", "Wine", "Tea"));
    }

    @Test
    void getMenusFor_returnsCorrectListForDRINKS_sortedByDateDesc() {
        var drinks = menuItemRepository.getMenusFor(Category.DRINKS, SortBy.DATE_DESC);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItem::getName, List.of("Tea", "Wine", "Cappuccino"));
    }
}
