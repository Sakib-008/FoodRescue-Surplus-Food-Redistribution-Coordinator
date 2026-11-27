package com.foodrescue.donation.catalog;

import com.foodrescue.donation.entity.Allergen;
import com.foodrescue.donation.repository.AllergenRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/** Proxy pattern: the real catalog is loaded lazily, once, on the first request. */
@ExtendWith(MockitoExtension.class)
class AllergenCatalogProxyTest {

    @Mock AllergenRepository repository;

    @Test
    void databaseIsQueriedOnlyOnTheFirstCall() {
        when(repository.findAllByOrderByNameAsc()).thenReturn(List.of(new Allergen("Milk"), new Allergen("Peanuts")));
        AllergenCatalog catalog = new AllergenCatalogProxy(repository);
        verifyNoInteractions(repository);                       // nothing loaded yet

        assertEquals(2, catalog.list().size());                 // loads from the database
        assertEquals("Milk", catalog.list().get(0).name());     // served by the already-loaded real catalog

        verify(repository, times(1)).findAllByOrderByNameAsc();
    }
}
