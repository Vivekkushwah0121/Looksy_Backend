package com.Looksy.Backend.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // Added for numerical fields

@Document(collection = "products")
public class Product {
    @Id
    private String id;

    @NotNull // Changed from @NotBlank for ObjectId
    private ObjectId categoryid;

    @NotNull // Changed from @NotBlank for ObjectId
    private ObjectId subcategoryid;

    @NotBlank
    private String productname;

    @NotNull // Changed to NotNull for Double
    private Double price; // Changed to Double

    private Double offerprice; // Changed to Double, can be null if no offer

    @NotNull // Changed to NotNull for Integer
    private Integer stock; // Changed to Integer

    @NotBlank
    private String description;

    @NotNull // Changed to NotNull for Double
    private Double rating; // Changed to Double

    @NotBlank
    private String status;

    @NotBlank
    private String salestatus;

    private String picture; // For multiple images (comma-separated)

    private String icon;    // Single icon file

    // Constructors
    public Product() {}

    public Product(ObjectId categoryid, ObjectId subcategoryid, String productname,
                   Double price, Double offerprice, Integer stock, String description,
                   Double rating, String status, String salestatus) {
        this.categoryid = categoryid;
        this.subcategoryid = subcategoryid;
        this.productname = productname;
        this.price = price;
        this.offerprice = offerprice;
        this.stock = stock;
        this.description = description;
        this.rating = rating;
        this.status = status;
        this.salestatus = salestatus;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ObjectId getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(ObjectId categoryid) {
        this.categoryid = categoryid;
    }

    public ObjectId getSubcategoryid() {
        return subcategoryid;
    }

    public void setSubcategoryid(ObjectId subcategoryid) {
        this.subcategoryid = subcategoryid;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public Double getPrice() { // Changed return type
        return price;
    }

    public void setPrice(Double price) { // Changed parameter type
        this.price = price;
    }

    public Double getOfferprice() { // Changed return type
        return offerprice;
    }

    public void setOfferprice(Double offerprice) { // Changed parameter type
        this.offerprice = offerprice;
    }

    public Integer getStock() { // Changed return type
        return stock;
    }

    public void setStock(Integer stock) { // Changed parameter type
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() { // Changed return type
        return rating;
    }

    public void setRating(Double rating) { // Changed parameter type
        this.rating = rating;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSalestatus() {
        return salestatus;
    }

    public void setSalestatus(String salestatus) {
        this.salestatus = salestatus;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}