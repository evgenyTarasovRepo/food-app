package ru.javaops.cloudjava.menuservice.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;

    @Override
    public MenuItemDto createMenuItem(CreateMenuRequest dto) {
        var menuItem = menuItemMapper.toDomain(dto);
        try {
            return menuItemMapper.toDto(menuItemRepository.save(menuItem));
        } catch (DataIntegrityViolationException e) {
            var msg = String.format("Failed to create MenuItem: %s. Reason: Item with name %s already exists.", dto, dto.getName());
            throw new MenuServiceException(msg, HttpStatus.CONFLICT);
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
                var msg = String.format("MenuItem with id=%d not found.", id);
                throw new MenuServiceException(msg.formatted(id), HttpStatus.NOT_FOUND);
            }
            return getMenu(id);
        } catch (DataIntegrityViolationException e) {
            var msg = String.format("Failed to update MenuItem with ID: %d. Reason: Item with name %s already exists.",
                    id, update.getName());
            throw new MenuServiceException(msg, HttpStatus.CONFLICT);
        }
    }

    @Override
    public MenuItemDto getMenu(Long id) {
        return menuItemRepository
                .findById(id)
                .map(menuItemMapper::toDto)
                .orElseThrow(
                        () -> new MenuServiceException(String.format("MenuItem with id=%d not found.", id), HttpStatus.NOT_FOUND));
    }

    @Override
    public List<MenuItemDto> getMenusFor(Category category, SortBy sortBy) {
        var items = menuItemRepository.getMenusFor(category, sortBy);
        return menuItemMapper.toDtoList(items);
    }
}
