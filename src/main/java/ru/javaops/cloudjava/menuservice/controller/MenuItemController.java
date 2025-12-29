package ru.javaops.cloudjava.menuservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.javaops.cloudjava.menuservice.dto.CreateMenuRequest;
import ru.javaops.cloudjava.menuservice.dto.MenuItemDto;
import ru.javaops.cloudjava.menuservice.dto.SortBy;
import ru.javaops.cloudjava.menuservice.dto.UpdateMenuRequest;
import ru.javaops.cloudjava.menuservice.model.Category;
import ru.javaops.cloudjava.menuservice.service.MenuService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuService menuService;

    @PostMapping
    public MenuItemDto createMenuItem(@RequestBody CreateMenuRequest request) {
        log.info("Create menu item: {}", request);
        return menuService.createMenuItem(request);
    }

    @DeleteMapping("/v1/menu-items/{id}")
    public void deleteMenuItem(@PathVariable("id") Long id) {
        log.info("Delete menu item: {}", id);
        menuService.deleteMenuItem(id);
    }

    @PatchMapping("/v1/menu-items/{id}")
    public MenuItemDto updateMenuItem(@PathVariable("id") Long id, @RequestBody UpdateMenuRequest request) {
        log.info("Update menu item: {}", request);
        return menuService.updateMenuItem(id, request);
    }

    @GetMapping("/v1/menu-items/{id}")
    public MenuItemDto getMenuItem(@PathVariable("id") Long id) {
        log.info("Get menu item: {}", id);
        return menuService.getMenu(id);
    }

    @GetMapping("/v1/menu-items")
    public List<MenuItemDto> getMenuItems(
            @RequestParam Category category,
            @RequestParam SortBy sort) {
        log.info("Get menu items for category: {}", category);
        return menuService.getMenusFor(category, sort);
    }
}
