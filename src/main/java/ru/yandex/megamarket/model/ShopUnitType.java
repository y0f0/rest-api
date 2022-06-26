package ru.yandex.megamarket.model;


/**
 * Тип элемента - категория или товар
 */
public enum ShopUnitType {
    OFFER("OFFER"),
    CATEGORY("CATEGORY");

    private String value;

    ShopUnitType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
