package com.foodrescue.donation.entity;

import com.foodrescue.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Food category. One category has many donations (1:M, inverse side of {@link Donation#getCategory()}).
 * The list is lazy and read-only here; donations are always saved through their own repository.
 */
@Entity
@Table(name = "food_categories")
@Getter
@Setter
@NoArgsConstructor
public class FoodCategory extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Donation> donations = new ArrayList<>();

    public FoodCategory(String name) {
        this.name = name;
    }
}
