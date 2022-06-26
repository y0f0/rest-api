package ru.yandex.megamarket.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.List;

/**
 * ShopUnitImportRequest
 */
@Data
@AllArgsConstructor
public class ShopUnitImportRequest {
    /**
     * Импортируемые элементы
     **/
    @Nullable
    private List<ShopUnit> items;

    /**
     * Время обновления добавляемых товаров/категорий.
     **/
    @NonNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]XXX", timezone = "UTC")
    private Instant updateDate;
}
