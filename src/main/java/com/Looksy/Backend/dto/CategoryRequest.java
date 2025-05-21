package com.Looksy.Backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    private String categoryname;

    private String oldpic; // optional, used for icon update

    private String categoryid; // optional, used for update/delete operations

    // For file uploads, validation is usually done in controller/service layer
    // But you can check if it is null there
    private MultipartFile icon;

    // Getters and Setters
    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public MultipartFile getIcon() {
        return icon;
    }

    public void setIcon(MultipartFile icon) {
        this.icon = icon;
    }

    public String getOldpic() {
        return oldpic;
    }

    public void setOldpic(String oldpic) {
        this.oldpic = oldpic;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }
}
