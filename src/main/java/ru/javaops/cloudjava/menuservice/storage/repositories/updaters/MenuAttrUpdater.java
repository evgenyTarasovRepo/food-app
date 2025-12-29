package ru.javaops.cloudjava.menuservice.storage.repositories.updaters;

import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.AllArgsConstructor;
import ru.javaops.cloudjava.menuservice.dto.UpdateMenuRequest;
import ru.javaops.cloudjava.menuservice.model.MenuItem;

import java.util.function.Function;

@AllArgsConstructor
public class MenuAttrUpdater<V> {

    private Function<UpdateMenuRequest, V> fieldExtractor;
    private SingularAttribute<MenuItem, V> attribute;

    public void updateAttr(CriteriaUpdate<MenuItem> criteria, UpdateMenuRequest dto) {
        var updatedValue = fieldExtractor.apply(dto);
        if (updatedValue != null) {
            criteria.set(attribute, updatedValue);
        }

    }
}
