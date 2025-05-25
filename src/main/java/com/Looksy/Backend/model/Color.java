package com.Looksy.Backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;

@Document(collection = "Color")
public class Color {
    @Id
    private ObjectId colorid;
    private ObjectId categoryid;
    private ObjectId subcategoryid;
    private ObjectId productid;
    private ObjectId sizeid;
    private String size;
    private String color;

    // Getters and Setters
    public ObjectId getColorid() {
        return colorid;
    }

    public void setColorid(ObjectId colorid) {
        this.colorid = colorid;
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

    public ObjectId getProductid() {
        return productid;
    }

    public void setProductid(ObjectId productid) {
        this.productid = productid;
    }

    public ObjectId getSizeid() {
        return sizeid;
    }

    public void setSizeid(ObjectId sizeid) {
        this.sizeid = sizeid;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
