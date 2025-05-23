package com.Looksy.Backend.repository;

import com.Looksy.Backend.model.Product;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    // Find products by category ID
    List<Product> findByCategoryid(String categoryId);

    // Find products by subcategory ID
    List<Product> findBySubcategoryid(String subcategoryId);

    // Find products by category and subcategory
    List<Product> findByCategoryidAndSubcategoryid(ObjectId categoryid, ObjectId subcategoryid);

    // Find products by status
    List<Product> findByStatus(String status);

    // Find products by sale status
    List<Product> findBySalestatus(String saleStatus);

    // Find products by name (case-insensitive search)
    List<Product> findByProductnameContainingIgnoreCase(String productName);

    // Find products by price range
    List<Product> findByPriceBetween(String minPrice, String maxPrice);

    // Find products by rating greater than or equal to
    List<Product> findByRatingGreaterThanEqual(String rating);

    // Find products by stock greater than
    List<Product> findByStockGreaterThan(String stock);

    // Find active products
    List<Product> findByStatusAndSalestatus(String status, String saleStatus);
}