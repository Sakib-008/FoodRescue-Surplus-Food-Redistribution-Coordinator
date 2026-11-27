package com.foodrescue.donation.service;

import com.foodrescue.common.exception.ApiException;
import com.foodrescue.donation.catalog.AllergenCatalog;
import com.foodrescue.donation.entity.FoodCategory;
import com.foodrescue.donation.repository.DonationRepository;
import com.foodrescue.donation.repository.FoodCategoryRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock FoodCategoryRepository categories;
    @Mock AllergenCatalog allergenCatalog;
    @Mock DonationRepository donations;

    @Test
    void categoryUsedByDonationsCannotBeDeleted() {
        CategoryService service = new CategoryService(categories, allergenCatalog, donations);
        when(categories.findById(4L)).thenReturn(Optional.of(new FoodCategory("Cooked Meals")));
        when(donations.existsByCategory_Id(4L)).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class, () -> service.delete(4L));

        assertEquals("CATEGORY_IN_USE", ex.getCode());
        verify(categories, never()).delete(any());
    }
}
