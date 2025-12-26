package ru.javaops.cloudjava.menuservice.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.javaops.cloudjava.menuservice.dto.CreateMenuRequest;
import ru.javaops.cloudjava.menuservice.dto.MenuItemDto;
import ru.javaops.cloudjava.menuservice.dto.SortBy;
import ru.javaops.cloudjava.menuservice.dto.UpdateMenuRequest;
import ru.javaops.cloudjava.menuservice.exception.MenuServiceException;
import ru.javaops.cloudjava.menuservice.mapper.MenuItemMapper;
import ru.javaops.cloudjava.menuservice.model.Category;
import ru.javaops.cloudjava.menuservice.service.MenuService;
import ru.javaops.cloudjava.menuservice.storage.repositories.MenuItemRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class MenuServiceImpl implements MenuService {

    public static final String MENU_ITEM_NOT_FOUND = "Menu item with id %d not found";
    public static final String MENU_ITEM_ALREADY_EXISTS = "Menu item with this name already exists";
    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;

    @Override
    public MenuItemDto createMenuItem(CreateMenuRequest dto) {
        try {
            var savedDomain = menuItemRepository.save(menuItemMapper.toDomain(dto));
            return menuItemMapper.toDto(savedDomain);
        } catch (DataIntegrityViolationException e) {
            throw new MenuServiceException(MENU_ITEM_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
    }

    @Override
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

    @Override
    public MenuItemDto updateMenuItem(Long id, UpdateMenuRequest update) {
        try {
            var updated = menuItemRepository.updateMenu(id, update);
            if (updated == 0) {
                throw new MenuServiceException(MENU_ITEM_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND);
            }
        } catch (DataIntegrityViolationException e) {
            throw new MenuServiceException(MENU_ITEM_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        return menuItemRepository.findById(id)
                .map(menuItemMapper::toDto)
                .orElseThrow(() -> new MenuServiceException(MENU_ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    public MenuItemDto getMenu(Long id) {
        var domain = menuItemRepository
                .findById(id).orElseThrow(
                        () -> new MenuServiceException(MENU_ITEM_NOT_FOUND.formatted(id), HttpStatus.NOT_FOUND));
        return menuItemMapper.toDto(domain);
    }

    @Override
    public List<MenuItemDto> getMenusFor(Category category, SortBy sortBy) {
        var items = menuItemRepository.getMenusFor(category, sortBy);
        return menuItemMapper.toDtoList(items);
    }
}
