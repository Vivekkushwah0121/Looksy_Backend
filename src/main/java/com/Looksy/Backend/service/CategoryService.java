package com.Looksy.Backend.service;

import com.Looksy.Backend.model.Category;
import com.Looksy.Backend.repository.CategoryRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private Cloudinary cloudinary;

    public Category addNewCategory(String categoryname, MultipartFile icon) throws IOException {
        if (icon == null || icon.isEmpty()) {
            throw new IllegalArgumentException("No image uploaded");
        }

        Map uploadResult = cloudinary.uploader().upload(icon.getBytes(), ObjectUtils.emptyMap());
        String publicId = (String) uploadResult.get("public_id");
        String url = (String) uploadResult.get("secure_url");

        Category category = new Category(categoryname, publicId);
        Category savedCategory = categoryRepository.save(category);

        logger.info("Category added with ID: {}, Name: {}", savedCategory.getCategoryid(), categoryname);
        return savedCategory;
    }

    public Iterable<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category updateCategoryName(String categoryid, String newName) {
        Category category = categoryRepository.findById(categoryid)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryid));

        category.setCategoryname(newName);
        Category updated = categoryRepository.save(category);

        logger.info("Category name updated for ID: {} to {}", categoryid, newName);
        return updated;
    }

    public void deleteCategory(String categoryid) throws IOException {
        Category category = categoryRepository.findById(categoryid)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryid));

        if (category.getIcon() != null && !category.getIcon().isEmpty()) {
            cloudinary.uploader().destroy(category.getIcon(), ObjectUtils.emptyMap());
            logger.info("Deleted image from Cloudinary with publicId: {}", category.getIcon());
        }

        categoryRepository.deleteById(categoryid);
        logger.info("Category deleted with ID: {}", categoryid);
    }

    public Category updateCategoryIcon(String categoryid, MultipartFile newIcon, String oldIcon) throws IOException {
        Category category = categoryRepository.findById(categoryid)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryid));

        if (newIcon == null || newIcon.isEmpty()) {
            throw new IllegalArgumentException("No new image uploaded");
        }

        if (oldIcon != null && !oldIcon.isEmpty()) {
            cloudinary.uploader().destroy(oldIcon, ObjectUtils.emptyMap());
            logger.info("Deleted old icon from Cloudinary with publicId: {}", oldIcon);
        }

        Map uploadResult = cloudinary.uploader().upload(newIcon.getBytes(), ObjectUtils.emptyMap());
        String newPublicId = (String) uploadResult.get("public_id");

        category.setIcon(newPublicId);
        Category updated = categoryRepository.save(category);

        logger.info("Category icon updated for ID: {}", categoryid);
        return updated;
    }
}
