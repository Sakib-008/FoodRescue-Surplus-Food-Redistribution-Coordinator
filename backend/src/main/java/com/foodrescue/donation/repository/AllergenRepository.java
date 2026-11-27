package com.foodrescue.donation.repository;

import com.foodrescue.donation.entity.Allergen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AllergenRepository extends JpaRepository<Allergen, Long> {
    List<Allergen> findAllByOrderByNameAsc();
}
