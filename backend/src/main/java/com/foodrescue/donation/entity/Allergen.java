package com.foodrescue.donation.entity;

import com.foodrescue.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Reference data: one of the 14 major food allergens. Linked to donations many-to-many. */
@Entity
@Table(name = "allergens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Allergen extends BaseEntity {

    @Column(nullable = false, unique = true, length = 60)
    private String name;
}
