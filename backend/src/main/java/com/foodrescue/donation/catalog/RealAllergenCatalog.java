package com.foodrescue.donation.catalog;

import com.foodrescue.donation.dto.DonationDtos.AllergenDto;
import com.foodrescue.donation.repository.AllergenRepository;
import java.util.List;

/** Proxy pattern - the real object (the lab's {@code RealImage}): loads the allergen list from the database when created. */
public class RealAllergenCatalog implements AllergenCatalog {

    private final List<AllergenDto> allergens;

    public RealAllergenCatalog(AllergenRepository repository) {
        this.allergens = loadFromDatabase(repository);
    }

    @Override
    public List<AllergenDto> list() {
        return allergens;
    }

    private List<AllergenDto> loadFromDatabase(AllergenRepository repository) {
        return repository.findAllByOrderByNameAsc().stream().map(AllergenDto::from).toList();
    }
}
