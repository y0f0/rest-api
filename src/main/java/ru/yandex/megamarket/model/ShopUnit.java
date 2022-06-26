package ru.yandex.megamarket.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.*;

/**
 * Категория или товар.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Valid
@Table(name = "shop_unit")
public class ShopUnit {
    /**
     * Уникальный идентификатор.
     */
    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private UUID id;

    /**
     * Имя категории.
     */
    @Column(name = "name", nullable = false)
    @NotNull
    private String name;

    /**
     * Тип shop unit категория/товар.
     */
    @Column(name = "type", nullable = false)
    @NotNull
    private ShopUnitType type;

    /**
     * Целое число, для категории - это средняя цена всех дочерних товаров(включая товары подкатегорий). Если цена
     * является не целым числом, округляется в меньшую сторону до целого числа. Если категория не содержит товаров цена
     * равна null.
     */
    @Column(name = "price")
    @Min(1)
    private Long price;

    /**
     * Время последнего обновления элемента.
     */
    @Column(name = "date", nullable = false)
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]XXX", timezone = "UTC")
    private Instant date;

    /**
     * Родительская категория.
     */
    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private ShopUnit parent;

    /**
     * UUID родительской категории.
     */
    @Column(name = "parent_uuid")
    private UUID parentId;

    /**
     * Список всех дочерних товаров\категорий. Для товаров поле равно null.
     */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShopUnit> children = null;

    /**
     * Возвращает цену категории/товара. цена категории - это средняя цена всех её товаров, включая товары дочерних
     * категорий. Если категория не содержит товаров цена равна null. При обновлении цены товара, средняя цена
     * категории, которая содержит этот товар, тоже обновляется
     * @return цену товара/средняя цена товаров данной категории.
     */
    public long getPrice() {
        if (type == ShopUnitType.OFFER) {
            return price;
        } else {

            if (children == null) {
                children = new LinkedList<>();
            }

            return (long) getAverage();
        }
    }

    /**
     * Возвращает цену категории - это средняя цена всех её товаров, включая товары дочерних категорий.
     * @return цена категории.
     */
    private double getAverage() {
        double sum = 0;
        long n = 0;
        if (children.size() == 0) {
            return 0;
        }

        for (ShopUnit child : children) {
            if (child.type == ShopUnitType.CATEGORY) {
                sum += child.getAverage() * child.getChildren().size();
                n += child.getChildren().size();
            } else {
                sum += child.price;
                n++;
            }
        }
        if (n != 0) {
            sum /= n;
        }
        return sum;
    }

    /**
     * Достать детей категории. Если товар возвращает null.
     * @return Список детей категории (подкатегории, товары).
     */
    public List<ShopUnit> getChildren() {
        if (type == ShopUnitType.OFFER) {
            return null;
        }
        return children;
    }

    /**
     * Обновить shop unit.
     * @param other новый shop unit.
     */
    public void update(ShopUnit other) {
        this.name = other.getName();
        this.price = other.getPrice();
        this.date = other.getDate();
        this.parentId = other.getParentId();
        this.parent = other.getParent();
    }

    public void setDate(Instant date) {
        this.date = date;
    }
}