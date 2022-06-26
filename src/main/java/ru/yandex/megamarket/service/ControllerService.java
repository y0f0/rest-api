package ru.yandex.megamarket.service;

import org.springframework.stereotype.Service;
import ru.yandex.megamarket.model.ShopUnit;
import ru.yandex.megamarket.model.ShopUnitType;
import ru.yandex.megamarket.repository.ShopUnitRepository;

import javax.validation.Valid;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class ControllerService {
    private final ShopUnitRepository repository;

    public ControllerService(ShopUnitRepository repository) {
        this.repository = repository;
    }

    public void create(@Valid ShopUnit unit) {
        repository.save(unit);

        if (unit.getParent() != null) {
            recursiveUpdateParent(unit.getParent(), unit.getDate());
        }
    }

    private void recursiveUpdateParent(ShopUnit unit, Instant updateDate) {
        if (updateDate != null && updateDate.isAfter(unit.getDate())) {
            unit.setDate(updateDate);
        }
        repository.save(unit);

        if (unit.getParent() != null) {
            ShopUnit parent = unit.getParent();
            recursiveUpdateParent(parent, updateDate);
        }
    }


    public ShopUnit findById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public void update(ShopUnit unit, UUID id) {
        ShopUnit old = findById(id);
        old.update(unit);
        repository.save(old);
        if (unit.getParent() != null) {
            recursiveUpdateParent(unit.getParent(), unit.getDate());
        }
    }

    public boolean deleteById(UUID id) {
        ShopUnit unit = findById(id);
        if (unit != null) {
            if (unit.getParentId() != null) {
                ShopUnit parent = unit.getParent();
                parent.getChildren().remove(unit);
                repository.save(parent);
                repository.delete(unit);
            } else {
                repository.delete(unit);
            }
            return true;
        }
        return false;
    }

    public List<ShopUnit> getSales(Instant date) {
        Instant dateMinusDay = date.minus(1, ChronoUnit.DAYS);
        return repository.getShopUnitByDateBetweenAndType(dateMinusDay, date, ShopUnitType.OFFER);
    }
}
