package com.foodrescue.donation.catalog;

import com.foodrescue.donation.dto.DonationDtos.AllergenDto;
import java.util.List;

/** Proxy pattern - the common interface of the real object and its proxy (the lab's {@code Image}). */
public interface AllergenCatalog {
    List<AllergenDto> list();
}
