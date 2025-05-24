package com.Looksy.Backend.dto;

import org.bson.types.ObjectId;

public class DimensionDTO {
    private ObjectId dimensionid;
    private ObjectId categoryid;
    private ObjectId subcategoryid;
    private ObjectId productid;
    private String dimension;

    // Getters and Setters
    public ObjectId getDimensionid() {
        return dimensionid;
    }

    public void setDimensionid(ObjectId dimensionid) {
        this.dimensionid = dimensionid;
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

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }
}
