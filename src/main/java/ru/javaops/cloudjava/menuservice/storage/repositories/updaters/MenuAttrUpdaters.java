package ru.javaops.cloudjava.menuservice.storage.repositories.updaters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.javaops.cloudjava.menuservice.dto.UpdateMenuRequest;
import ru.javaops.cloudjava.menuservice.model.MenuItem_;

import java.math.BigDecimal;

@Configuration
    public class MenuAttrUpdaters {
        @Bean
        MenuAttrUpdater<String> description() {
            return new MenuAttrUpdater<>(UpdateMenuRequest::getDescription, MenuItem_.description);
        }

        @Bean
        MenuAttrUpdater<String> imageUrl() {
            return new MenuAttrUpdater<>(UpdateMenuRequest::getImageUrl, MenuItem_.imageUrl);
        }

        @Bean
        MenuAttrUpdater<String> name() {
            return new MenuAttrUpdater<>(UpdateMenuRequest::getName, MenuItem_.name);
        }

        @Bean
        MenuAttrUpdater<BigDecimal> price() {

            return new MenuAttrUpdater<>(UpdateMenuRequest::getPrice, MenuItem_.price);
        }

        @Bean
        MenuAttrUpdater<Long> timeToCook() {

            return new MenuAttrUpdater<>(UpdateMenuRequest::getTimeToCook, MenuItem_.timeToCook);
        }
    }
