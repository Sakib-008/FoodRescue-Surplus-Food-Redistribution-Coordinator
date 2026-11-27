package com.foodrescue.donation.repository;

import com.foodrescue.donation.entity.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Long> {
    List<FoodCategory> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
