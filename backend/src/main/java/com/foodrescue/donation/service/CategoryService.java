package com.foodrescue.donation.service;

import com.foodrescue.common.exception.ApiException;
import com.foodrescue.donation.catalog.AllergenCatalog;
import com.foodrescue.donation.dto.DonationDtos.AllergenDto;
import com.foodrescue.donation.dto.DonationDtos.CategoryDto;
import com.foodrescue.donation.dto.DonationDtos.CategoryForm;
import com.foodrescue.donation.entity.FoodCategory;
import com.foodrescue.donation.repository.DonationRepository;
import com.foodrescue.donation.repository.FoodCategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** CRUD for food categories (admin) and the read-only allergen catalogue. */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final FoodCategoryRepository categories;
    private final AllergenCatalog allergenCatalog;   // AllergenCatalogProxy (Proxy pattern)
    private final DonationRepository donations;

    @Transactional(readOnly = true)
    public List<CategoryDto> list() {
        return categories.findAllByOrderByNameAsc().stream().map(CategoryDto::from).toList();
    }

    @Transactional(readOnly = true)
    public CategoryDto get(Long id) {
        return CategoryDto.from(find(id));
    }

    @Transactional
    public CategoryDto create(CategoryForm form) {
        String name = form.name().trim();
        if (categories.existsByNameIgnoreCase(name)) {
            throw ApiException.conflict("CATEGORY_EXISTS", "A category named '" + name + "' already exists");
        }
        FoodCategory saved = categories.save(new FoodCategory(name));
        return CategoryDto.from(saved);
    }

    @Transactional
    public CategoryDto rename(Long id, CategoryForm form) {
        FoodCategory category = find(id);
        String name = form.name().trim();
        if (categories.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw ApiException.conflict("CATEGORY_EXISTS", "A category named '" + name + "' already exists");
        }
        category.setName(name);
        categories.save(category);
        return CategoryDto.from(category);
    }

    /** A category that is used by any donation cannot be deleted (rename it instead). */
    @Transactional
    public void delete(Long id) {
        FoodCategory category = find(id);
        if (donations.existsByCategory_Id(id)) {
            throw ApiException.conflict("CATEGORY_IN_USE", "Category '" + category.getName() + "' is used by donations and cannot be deleted");
        }
        categories.delete(category);
    }

    public List<AllergenDto> allergens() {
        return allergenCatalog.list();
    }

    private FoodCategory find(Long id) {
        return categories.findById(id).orElseThrow(() -> ApiException.notFound("Category"));
    }
}
