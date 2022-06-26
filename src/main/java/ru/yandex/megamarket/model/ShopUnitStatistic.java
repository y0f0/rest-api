package ru.yandex.megamarket.model;

import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
public class ShopUnitStatistic {
    private UUID id;

    private String name;

    private Instant date;

    private UUID parentId;

    private Long price;

    private ShopUnitType type;
}

