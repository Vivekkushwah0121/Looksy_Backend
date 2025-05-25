package com.Looksy.Backend.service;

import com.Looksy.Backend.model.Product;
import com.Looksy.Backend.repository.ProductRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    // Add new product
    public Product addProduct(Product product) {
        try {
            return productRepository.save(product);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save product: " + e.getMessage());
        }
    }



    public List<Product> getProductsBySaleStatus(String saleStatus) {
        try {
            return productRepository.findBySalestatus(saleStatus);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch products by sale status: " + e.getMessage());
        }
    }

    // Get all products
    public List<Product> getAllProducts() {
        try {
            return productRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch products: " + e.getMessage());
        }
    }

    // Get product by ID
    public Product getProductById(String id) {
        try {
            Optional<Product> product = productRepository.findById(id);
            return product.orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch product: " + e.getMessage());
        }
    }

    // Update product
    public Product updateProduct(Product product) {
        try {
            if (product.getId() == null || !productRepository.existsById(product.getId())) {
                throw new RuntimeException("Product not found for update");
            }
            return productRepository.save(product);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update product: " + e.getMessage());
        }
    }

    // Delete product
    public void deleteProduct(String id) {
        try {
            if (!productRepository.existsById(id)) {
                throw new RuntimeException("Product not found with id: " + id);
            }
            productRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete product: " + e.getMessage());
        }
    }


// In ProductService.java
    public List<Product> getProductsByCategoryAndSubcategory(String categoryId, String subcategoryId) {
        try {
            ObjectId categoryObjectId = new ObjectId(categoryId);
            ObjectId subcategoryObjectId = new ObjectId(subcategoryId);
            return productRepository.findByCategoryidAndSubcategoryid(categoryObjectId, subcategoryObjectId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch products by category and subcategory: " + e.getMessage());
        }
    }


    // Get products by category only
    public List<Product> getProductsByCategory(String categoryId) {
        try {
            return productRepository.findByCategoryid(categoryId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch products by category: " + e.getMessage());
        }
    }

    // Get products by status
    public List<Product> getProductsByStatus(String status) {
        try {
            return productRepository.findByStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch products by status: " + e.getMessage());
        }
    }

    public List<Product> searchProductsByName(String productName) {
        try {
            return productRepository.findByProductnameContainingIgnoreCase(productName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to search products: " + e.getMessage());
        }
    }
}