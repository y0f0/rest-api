package ru.yandex.megamarket.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.megamarket.exception.NotFound;
import ru.yandex.megamarket.model.Error;
import ru.yandex.megamarket.model.ShopUnit;
import ru.yandex.megamarket.model.ShopUnitImportRequest;
import ru.yandex.megamarket.service.ControllerService;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
public class ApiController {
    private final ControllerService service;

    public ApiController(ControllerService service) {
        this.service = service;
    }

    /**
     * Импортирует новые товары и/или категории. Товары/категории импортированные повторно обновляют текущие.
     * Изменение типа элемента с товара на категорию или с категории на товар не допускается. Порядок элементов в запросе
     * является произвольным.
     *
     * @param body
     */
    @PostMapping("/imports")
    @ResponseStatus(HttpStatus.OK)
    public void importItems(@Valid @RequestBody ShopUnitImportRequest body) {
        if (body.getItems() == null) {
            return;
        }

        for (ShopUnit item : body.getItems()) {
            item.setDate(body.getUpdateDate());
            UUID parentId = item.getParentId();
            if (parentId != null) {
                item.setParent(service.findById(parentId));
            }
            if (service.findById(item.getId()) != null) {
                service.update(item, item.getId());
            } else {
                service.create(item);
            }
        }
    }

    /**
     * Получить информацию об элементе по идентификатору. При получении информации о категории также предоставляется
     * информация о её дочерних элементах.
     *
     * @param id Идентификатор элемента.
     * @return shop unit категория/товар.
     */
    @GetMapping(value = "/nodes/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ShopUnit findById(@PathVariable(name = "id") @Valid UUID id) {
        ShopUnit unit = service.findById(id);
        if (unit != null) {
            return unit;
        }
        throw new NotFound();
    }

    /**
     * Удалить элемент по идентификатору. При удалении категории удаляются все дочерние элементы. Доступ к статистике
     * (истории обновлений) удаленного элемента невозможен. Так как время удаления не передается, при удалении элемента
     * время обновления родителя изменять не нужно.
     *
     * @param id Идентификатор.
     */
    @DeleteMapping(value = "/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable(name = "id") @Valid UUID id) {
        if (!service.deleteById(id)) {
            throw new NotFound();
        }
    }

    /**
     * Получение списка товаров, цена которых была обновлена за последние 24 часа включительно [now() - 24h, now()] от
     * времени переданном в запросе. Обновление цены не означает её изменение. Обновления цен удаленных товаров
     * недоступны. При обновлении цены товара, средняя цена категории, которая содержит этот товар, тоже обновляется.
     *
     * @param date Дата и время запроса. Дата должна обрабатываться согласно ISO 8601 (такой придерживается OpenAPI).
     *             Если дата не удовлетворяет данному формату, необходимо отвечать 400
     * @return shop unit категория/товар
     */
    @GetMapping(value = "/sales")
    @ResponseStatus(HttpStatus.OK)
    public List<ShopUnit> getSales(@RequestParam @Valid Instant date) {
        return service.getSales(date);
    }


    /**
     * Получение статистики (истории обновлений) по товару/категории за заданный полуинтервал [from, to). Статистика по
     * удаленным элементам недоступна.
     * <p>
     * - цена категории - это средняя цена всех её товаров, включая товары дочерних категорий.Если категория не содержит
     * товаров цена равна null. При обновлении цены товара, средняя цена категории, которая содержит этот товар, тоже
     * обновляется.
     * - можно получить статистику за всё время.
     *
     * @param id        UUID товара/категории для которой будет отображаться статистика.
     * @param dateStart Дата и время начала интервала, для которого считается статистика.
     * @param dateEnd   Дата и время конца интервала, для которого считается статистика.
     * @return статистика (история обновлений) по товару/категории за заданном полуинтервале [dateStart, dateEnd).
     */
    @GetMapping(value = "/node/{id}/statistic")
    @ResponseStatus(HttpStatus.OK)
    public List<ShopUnit> getStatistic(@PathVariable(name = "id") @Valid UUID id, @RequestParam @Valid Instant dateStart,
                                       @RequestParam @Valid Instant dateEnd) {
        return new LinkedList<>();
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFound.class})
    public Error notFountError() {
        return new Error(HttpStatus.NOT_FOUND, "Item not found");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,
            HttpMessageNotReadableException.class, IllegalArgumentException.class, Exception.class, Throwable.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error validationError() {
        return new Error(HttpStatus.BAD_REQUEST, "Validation Failed");
    }
}
