package ru.yandex.megamarket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.megamarket.model.ShopUnit;
import ru.yandex.megamarket.model.ShopUnitType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ShopUnitRepository extends JpaRepository<ShopUnit, UUID> {
    List<ShopUnit> getShopUnitByDateBetweenAndType(Instant dateMinusDay, Instant date, ShopUnitType offer);
}
