package com.Looksy.Backend.controller;

import com.Looksy.Backend.dto.CategoryRequest;
import com.Looksy.Backend.model.Category;
import com.Looksy.Backend.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/add_new_category")
    public ResponseEntity<?> addNewCategory(@Valid CategoryRequest request, BindingResult result) {
        if (result.hasErrors()) {
            // Return first validation error message
            String errorMessage = result.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", errorMessage));
        }
        MultipartFile icon = request.getIcon();
        if (icon == null || icon.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", "Icon file is required"));
        }

        try {
            Category category = categoryService.addNewCategory(request.getCategoryname(), icon);
            return ResponseEntity.ok(Map.of("status", true, "message", "Category added successfully", "category", category));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("status", false, "message", "Failed to upload image"));
        }
    }

    @GetMapping("/display_all_category")
    public ResponseEntity<?> displayAllCategory() {
        return ResponseEntity.ok(Map.of("data", categoryService.getAllCategories()));
    }

    @PostMapping("/edit_category_data")
    public ResponseEntity<?> editCategoryData(@Valid @RequestBody CategoryRequest request, BindingResult result) {
        if (result.hasErrors() || request.getCategoryid() == null || request.getCategoryid().isEmpty()) {
            String errorMessage = result.hasErrors() ? result.getFieldError().getDefaultMessage() : "Category ID is required";
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", errorMessage));
        }

        try {
            Category updated = categoryService.updateCategoryName(request.getCategoryid(), request.getCategoryname());
            return ResponseEntity.ok(Map.of("status", true, "message", "Category updated successfully", "category", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("status", false, "message", "Update failed"));
        }
    }

    @PostMapping("/delete_category_data")
    public ResponseEntity<?> deleteCategoryData(@RequestBody Map<String, String> payload) {
        String categoryid = payload.get("categoryid");
        if (categoryid == null || categoryid.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", "Category ID is required"));
        }
        try {
            categoryService.deleteCategory(categoryid);
            return ResponseEntity.ok(Map.of("status", true, "message", "Category deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("status", false, "message", "Deletion failed"));
        }
    }

    @PostMapping("/update_icon")
    public ResponseEntity<?> updateIcon(@Valid CategoryRequest request, BindingResult result) {
        if (result.hasErrors() || request.getCategoryid() == null || request.getCategoryid().isEmpty()) {
            String errorMessage = result.hasErrors() ? result.getFieldError().getDefaultMessage() : "Category ID is required";
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", errorMessage));
        }

        MultipartFile icon = request.getIcon();
        if (icon == null || icon.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", "Icon file is required"));
        }

        try {
            Category updated = categoryService.updateCategoryIcon(request.getCategoryid(), icon, request.getOldpic());
            return ResponseEntity.ok(Map.of("status", true, "message", "Icon updated successfully", "category", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("status", false, "message", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("status", false, "message", "Icon update failed"));
        }
    }
}
