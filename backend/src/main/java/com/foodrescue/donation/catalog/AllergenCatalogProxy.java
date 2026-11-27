package com.foodrescue.donation.catalog;

import com.foodrescue.donation.dto.DonationDtos.AllergenDto;
import com.foodrescue.donation.repository.AllergenRepository;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Proxy pattern (as in the lab's {@code ProxyImage}): stands in for {@link RealAllergenCatalog} and controls
 * access to it. The real catalog (one database query) is created only on the first call; every later call is
 * answered from it without touching the database.
 *
 * <p>Why here: the 14 allergens are fixed reference data (inserted by migration V21, no API changes them), but
 * the donation form asks for them every time it opens.
 */
@Component
public class AllergenCatalogProxy implements AllergenCatalog {

    private final AllergenRepository repository;
    private RealAllergenCatalog realCatalog;

    public AllergenCatalogProxy(AllergenRepository repository) {
        this.repository = repository;
    }

    @Override
    public synchronized List<AllergenDto> list() {
        if (realCatalog == null) {
            realCatalog = new RealAllergenCatalog(repository);
        }
        return realCatalog.list();
    }
}
