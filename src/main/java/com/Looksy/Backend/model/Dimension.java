package com.Looksy.Backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;

@Document(collection = "dimension")
public class Dimension {
    @Id
    private ObjectId dimensionid;  // formerly sizeid
    private ObjectId categoryid;
    private ObjectId subcategoryid;
    private ObjectId productid;
    private String dimension;

    public Dimension(ObjectId categoryid, ObjectId subcategoryid, ObjectId productid, String dimension) {
        this.categoryid = categoryid;
        this.subcategoryid = subcategoryid;
        this.productid = productid;
        this.dimension = dimension;
    }

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
