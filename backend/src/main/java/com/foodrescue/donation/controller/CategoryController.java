package com.foodrescue.donation.controller;

import com.foodrescue.common.response.ApiResponse;
import com.foodrescue.donation.dto.DonationDtos.AllergenDto;
import com.foodrescue.donation.dto.DonationDtos.CategoryDto;
import com.foodrescue.donation.dto.DonationDtos.CategoryForm;
import com.foodrescue.donation.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Food categories: everyone signed in may read, only ADMIN may create, rename or delete. */
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/api/categories")
    public ApiResponse<List<CategoryDto>> list() {
        return ApiResponse.ok(categoryService.list());
    }

    @GetMapping("/api/categories/{id}")
    public ApiResponse<CategoryDto> get(@PathVariable Long id) {
        return ApiResponse.ok(categoryService.get(id));
    }

    @PostMapping("/api/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDto>> create(@Valid @RequestBody CategoryForm form) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(categoryService.create(form)));
    }

    @PutMapping("/api/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryDto> rename(@PathVariable Long id, @Valid @RequestBody CategoryForm form) {
        return ApiResponse.ok(categoryService.rename(id, form));
    }

    @DeleteMapping("/api/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/allergens")
    public ApiResponse<List<AllergenDto>> allergens() {
        return ApiResponse.ok(categoryService.allergens());
    }
}
