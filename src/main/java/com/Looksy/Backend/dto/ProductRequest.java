package com.Looksy.Backend.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProductRequest {
    private String productname;
    private String offerprice;
    private String stock;
    private String description;
    private String rating;
    private String status;
    private String salestatus;
    private String price;
    private String categoryId;
    private String subCategoryId;
    private MultipartFile icon;
}
